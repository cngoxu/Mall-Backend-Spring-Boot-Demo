package xyz.cngo.service;

import xyz.cngo.common.error.BusinessException;
import xyz.cngo.model.CartItemModel;
import xyz.cngo.model.UserModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface CartService {
    List<CartItemModel> getCartItems(UserModel userModel);

    void addCartItem(UserModel userModel, Integer productId, Integer quantity) throws BusinessException;

    Integer getCartItemCount(UserModel userModel);

    void removeCartItem(UserModel userModel, Integer productId) throws BusinessException;

    void updateCartItem(UserModel userModel, Integer productId, Integer quantity) throws BusinessException;
}
