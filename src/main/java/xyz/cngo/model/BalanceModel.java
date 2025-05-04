package xyz.cngo.model;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.cngo.entity.BalanceEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceModel {
    public BalanceModel(BalanceEntity balanceEntity) {
        this.userId = balanceEntity.getUserId();
        this.balance = balanceEntity.getBalance();
        this.updatedAt = balanceEntity.getUpdatedAt();
    }

    private Integer userId;

    private BigDecimal balance;

    private Date updatedAt;
}
