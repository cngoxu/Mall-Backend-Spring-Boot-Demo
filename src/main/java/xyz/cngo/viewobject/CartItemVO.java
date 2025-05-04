package xyz.cngo.viewobject;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.CartItemModel;

import java.math.BigDecimal;

@Data
public class CartItemVO {
//    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private String title;

    private String description;

    private BigDecimal price;

    private String images;

    private String status;

    // 库存相关
    private Integer stock;

    public static CartItemVO convertFromModel(CartItemModel cartItemModel) {
        CartItemVO cartItemVO = new CartItemVO();
        BeanUtils.copyProperties(cartItemModel, cartItemVO);
        return cartItemVO;
    }
}
