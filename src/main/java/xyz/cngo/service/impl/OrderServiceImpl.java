package xyz.cngo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.utils.GenerateUtil;
import xyz.cngo.dao.OrderMapper;
import xyz.cngo.entity.OrderEntity;
import xyz.cngo.model.OrderModel;
import xyz.cngo.model.ProductModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.BalanceService;
import xyz.cngo.service.OrderService;
import xyz.cngo.service.ProductService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BalanceService balanceService;
    @Resource
    private ProductService productService;

    /**
     * 查询单个订单服务
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Override
    public OrderModel getOrder(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = getAndVerifyOrderEntity(userModel, orderId);
        return OrderModel.convertFromEntity(orderEntity);
    }

    /**
     * 查询用户的所有订单
     * @param userModel
     * @return
     */
    @Override
    public List<OrderModel> getUserOrders(UserModel userModel) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.orderByDesc("created_at");
        List<OrderEntity> orderEntityList = orderMapper.selectList(queryWrapper);
        return orderEntityList.stream()
                .map(OrderModel::convertFromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 创建订单
     * @param userModel
     * @param productId
     * @param quantity
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public OrderModel createOrder(UserModel userModel, Integer productId, Integer quantity) throws BusinessException {
        // 有未支付订单时禁止创建订单
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.eq("status", "pending");
        Long count = orderMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new BusinessException(EmBusinessError.ORDER_PENDING_PAYMENT);
        }

        // 查询商品
        ProductModel productModel = productService.getProductById(productId);
        if(Objects.isNull(productModel)){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_EXIST);
        }
        if(productModel.getStatus().equals("inactive")){
            throw new BusinessException(EmBusinessError.PRODUCT_OFF_SHELF);
        }

        // 生成订单号
        String orderId = GenerateUtil.generateOrderSerialNumber();
        // 插入订单
        BigDecimal totalPrice = productModel.getPrice().multiply(new BigDecimal(quantity));
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setUserId(userModel.getUserId());
        orderEntity.setProductId(productModel.getProductId());
        orderEntity.setQuantity(quantity);
        orderEntity.setOriginalPrice(productModel.getPrice());
        orderEntity.setPromotionPrice(null);
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setStatus("pending");

        orderMapper.insert(orderEntity);
        return OrderModel.convertFromEntity(orderEntity);
    }

    /**
     * 支付订单
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public OrderModel payOrder(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = getAndVerifyOrderEntity(userModel, orderId);
        if(!orderEntity.getStatus().equals("pending")){
            throw new BusinessException(EmBusinessError.ORDER_ALREADY_PAID);
        }
        OrderEntity payedOrderEntity = payOrderEntity(userModel, orderEntity);
        orderMapper.updateById(payedOrderEntity);
        return OrderModel.convertFromEntity(payedOrderEntity);
    }

    /**
     * 取消订单，只有在未支付的状态下
     *
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public OrderModel cancelOrder(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = getAndVerifyOrderEntity(userModel, orderId);
        switch(orderEntity.getStatus()){
            case "pending":
                break;
            case "cancelled":
                throw new BusinessException(EmBusinessError.ORDER_ALREADY_CANCELLED);
            default:
                throw new BusinessException(EmBusinessError.ORDER_CANNOT_CANCEL);
        }

        orderEntity.setStatus("cancelled");
        orderMapper.updateById(orderEntity);

        return OrderModel.convertFromEntity(orderEntity);
    }

    /**
     * 订单退款，在订单支付后
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public OrderModel refundOrder(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = getAndVerifyOrderEntity(userModel, orderId);
        switch(orderEntity.getStatus()){
            case "pending":
            case "completed":
            case "cancelled":
                throw new BusinessException(EmBusinessError.ORDER_CANNOT_REFUND);
            case "refunded":
                throw new BusinessException(EmBusinessError.ORDER_ALREADY_REFUNDED);
        }

        // 退款
        balanceService.refundBalance(userModel, OrderModel.convertFromEntity(orderEntity));

        // 更新商品的库存和销量
        productService.addProductStock(
                orderEntity.getProductId(),
                orderEntity.getQuantity(),
                String.format("订单[%s]取消返回库存", orderEntity.getOrderId()),
                orderEntity.getOrderId());

        orderEntity.setStatus("refunded");
        orderMapper.updateById(orderEntity);

        return OrderModel.convertFromEntity(orderEntity);
    }

    /**
     * 更新订单状态
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public OrderModel updateOrderStatus(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = getAndVerifyOrderEntity(userModel, orderId);
        switch (orderEntity.getStatus()){
            case "paid":
                orderEntity.setStatus("shipped");
                break;
            case "shipped":
                orderEntity.setStatus("received");
                break;
            case "received":
                orderEntity.setStatus("completed");
                break;
            default:
                throw new BusinessException(EmBusinessError.ORDER_STATUS_ABNORMAL);
        }
        orderMapper.updateById(orderEntity);
        return OrderModel.convertFromEntity(orderEntity);
    }

    /**
     * 私有方法，根据id查找订单并且验证是否属于用户
     * @param userModel
     * @param orderId
     * @return
     * @throws BusinessException
     */
    private OrderEntity getAndVerifyOrderEntity(UserModel userModel, String orderId) throws BusinessException {
        OrderEntity orderEntity = orderMapper.selectById(orderId);
        if(Objects.isNull(orderEntity)){
            throw new BusinessException(EmBusinessError.ORDER_NOT_FOUND);
        }
        if(!orderEntity.getUserId().equals(userModel.getUserId())){
            throw new BusinessException(EmBusinessError.ORDER_NOT_OWNED_BY_USER);
        }
        return orderEntity;
    }

    /**
     * 创建新的订单实体并进行相应检查，然后返回
     * @param userModel
     * @param productId
     * @param quantity
     * @return
     * @throws BusinessException
     */
    private OrderEntity createOrderEntity(UserModel userModel, Integer productId, Integer quantity) throws BusinessException {
        return null;
    }

    /**
     * 对未支付的订单实体进行扣减库存和支付的操作，然后返回实体
     * @param userModel
     * @param orderEntity
     * @return
     * @throws BusinessException
     */
    private OrderEntity payOrderEntity(UserModel userModel, OrderEntity orderEntity) throws BusinessException {
        // 尝试减少商品库存，若库存不足，内部会报错
        productService.reduceProductStock(
                orderEntity.getProductId(),
                orderEntity.getQuantity(),
                "用户购买"+orderEntity.getQuantity()+"件商品",
                orderEntity.getOrderId());

        balanceService.consumeBalance(userModel, OrderModel.convertFromEntity(orderEntity));
        orderEntity.setStatus("paid");

        return orderEntity;
    }
}
