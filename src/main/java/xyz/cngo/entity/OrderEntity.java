package xyz.cngo.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_order")
public class OrderEntity {
    @TableId
    private String orderId;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer productId;

    private Integer quantity;

    private BigDecimal originalPrice;

    private BigDecimal promotionPrice;

    private BigDecimal totalPrice;

    private String status;

//    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    // 枚举类型
    public enum Status {
        PENDING,
        PAID,
        SHIPPED,
        RECEIVED,
        COMPLETED,
        CANCELLED,
        REFUNDED
    }
}
