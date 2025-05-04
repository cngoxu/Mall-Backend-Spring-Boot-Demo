package xyz.cngo.model;

import org.springframework.beans.BeanUtils;
import xyz.cngo.entity.ProductStockEntity;

import java.util.Date;

public class ProductStockModel {
    private Integer productId;

    private Integer stock;

    private Date updatedAt;

    public static ProductStockModel convertFromEntity(ProductStockEntity productStockEntity) {
        ProductStockModel productStockModel = new ProductStockModel();
        BeanUtils.copyProperties(productStockEntity, productStockModel);
        return productStockModel;
    }
}
