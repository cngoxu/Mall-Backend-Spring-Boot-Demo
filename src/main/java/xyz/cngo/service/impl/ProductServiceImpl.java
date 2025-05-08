package xyz.cngo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.utils.GenerateUtil;
import xyz.cngo.dao.ProductMapper;
import xyz.cngo.dao.ProductStockLogMapper;
import xyz.cngo.dao.ProductStockMapper;
import xyz.cngo.dto.ProductUpdateInfoDTO;
import xyz.cngo.entity.ProductEntity;
import xyz.cngo.entity.ProductStockEntity;
import xyz.cngo.entity.ProductStockLogEntity;
import xyz.cngo.model.ProductModel;
import xyz.cngo.model.ProductStockModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.ProductService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductStockMapper productStockMapper;
    @Resource
    private ProductStockLogMapper productStockLogMapper;

    /**
     * 获取所有商品列表
     * @return
     */
    @Override
    public List<ProductModel> getAllProducts() {
        return productMapper.selectAllProductModels();
    }

    /**
     * 获取用户商品列表
     * @param userModel
     * @return
     */
    @Override
    public List<ProductModel> getUserProducts(UserModel userModel) {
        return productMapper.selectProductModelsByUserId(userModel.getUserId());
    }

    /**
     * 分页获取上架商品列表
     * @param page
     * @param pageSize
     * @param sort 排序的方法
     * @return
     */
    @Override
    public List<ProductModel> getProductsByPage(Integer page, Integer pageSize, String sort) {
        int start = (page - 1) * pageSize;

        Map<String, String> map = Map.of("newest", "created_at",
                "price", "price",
                "sales", "sales");

        return productMapper.selectProductModelsByPage(start, pageSize, map.get(sort));
    }

    /**
     * 获取上架商品的总数量
     * @return
     */
    @Override
    public Integer countActiveProducts() {
        QueryWrapper<ProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        return Math.toIntExact(productMapper.selectCount(queryWrapper));
    }

    /**
     * 根据id获取商品模型（详情）
     * @param productId
     * @return
     */
    @Override
    public ProductModel getProductById(Integer productId) throws BusinessException {
        ProductEntity productEntity = productMapper.selectById(productId);
        if(Objects.isNull(productEntity)){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_EXIST);
        }
        ProductStockEntity productStockEntity = getProductStockEntityById(productId);
        return ProductModel.convertFromEntity(productEntity, productStockEntity);
    }

    /**
     * 删除商品服务
     * @param userModel
     * @param productId
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void deleteProduct(UserModel userModel, Integer productId) throws BusinessException {
        // 删除商品
        QueryWrapper<ProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.eq("product_id", productId);
        int result = productMapper.delete(queryWrapper);

        //验证该商品是否存在且属于这个用户
        if(result == 0){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_OWNED_BY_USER);
        }

        /* 外键约束会自动删除

        // 删除库存表
        QueryWrapper<ProductStockEntity> qwStock = new QueryWrapper<>();
        qwStock.eq("product_id", productId);
        productStockMapper.delete(qwStock);

        // 删除库存明细表
        QueryWrapper<ProductStockLogEntity> qwStockLog = new QueryWrapper<>();
        qwStockLog.eq("product_id", productId);
        productStockLogMapper.delete(qwStockLog);
        */
    }

    /**
     * 上下架商品服务
     * @param userModel
     * @param productId
     * @param status
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public String publishProduct(UserModel userModel, Integer productId, String status) throws BusinessException {
        ProductEntity entity = verifyProductOwnership(userModel, productId);
        entity.setStatus(status);
        productMapper.updateById(entity);
        return status;
    }

    /**
     * 创建商品服务
     * @param productModel
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public ProductModel createProduct(ProductModel productModel) throws BusinessException {
        // 插入商品信息
        ProductEntity productEntity = ProductEntity.convertFromModel(productModel);
        productMapper.insert(productEntity);

        // 插入商品库存
        productModel.setProductId(productEntity.getProductId());
        ProductStockEntity productStockEntity = ProductStockEntity.convertFromModel(productModel);
        productStockMapper.insert(productStockEntity);

        // 插入商品库存明细
        insertProductStockLog(productEntity.getProductId(),
                productStockEntity.getStock(),
                productStockEntity.getStock(),
                "set",
                null,
                String.format("商品%s初始库存", productModel.getTitle()));

        return ProductModel.convertFromEntity(productEntity, productStockEntity);
    }

    /**
     * 更新商品信息服务函数，这里不会更新库存
     * 不允许更改所属的用户！！！
     * @param userModel
     * @param dto
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void updateProduct(UserModel userModel, ProductUpdateInfoDTO dto) throws BusinessException {
        // 先验证该商品是否属于用户
        ProductEntity productEntity = verifyProductOwnership(userModel, dto.getProductId());

        // 更新商品信息
        productEntity.setDescription(dto.getDescription());
        productEntity.setTitle(dto.getTitle());
        productEntity.setPrice(dto.getPrice());
        productEntity.setImages(dto.getImages());
        productEntity.setStatus(dto.getStatus());
        productMapper.updateById(productEntity);
    }

    /**
     * 用户更新商品库存服务
     * @param userModel
     * @param productId
     * @param newStock
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void updateProductStock(UserModel userModel, Integer productId, Integer newStock) throws BusinessException {
        // 先验证该商品是否属于用户
        ProductEntity productEntity = verifyProductOwnership(userModel, productId);

        ProductStockEntity productStockEntity = new ProductStockEntity();
        productStockEntity.setProductId(productId);
        productStockEntity.setStock(newStock);

        UpdateWrapper<ProductStockEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("product_id", productId);
        productStockMapper.update(productStockEntity, updateWrapper);

        insertProductStockLog(productId, newStock, newStock, "set", null, "用户更新商品库存为" + newStock);
    }

    /**
     * 由于退货返回库存，所以不需要校验用户身份
     * @param productId
     * @param stock
     * @param remark
     * @param orderId
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public ProductStockModel addProductStock(Integer productId, Integer stock, String remark, String orderId) throws BusinessException {
        // 获取商品库存
        QueryWrapper<ProductStockEntity> qw = new QueryWrapper<>();
        qw.eq("product_id", productId);
        ProductStockEntity productStockEntity = productStockMapper.selectOne(qw);

        // 更新商品库存
        if(Objects.isNull(productStockEntity)){
            // 如果商品库存为空
            productStockEntity = new ProductStockEntity();
            productStockEntity.setProductId(productId);
            productStockEntity.setStock(stock);
            productStockMapper.insert(productStockEntity);
        }else{
            productStockEntity.setStock(productStockEntity.getStock() + stock);
            productStockMapper.update(productStockEntity, qw);
        }

        // 是取消订单导致的库存增加，需要减销量
        if(Objects.nonNull(orderId)){
            updateProductSales(productId, -stock);
        }

        // 插入库存明细
        insertProductStockLog(productId, stock, productStockEntity.getStock(), "in", orderId, remark);

        return ProductStockModel.convertFromEntity(productStockEntity);
    }

    /**
     * 根据订单减库存
     * 根据订单减库存，所以不需要验证用户
     * @param productId
     * @param stock
     * @param remark
     * @param orderId
     * @throws BusinessException
     */
    @Transactional
    @Override
    public ProductStockModel reduceProductStock(Integer productId, Integer stock, String remark, String orderId) throws BusinessException {
        // 获取商品库存
        QueryWrapper<ProductStockEntity> qw = new QueryWrapper<>();
        qw.eq("product_id", productId);
        ProductStockEntity productStockEntity = productStockMapper.selectOne(qw);

        // 更新商品库存
        if(Objects.isNull(productStockEntity) || productStockEntity.getStock() < stock){
            throw new BusinessException(EmBusinessError.PRODUCT_STOCK_NOT_ENOUGH);
        }else{
            productStockEntity.setStock(productStockEntity.getStock() - stock);
            productStockMapper.update(productStockEntity, qw);
        }

        // 是下订单导致的库存增加，需要增加销量
        if(Objects.nonNull(orderId)){
            updateProductSales(productId, stock);
        }

        // 插入库存日志
        insertProductStockLog(productId, -stock, productStockEntity.getStock(), "out", orderId, remark);
        return ProductStockModel.convertFromEntity(productStockEntity);
    }

    /**
     * 私有函数，插入库存日志表
     * @param productId
     * @param delta
     * @param newStock
     * @param type
     * @param orderNo
     * @param remark
     * @throws BusinessException
     */
    private void insertProductStockLog(Integer productId, Integer delta, Integer newStock, String type, String orderNo, String remark) throws BusinessException {
        ProductStockLogEntity entity = new ProductStockLogEntity(
                null, productId, delta,
                newStock, type,
                GenerateUtil.generateStockSerialNumber(),
                orderNo, remark, null
        );
        productStockLogMapper.insert(entity);
    }

    /**
     * 私有函数，验证商品是否属于用户
     * @param userModel
     * @param productId
     * @throws BusinessException
     */
    private ProductEntity verifyProductOwnership(UserModel userModel, Integer productId) throws BusinessException {
        ProductEntity productEntity = productMapper.selectById(productId);
        if(Objects.isNull(productEntity)){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_EXIST);
        }
        if(!productEntity.getUserId().equals(userModel.getUserId())){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_OWNED_BY_USER);
        }
        return productEntity;
    }

    /**
     * 私有方法，更新商品销量
     * @param productId
     * @param change
     * @throws BusinessException
     */
    private void updateProductSales(Integer productId, Integer change) throws BusinessException {
        ProductEntity productEntity = productMapper.selectById(productId);
        if(Objects.isNull(productEntity)){
            throw new BusinessException(EmBusinessError.PRODUCT_NOT_EXIST);
        }
        productEntity.setSales(productEntity.getSales() + change);
        productMapper.updateById(productEntity);
    }

    /**
     * 私有方法，根据id获取库存记录
     * @param productId
     * @return
     */
    public ProductStockEntity getProductStockEntityById(Integer productId) {
        QueryWrapper<ProductStockEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        return productStockMapper.selectOne(queryWrapper);
    }
}
