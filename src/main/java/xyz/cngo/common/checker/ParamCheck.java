package xyz.cngo.common.checker;

import java.lang.annotation.*;

/**
 * 参数校验注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamCheck {
    // 参数名称(用于错误提示)
    String name() default "";

    // 是否不允许为空
    boolean notNull() default true;

    // 是否不允许为空字符串(仅对String有效)
    boolean notEmpty() default true;

    // 最小长度(对String和Collection有效)
    int minLength() default 0;

    // 最大长度(对String和Collection有效)
    int maxLength() default 128;

    // 最小值(对数字类型有效)
    double min() default 0;

    // 最大值(对数字类型有效)
    double max() default Long.MAX_VALUE;

    // 自定义正则表达式(对String有效)
    String regex() default "";

    // 校验失败时的错误提示
    String message() default "参数校验失败";
}
