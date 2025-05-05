package xyz.cngo.controller;

import org.springframework.web.bind.annotation.*;
import xyz.cngo.common.checker.ParamCheck;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.response.CommonReturnType;
import xyz.cngo.model.OrderModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.OrderService;
import xyz.cngo.service.UserService;
import xyz.cngo.viewobject.OrderVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController("OrderController")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class OrderController extends BaseController {
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;

    @GetMapping("/get")
    public CommonReturnType getOrder(
            @RequestParam("orderId") @ParamCheck(name = "订单ID", minLength = 20, maxLength = 20) String orderId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.getOrder(userModel, orderId);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }

    @GetMapping
    public CommonReturnType getUserOrders() throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        List<OrderModel> orderModelList =  orderService.getUserOrders(userModel);
        List<OrderVO> orderVOList = orderModelList.stream()
                .map(OrderVO::convertFromModel)
                .collect(Collectors.toList());
        return CommonReturnType.create(orderVOList);
    }

    @PostMapping("/create")
    public CommonReturnType createOrder(
            @RequestParam("productId") @ParamCheck(name = "商品ID", min = 1) Integer productId,
            @RequestParam("quantity") @ParamCheck(name = "购买数量", min = 1) Integer quantity
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.createOrder(userModel, productId, quantity);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }

    @PutMapping("/pay")
    public CommonReturnType payOrder(
            @RequestParam("orderId") @ParamCheck(name = "订单ID", minLength = 20, maxLength = 20) String orderId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.payOrder(userModel, orderId);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }

    @PutMapping("/update")
    public CommonReturnType updateOrder(
            @RequestParam("orderId") @ParamCheck(name = "订单ID", minLength = 20, maxLength = 20) String orderId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.updateOrderStatus(userModel, orderId);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }

    @PutMapping("/cancel")
    public CommonReturnType cancelOrder(
            @RequestParam("orderId") @ParamCheck(name = "订单ID", minLength = 20, maxLength = 20) String orderId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.cancelOrder(userModel, orderId);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }

    @PutMapping("/refund")
    public CommonReturnType refundOrder(
            @RequestParam("orderId") @ParamCheck(name = "订单ID", minLength = 20, maxLength = 20) String orderId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        OrderModel orderModel = orderService.refundOrder(userModel, orderId);
        return CommonReturnType.create(OrderVO.convertFromModel(orderModel));
    }
}
