package xyz.cngo.service;

import xyz.cngo.common.error.BusinessException;
import xyz.cngo.model.BalanceLogModel;
import xyz.cngo.model.BalanceModel;
import xyz.cngo.model.OrderModel;
import xyz.cngo.model.UserModel;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {

    BalanceModel getBalance(UserModel userModel);

    List<BalanceLogModel> getBalanceLogs(UserModel userModel, Integer page, Integer limit);

    BalanceModel rechargeBalance(UserModel userModel, BigDecimal amount) throws BusinessException;

    BalanceModel withdrawBalance(UserModel userModel, BigDecimal amount) throws BusinessException;

    void consumeBalance(UserModel userModel, OrderModel orderModel) throws BusinessException;

    void refundBalance(UserModel userModel, OrderModel orderModel) throws BusinessException;
}
