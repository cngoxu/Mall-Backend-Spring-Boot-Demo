package xyz.cngo.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.ProductModel;

@Data
@TableName("t_product_stock")
public class ProductStockEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer productId;

    private Integer stock;

//    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    public static ProductStockEntity convertFromModel(ProductModel productModel) {
        ProductStockEntity productStockEntity = new ProductStockEntity();
        BeanUtils.copyProperties(productModel, productStockEntity);
        return productStockEntity;
    }
}
