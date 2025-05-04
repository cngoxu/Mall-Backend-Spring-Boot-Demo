package xyz.cngo.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_balance_log")
public class BalanceLogEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(condition = SqlCondition.EQUAL)
    private Integer userId;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String type;

    private String serialNo;

    private String orderId;

    private String remark;

//    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    // 枚举类型
    public enum Type {
        RECHARGE, WITHDRAW, CONSUME, REFUND
    }
}
