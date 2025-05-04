package xyz.cngo.model;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.entity.ProductEntity;
import xyz.cngo.entity.ProductStockEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductModel {
    private Integer productId;

    private Integer userId;

    private String title;

    private String description;

    private BigDecimal price;

    private String images;

    private String status;

    private Date createdAt;

    // 库存相关
    private Integer stock;

    private Date updatedAt;

    private Integer sales;


    public static ProductModel convertFromEntity(ProductEntity productEntity, ProductStockEntity productStockEntity) {
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productEntity, productModel);
        productModel.setStock(productStockEntity.getStock());
        productModel.setUpdatedAt(productStockEntity.getUpdatedAt());
        return productModel;
    }
}
