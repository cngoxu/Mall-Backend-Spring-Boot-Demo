package xyz.cngo.service;

import xyz.cngo.common.error.BusinessException;
import xyz.cngo.model.OrderModel;
import xyz.cngo.model.UserModel;

import java.util.List;

public interface OrderService {
    OrderModel getOrder(UserModel userModel, String orderId) throws BusinessException;

    List<OrderModel> getUserOrders(UserModel userModel);

    OrderModel createOrder(UserModel userModel, Integer productId, Integer quantity) throws BusinessException;

    OrderModel payOrder(UserModel userModel, String orderId) throws BusinessException;

    OrderModel cancelOrder(UserModel userModel, String orderId) throws BusinessException;

    OrderModel refundOrder(UserModel userModel, String orderId) throws BusinessException;

    OrderModel updateOrderStatus(UserModel userModel, String orderId) throws BusinessException;
}