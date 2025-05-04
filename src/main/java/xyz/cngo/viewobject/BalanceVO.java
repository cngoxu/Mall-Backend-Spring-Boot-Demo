package xyz.cngo.viewobject;

import lombok.Data;
import xyz.cngo.model.BalanceModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceVO {
    private BigDecimal balance;

    private Date updatedAt;

    public BalanceVO(BalanceModel balanceModel) {
        this.balance = balanceModel.getBalance();
        this.updatedAt = balanceModel.getUpdatedAt();
    }
}
