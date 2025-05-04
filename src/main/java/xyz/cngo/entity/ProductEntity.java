package xyz.cngo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.ProductModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_product")
public class ProductEntity {
    @TableId(type = IdType.AUTO)
    private Integer productId;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    @TableField(condition = SqlCondition.LIKE)
    private String title;

    private String description;

    private BigDecimal price;

    private String images;

    private String status;

//    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    private Integer sales;

    // 枚举类型
    public enum Status {
        ACTIVE, INACTIVE
    }

    public static ProductEntity convertFromModel(ProductModel productModel){
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productModel, productEntity);
        return productEntity;
    }
}
