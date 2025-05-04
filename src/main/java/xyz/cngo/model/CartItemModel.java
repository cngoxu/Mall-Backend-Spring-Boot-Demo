package xyz.cngo.model;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItemModel {
    private Integer id;

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

    private Integer sales;
}
