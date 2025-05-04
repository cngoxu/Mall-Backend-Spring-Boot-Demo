package xyz.cngo.viewobject;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.cngo.model.BalanceLogModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceLogVO {
    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String type;

    private String serialNo;

    private String orderId;

    private String remark;

    private Date createdAt;

    public static BalanceLogVO convertFromModel(BalanceLogModel balanceLogModel) {
        BalanceLogVO balanceLogVO = new BalanceLogVO();
        BeanUtils.copyProperties(balanceLogModel, balanceLogVO);
        return balanceLogVO;
    }
}
