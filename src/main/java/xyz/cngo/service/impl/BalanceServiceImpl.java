package xyz.cngo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.utils.GenerateUtil;
import xyz.cngo.dao.BalanceLogMapper;
import xyz.cngo.dao.BalanceMapper;
import xyz.cngo.entity.BalanceEntity;
import xyz.cngo.entity.BalanceLogEntity;
import xyz.cngo.entity.OrderEntity;
import xyz.cngo.model.BalanceLogModel;
import xyz.cngo.model.BalanceModel;
import xyz.cngo.model.OrderModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.BalanceService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BalanceServiceImpl implements BalanceService {
    @Resource
    private BalanceMapper balanceMapper;
    @Resource
    private BalanceLogMapper balanceLogMapper;

    /**
     * 获取用户的余额
     * @param userModel
     * @return
     */
    @Override
    public BalanceModel getBalance(UserModel userModel) {
        BalanceEntity balanceEntity = getBalanceEntity(userModel);
        if(Objects.isNull(balanceEntity)){
            return new BalanceModel(userModel.getUserId(), BigDecimal.ZERO, new Date());
        }
        return new BalanceModel(balanceEntity);
    }

    /**
     * 查询用户的余额明细
     * @param userModel
     * @return
     */
    @Override
    public List<BalanceLogModel> getBalanceLogs(UserModel userModel, Integer page, Integer limit) {
        Integer offset = (page - 1) * limit;
        QueryWrapper<BalanceLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        queryWrapper.orderByDesc("created_at"); // 最新优先
        queryWrapper.last("LIMIT " + offset + ", " + limit);

        List<BalanceLogEntity> balancelogEntityList = balanceLogMapper.selectList(queryWrapper);
        return balancelogEntityList.stream()
                .map(BalanceLogModel::convertFromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 用户充值
     * @param userModel
     * @param amount
     */
    @Transactional
    @Override
    public BalanceModel rechargeBalance(UserModel userModel, BigDecimal amount) throws BusinessException {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "充值金额不合法");
        }
        return addBalance(userModel, amount, "recharge", null);
    }

    /**
     * 用户提现
     * @param userModel
     * @param amount
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public BalanceModel withdrawBalance(UserModel userModel, BigDecimal amount) throws BusinessException {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "提现金额不合法");
        }
        return subBalance(userModel, amount, "withdraw", null);
    }

    /**
     * 用户消费
     * @param userModel
     * @param orderModel
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void consumeBalance(UserModel userModel, OrderModel orderModel) throws BusinessException {
        BalanceModel balanceModel = subBalance(userModel, orderModel.getTotalPrice(), "consume", orderModel);
    }

    /**
     * 用户退款
     * @param userModel
     * @param orderModel
     * @throws BusinessException
     */
    @Transactional
    @Override
    public void refundBalance(UserModel userModel, OrderModel orderModel) throws BusinessException {
        BalanceModel balanceModel = addBalance(userModel, orderModel.getTotalPrice(), "refund", orderModel);
    }

    /**
     * 私有方法，扣减余额
     * @param userModel
     * @param amount
     * @param type
     * @param orderModel
     * @return
     */
    private BalanceModel subBalance(UserModel userModel, BigDecimal amount, String type, OrderModel orderModel) throws BusinessException {
        BalanceEntity balanceEntity = getBalanceEntity(userModel);
        if(Objects.isNull(balanceEntity) ||
           balanceEntity.getBalance().compareTo(amount) < 0){
            throw new BusinessException(EmBusinessError.BALANCE_INSUFFICIENT);
        }

        // 更新余额
        balanceEntity.setBalance(balanceEntity.getBalance().subtract(amount));
        balanceMapper.updateById(balanceEntity);

        insertBalanceLog(userModel, balanceEntity, amount, type, orderModel);
        return new BalanceModel(balanceEntity);
    }

    /**
     * 私有方法，增加余额
     * @param userModel
     * @param amount
     * @param type
     * @param orderModel
     * @return
     * @throws BusinessException
     */
    private BalanceModel addBalance(UserModel userModel, BigDecimal amount, String type, OrderModel orderModel) throws BusinessException {
        BalanceEntity balanceEntity = getBalanceEntity(userModel);
        if (Objects.isNull(balanceEntity)) {
            // 没有查询到余额则创建
            balanceEntity = new BalanceEntity();
            balanceEntity.setUserId(userModel.getUserId());
            balanceEntity.setBalance(amount);
            balanceMapper.insert(balanceEntity);
        } else {
            // 修改余额
            balanceEntity.setBalance(balanceEntity.getBalance().add(amount));
            balanceMapper.updateById(balanceEntity);
        }

        // 插入余额变动明细
        insertBalanceLog(userModel, balanceEntity, amount, type, orderModel);

        return new BalanceModel(balanceEntity);
    }

    /**
     * 私有方法，插入余额明细
     * @param userModel
     * @param balanceEntity
     * @param amount
     * @param type
     * @param orderModel
     */
    private void insertBalanceLog(UserModel userModel, BalanceEntity balanceEntity, BigDecimal amount, String type, OrderModel orderModel){
        BalanceLogEntity balanceLogEntity = new BalanceLogEntity();
        balanceLogEntity.setUserId(userModel.getUserId());
        balanceLogEntity.setBalanceAfter(balanceEntity.getBalance());
        balanceLogEntity.setType(type);

        switch (type){
            case "recharge":
            case "refund":
                balanceLogEntity.setAmount(amount);
                break;
            case "withdraw":
            case "consume":
                balanceLogEntity.setAmount(amount.negate());
                break;
        }
        switch (type) {
            case "consume":
            case "refund":
                balanceLogEntity.setOrderId(orderModel.getOrderId());
                break;
            case "withdraw":
            case "recharge":
                balanceLogEntity.setOrderId(null);
        }

        balanceLogEntity.setRemark(type + amount + "元");
        balanceLogEntity.setSerialNo(GenerateUtil.generateBalanceSerialNumber());
        balanceLogMapper.insert(balanceLogEntity);
    }

    /**
     * 私有方法，查询余额记录
     * @param userModel
     * @return
     */
    private BalanceEntity getBalanceEntity(UserModel userModel) {
        QueryWrapper<BalanceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userModel.getUserId());
        return balanceMapper.selectOne(queryWrapper);
    }
}
