package xyz.cngo.controller;

import org.springframework.web.bind.annotation.*;
import xyz.cngo.common.checker.ParamCheck;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.response.CommonReturnType;
import xyz.cngo.model.BalanceLogModel;
import xyz.cngo.model.BalanceModel;
import xyz.cngo.model.UserModel;
import xyz.cngo.service.BalanceService;
import xyz.cngo.service.UserService;
import xyz.cngo.viewobject.BalanceLogVO;
import xyz.cngo.viewobject.BalanceVO;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController("BalanceController")
@RequestMapping("/balance")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class BalanceController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private BalanceService balanceService;

    /**
     * 查询用户余额接口
     * @return
     * @throws BusinessException
     */
    @GetMapping
    public CommonReturnType queryBalance() throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        BalanceModel balanceModel = balanceService.getBalance(userModel);
        BalanceVO balanceVO = new BalanceVO(balanceModel);
        return CommonReturnType.create(balanceVO);
    }

    @GetMapping("/log")
    public CommonReturnType queryBalanceLog(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        List<BalanceLogModel> balanceLogModelList = balanceService.getBalanceLogs(userModel, page, limit);
        List<BalanceLogVO> balanceLogVOList = balanceLogModelList.stream()
                .map(BalanceLogVO::convertFromModel)
                .collect(Collectors.toList());
        return CommonReturnType.create(balanceLogVOList);
    }

    /**
     * 余额充值接口
     * @param amount
     * @return
     * @throws BusinessException
     */
    @PostMapping("/recharge")
    public CommonReturnType rechargeBalance(
            @RequestParam("amount") @ParamCheck(name = "充值金额", min = 0.01) BigDecimal amount
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        BalanceModel balanceModel = balanceService.rechargeBalance(userModel, amount);
        BalanceVO balanceVO = new BalanceVO(balanceModel);
        return CommonReturnType.create(balanceVO);
    }

    /**
     * 提现余额
     * @param amount
     * @return
     * @throws BusinessException
     */
    @PostMapping("/withdraw")
    public CommonReturnType withdrawBalance(
            @RequestParam("amount") @ParamCheck(name = "提现金额", min = 0.01) BigDecimal amount
    ) throws BusinessException {
        UserModel userModel = userService.getCurrentLoggedInUser();
        BalanceModel balanceModel = balanceService.withdrawBalance(userModel, amount);
        BalanceVO balanceVO = new BalanceVO(balanceModel);
        return CommonReturnType.create(balanceVO);
    }
}
