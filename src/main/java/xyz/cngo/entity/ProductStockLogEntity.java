package xyz.cngo.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_product_stock_log")
public class ProductStockLogEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer productId;

    private Integer amount;

    private Integer stockAfter;

    private String type;

    private String serialNo;

    private String orderId;

    private String remark;

//    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    // 枚举类型
    public enum Type {
        IN, OUT, SET
    }
}
