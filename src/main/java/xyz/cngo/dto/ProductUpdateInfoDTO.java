package xyz.cngo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateInfoDTO {
    private Integer productId;
    private String title;
    private String description;
    private BigDecimal price;
    private String images;
    private String status;
}
