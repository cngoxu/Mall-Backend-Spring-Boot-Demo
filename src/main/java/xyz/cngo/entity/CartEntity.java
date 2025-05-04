package xyz.cngo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_cart")
public class CartEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer productId;

    private Integer quantity;
}