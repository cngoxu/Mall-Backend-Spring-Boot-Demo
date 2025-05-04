package xyz.cngo.viewobject;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.OrderModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    private String orderId;

    private Integer productId;

    private Integer quantity;

    private BigDecimal originalPrice;

    private BigDecimal promotionPrice;

    private BigDecimal totalPrice;

    private String status;

    private Date createdAt;

    private Date updatedAt;

    public static OrderVO convertFromModel(OrderModel orderModel) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderModel, orderVO);
        return orderVO;
    }
}
