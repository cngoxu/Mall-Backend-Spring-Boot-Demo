package xyz.cngo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.dao.CartMapper;
import xyz.cngo.entity.CartEntity;
import xyz.cngo.model.CartItemModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.CartService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {
    @Resource
    private CartMapper cartMapper;

    @Value("${custom.cart.max_capacity}")
    private Integer maxCapacity;

    /**
     * 获取所有购物车项目
     * @param userModel
     * @return
     */
    @Override
    public List<CartItemModel> getCartItems(UserModel userModel) {
        // 具体逻辑在mapper中用sql语句实现，直接返回Model
        return cartMapper.getUserCartItems(userModel.getUserId());
    }

    /**
     * 增加购物车项目
     * @param userModel
     * @param productId
     * @param quantity
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void addCartItem(UserModel userModel, Integer productId, Integer quantity) throws BusinessException {
        int count = getCartItemCount(userModel);
        if(count >= maxCapacity) {
            throw new BusinessException(EmBusinessError.CART_CAPACITY_FULL);
        }

        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserId(userModel.getUserId());
        cartEntity.setProductId(productId);
        cartEntity.setQuantity(quantity);

        try {
            int result = cartMapper.insert(cartEntity);
            if (result < 0) {
                throw new BusinessException(EmBusinessError.DATABASE_OPERATION_FAILED);
            }
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException(EmBusinessError.PRODUCT_ALREADY_IN_CART);
            } else {
                throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, e.getMessage());
            }
        }
    }

    /**
     * 获取购物车项目总数
     * @param userModel
     * @return
     */
    @Override
    public Integer getCartItemCount(UserModel userModel) {
        QueryWrapper<CartEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        return Math.toIntExact(cartMapper.selectCount(queryWrapper));
    }

    /**
     * 移除购物车项目
     * @param userModel
     * @param productId
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void removeCartItem(UserModel userModel, Integer productId) throws BusinessException {
        QueryWrapper<CartEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.eq("product_id", productId);
        int result = cartMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(EmBusinessError.CART_ITEM_NOT_FOUND);
        }
    }

    /**
     * 更新购物车项目
     * @param userModel
     * @param productId
     * @param quantity
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void updateCartItem(UserModel userModel, Integer productId, Integer quantity) throws BusinessException {
        // 更新为0则移除
        if(quantity <= 0) {
            removeCartItem(userModel, productId);
        }

        QueryWrapper<CartEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.eq("product_id", productId);
        CartEntity cartEntity = cartMapper.selectOne(queryWrapper);

        // 数据库中没记录则创建
        if(Objects.isNull(cartEntity)){
            addCartItem(userModel, productId, quantity);
        }else{
            cartEntity.setQuantity(quantity);
            // 更新数据
            cartMapper.update(cartEntity, queryWrapper);
        }
    }
}
