package xyz.cngo.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderModel {
    private String orderId;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private BigDecimal originalPrice;

    private BigDecimal promotionPrice;

    private BigDecimal totalPrice;

    private String status;

    private Date createdAt;

    private Date updatedAt;

    public static OrderModel convertFromEntity(OrderEntity orderEntity) {
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderEntity, orderModel);
        return orderModel;
    }
}
