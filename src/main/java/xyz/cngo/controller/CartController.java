package xyz.cngo.controller;


import org.springframework.web.bind.annotation.*;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.response.CommonReturnType;
import xyz.cngo.model.CartItemModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.CartService;
import xyz.cngo.service.UserService;
import xyz.cngo.viewobject.CartItemVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController("CartController")
@RequestMapping("/cart")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class CartController extends BaseController{
    @Resource
    UserService userService;
    @Resource
    CartService cartService;

    @GetMapping
    public CommonReturnType getCartItems() throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        List<CartItemModel> cartItemModelList = cartService.getCartItems(userModel);
        List<CartItemVO> cartItemVOList = cartItemModelList.stream()
                .map(CartItemVO::convertFromModel)
                .collect(Collectors.toList());

        return CommonReturnType.create(cartItemVOList);
    }

    /**
     * 添加商品到购物车
     * @param productId
     * @param quantity
     * @return
     * @throws BusinessException
     */
    @PostMapping("/add")
    public CommonReturnType addCartItem(
            @RequestParam(name="productId") Integer productId,
            @RequestParam(name="quantity") Integer quantity
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        cartService.addCartItem(userModel, productId, quantity);
        return CommonReturnType.create(true);
    }

    /**
     * 移除购物车单个商品
     * @param productId
     * @return
     * @throws BusinessException
     */
    @DeleteMapping("/remove")
    public CommonReturnType removeCartItem(
            @RequestParam("productId") Integer productId
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        cartService.removeCartItem(userModel, productId);
        return CommonReturnType.create(true);
    }

    @DeleteMapping("/removeList")
    public CommonReturnType removeCartItems(
            @RequestParam("productIds[]") List<Integer> productIds
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        for (Integer productId : productIds) {
            cartService.removeCartItem(userModel, productId);
        }
        return CommonReturnType.create(true);
    }

    @PutMapping("/update")
    public CommonReturnType updateCartItem(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        cartService.updateCartItem(userModel, productId, quantity);
        return CommonReturnType.create(true);
    }
}
