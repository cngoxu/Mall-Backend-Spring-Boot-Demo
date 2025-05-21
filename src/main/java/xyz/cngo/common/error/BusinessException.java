package xyz.cngo.common.error;

import java.util.Objects;

public class BusinessException extends RuntimeException implements CommonError {
    private final CommonError error;
    private final String message;   // 可选的自定义消息

    // 直接接受 EmBusinessError 的传参用于构造业务异常
    public BusinessException(CommonError commonError) {
        this(commonError, null);
    }

    // 接受自定义 errMsg 的方式构造业务异常
    public BusinessException(CommonError commonError, String message) {
        super(Objects.isNull(message) ? commonError.getErrMsg() : message);
        this.error = commonError;
        this.message = message;
    }

    @Override
    public int getErrCode() {
        return error.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return Objects.isNull(message) ? error.getErrMsg() : message;
    }
}