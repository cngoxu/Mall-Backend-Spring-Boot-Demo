package xyz.cngo.common.checker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;

public class ParamCheckValidator {

    /**
     * 校验方法参数
     * @param method 方法
     * @param args 参数值数组
     * @throws IllegalArgumentException 如果参数校验失败
     */
    public static void validate(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            ParamCheck annotation = parameters[i].getAnnotation(ParamCheck.class);
            if (annotation != null) {
                validateParameter(parameters[i].getName(), args[i], annotation);
            }
        }
    }

    private static void validateParameter(String paramName, Object value, ParamCheck annotation) {


        // 1. 非空检查
        if (annotation.notNull() && value == null) {
            throw new IllegalArgumentException(
                    buildMessage(paramName, annotation, "不能为null")
            );
        }

        if (value == null) {
            return; // 如果允许为null，且值为null，则跳过其他检查
        }

        // 2. 非空字符串检查
        if (annotation.notEmpty() && value instanceof String && ((String) value).isEmpty()) {
            throw new IllegalArgumentException(
                    buildMessage(paramName, annotation, "不能为空字符串")
            );
        }

        // 3. 长度检查
        if (value instanceof CharSequence || value instanceof Collection) {
            int length = value instanceof CharSequence ?
                    ((CharSequence) value).length() : ((Collection<?>) value).size();

            if (annotation.minLength() > -1 && length < annotation.minLength()) {
                throw new IllegalArgumentException(
                        buildMessage(paramName, annotation,
                                "长度不能小于" + annotation.minLength() + "，当前长度：" + length)
                );
            }

            if (annotation.maxLength() > -1 && length > annotation.maxLength()) {
                throw new IllegalArgumentException(
                        buildMessage(paramName, annotation,
                                "长度不能大于" + annotation.maxLength() + "，当前长度：" + length)
                );
            }
        }

        // 4. 数值范围检查
        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();

            if (numValue < annotation.min()) {
                throw new IllegalArgumentException(
                        buildMessage(paramName, annotation,
                                "不能小于" + annotation.min() + "，当前值：" + numValue)
                );
            }

            if (numValue > annotation.max()) {
                throw new IllegalArgumentException(
                        buildMessage(paramName, annotation,
                                "不能大于" + annotation.max() + "，当前值：" + numValue)
                );
            }
        }

        // 5. 正则表达式检查
        if (!annotation.regex().isEmpty() && value instanceof String) {
            if (!Pattern.matches(annotation.regex(), (String) value)) {
                throw new IllegalArgumentException(
                        buildMessage(paramName, annotation,
                                "格式不符合要求，必须匹配正则表达式：" + annotation.regex())
                );
            }
        }
    }

    private static String buildMessage(String paramName, ParamCheck annotation, String reason) {
        String name = annotation.name().isEmpty() ? paramName : annotation.name();
        return name + reason + (annotation.message().isEmpty() ? "" : " (" + annotation.message() + ")");
    }
}
