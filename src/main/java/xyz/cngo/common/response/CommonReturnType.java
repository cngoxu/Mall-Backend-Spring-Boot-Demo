package xyz.cngo.common.response;

import lombok.Getter;
import lombok.Setter;

/**
 * status：表明对应请求的处理结果，取值为 "success" 或 "fail"。
 * success：表示请求成功。
 * fail：表示请求失败。
 * data：
 * 若 status = "success"，则 data 内返回前端需要的 JSON 数据。
 * 若 status = "fail"，则 data 内返回通用的错误码格式，包含以下字段：
 * code：错误代码。
 * message：错误描述。
 */
@Getter
@Setter
public class CommonReturnType {

    private String status;

    private Object data;

    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}

