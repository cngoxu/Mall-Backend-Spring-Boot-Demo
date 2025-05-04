package xyz.cngo.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_balance")
public class BalanceEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    private BigDecimal balance;

//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
