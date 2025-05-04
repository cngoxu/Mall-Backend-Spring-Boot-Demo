package xyz.cngo.common.checker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ParamCheckAspect {

    public ParamCheckAspect() {
        System.out.println("ParamCheckAspect is loaded");
    }

    // @Before("@annotation(xyz.cngo.common.checker.ParamCheck) || @within(xyz.cngo.common.checker.ParamCheck)")
    @Before("execution(* *(..,@xyz.cngo.common.checker.ParamCheck (*),..))")
    public void beforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        ParamCheckValidator.validate(method, args);
    }
}
