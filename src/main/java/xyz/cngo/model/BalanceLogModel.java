package xyz.cngo.model;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import xyz.cngo.entity.BalanceLogEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceLogModel {
    private Integer id;

    private Integer userId;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String type;

    private String serialNo;

    private String orderId;

    private String remark;

    private Date createdAt;

    static public BalanceLogModel convertFromEntity(BalanceLogEntity balanceLogEntity) {
        BalanceLogModel balanceLogModel = new BalanceLogModel();
        BeanUtils.copyProperties(balanceLogEntity, balanceLogModel);
        return balanceLogModel;
    }
}
