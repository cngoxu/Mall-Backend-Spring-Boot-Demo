package xyz.cngo.common.error;

public enum EmBusinessError implements CommonError {
    // 通用错误
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    PARAMETER_MISSING(10002, "缺少必要参数"),
    UNKNOWN_ERROR(15001, "未知错误"),
    DATABASE_OPERATION_FAILED(15002, "数据库操作失败"),
    TRANSACTION_FAILED(15003, "事务处理异常"),
    EMAIL_SEND_FAILED(15004, "邮件发送失败"),
    VERIFICATION_CODE_EXPIRED(15005, "验证码已过期"),
    VERIFICATION_CODE_ERROR(15006, "短信验证码不正确"),

    // 用户相关
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户名或密码不正确"),
    USER_NOT_LOGIN(20003, "用户尚未登陆"),
    USER_REGISTER_FAIL(20004, "用户注册失败"),
    USER_ALREADY_REGISTERED(20005, "该用户已经注册"),

    // 商品相关
    PRODUCT_STOCK_NOT_ENOUGH(30001, "商品库存不足"),
    PRODUCT_NOT_EXIST(30002, "商品不存在或者已被删除"),
    PRODUCT_OFF_SHELF(30003, "商品已下架"),
    PRODUCT_OUT_OF_STOCK(30005, "商品已售罄"),
    PRODUCT_NOT_OWNED_BY_USER(30007, "商品不属于该用户"),

    // 购物车相关
    PRODUCT_ALREADY_IN_CART(40001, "该商品已经添加到购物车"),
    CART_CAPACITY_FULL(40002, "购物车容量已满"),
    CART_ITEM_NOT_FOUND(40003, "购物车中未找到该商品"),

    //订单相关错误
    ORDER_NOT_FOUND(50001, "订单不存在"),
    ORDER_ALREADY_PAID(50002, "订单已经支付"),
    ORDER_ALREADY_CANCELLED(50003, "订单已经取消"),
    ORDER_CANNOT_CANCEL(50004, "订单不允许取消"),
    ORDER_STATUS_ABNORMAL(50005, "订单状态异常"),
    ORDER_NOT_OWNED_BY_USER(50006, "订单不属于该用户"),
    ORDER_PENDING_PAYMENT(50007, "有待支付订单"),
    ORDER_CANNOT_REFUND(50008, "订单不允许退款"),
    ORDER_ALREADY_REFUNDED(50009, "订单已经退款"),

    //余额相关
    BALANCE_INSUFFICIENT(60001, "余额不足"),
    ;

    EmBusinessError(int errorCode, String errorMsg) {
        this.errCode = errorCode;
        this.errMsg = errorMsg;
    }

    private final int errCode;
    private final String errMsg;

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
