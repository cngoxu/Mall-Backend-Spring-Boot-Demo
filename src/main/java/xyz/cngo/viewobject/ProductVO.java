package xyz.cngo.viewobject;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.ProductModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductVO {
    private Integer productId;

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

    public static ProductVO convertFromModel(ProductModel productModel) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productModel, productVO);
        return productVO;
    }
}
