package xyz.cngo.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.CommonError;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.common.response.CommonReturnType;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


//@ControllerAdvice 考虑全局注解处理器
@RestController("BaseController")
public class BaseController {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|163\\.com|qq\\.com|buaa\\.edu\\.cn|126\\.com|outlook\\.com|cngo\\.xyz)$";
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    public static final String IMAGE_LINK_REGEX = "https?://[^\\s<]+";
    public static final String GENDER_REGEX = "\\b(male|female|other)\\b";
    public static final String PRODUCT_STATUS_REGEX = "\\b(active|inactive)\\b";

    /**
     * 定义 ExceptionHandler 来解决未被 Controller 层捕获的异常
     * 使用 Spring 的钩子思想，通过全局异常处理器捕获并处理所有未被 Controller 层捕获的异常
     * 这样可以统一处理异常，避免在每个 Controller 方法中重复编写异常处理逻辑
     * 同时，可以提供更友好的错误响应给前端，提升用户体验
     * @param request
     * @param ex    捕获到的异常
     * @return
     */
    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> handleException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData;
        HttpStatus status;

        if(ex instanceof BusinessException){
            // 处理已知的业务异常
            responseData = processResponseData((BusinessException) ex);
            status = HttpStatus.OK; // 返回 200 状态码
        } else if (ex instanceof SQLException) {
            // 处理 SQL 异常
            responseData = processResponseData(EmBusinessError.DATABASE_OPERATION_FAILED);
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 返回 500 状态码
        } else if (ex instanceof DataAccessException) {
            // 处理 MyBatis 或 Spring 数据访问异常
            responseData = processResponseData(EmBusinessError.DATABASE_OPERATION_FAILED);
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 返回 500 状态码
        } else if (ex instanceof IllegalArgumentException) {
            // 处理非法参数异常
            responseData = processResponseData(EmBusinessError.PARAMETER_VALIDATION_ERROR.getErrCode(), ex.getMessage());
            status = HttpStatus.BAD_REQUEST; // 返回 400 状态码
        } else if (ex instanceof TransactionException) {
            // 处理事务异常
            responseData = processResponseData(EmBusinessError.TRANSACTION_FAILED);
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 返回 500 状态码
        }else if (ex instanceof MissingServletRequestParameterException) {
            // 处理缺少参数异常
            responseData = processResponseData(EmBusinessError.PARAMETER_MISSING.getErrCode(), ex.getMessage());
            status = HttpStatus.BAD_REQUEST; // 返回 400 状态码
        } else {
            // 处理其他未知异常
            responseData = processResponseData(EmBusinessError.UNKNOWN_ERROR.getErrCode(), ex.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 返回 500 状态码
        }
        return new ResponseEntity<>(responseData, status);
    }

    /**
     * 处理返回消息
     * @param error
     * @return
     */
    private Map<String,Object> processResponseData(CommonError error){
        Map<String,Object> responseData = new HashMap<>();
        responseData.put("status", "fail");
        responseData.put("code", error.getErrCode());
        responseData.put("message", error.getErrMsg());
        return responseData;
    }

    /**
     * 处理返回消息，自定义消息
     * @param errCode
     * @param errMsg
     * @return
     */
    private Map<String,Object> processResponseData(Integer errCode, String errMsg){
        Map<String,Object> responseData = new HashMap<>();
        responseData.put("status", "fail");
        responseData.put("code", errCode);
        responseData.put("message", errMsg);
        return responseData;
    }
}

