package com.qg.dorm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 日志切面, 用于记录Controller和Service方法的执行日志
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    // 使用ThreadLocal存储请求信息
    private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    // 定义切点：匹配所有Controller方法
    @Pointcut("execution(* com.qg.dorm.controller.*.*(..))")
    public void controllerPointcut() {}

    // 定义切点：匹配所有Service方法
    @Pointcut("execution(* com.qg.dorm.service.impl.*.*(..))")
    public void servicePointcut() {}

    // 前置通知：方法执行前
    @Before("controllerPointcut()")
    public void beforeController(JoinPoint joinPoint) {
        // 从RequestContextHolder获取请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 存储到ThreadLocal
        requestThreadLocal.set(request);
        
        log.info("[Controller] Request: {}", request.getRequestURL());
        log.info("[Controller] Method: {}", joinPoint.getSignature().getName());
        log.info("[Controller] Args: {}", Arrays.toString(joinPoint.getArgs()));
    }

    // 环绕通知：方法执行前后
    @Around("controllerPointcut() || servicePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[{}] Method: {} executed in {}ms", 
                    joinPoint.getSignature().getDeclaringTypeName().contains("controller") ? "Controller" : "Service",
                    joinPoint.getSignature().getName(),
                    endTime - startTime);
            return result;
        } catch (Exception e) {
            log.error("[{}] Method: {} error: {}",
                    joinPoint.getSignature().getDeclaringTypeName().contains("controller") ? "Controller" : "Service",
                    joinPoint.getSignature().getName(),
                    e.getMessage());
            throw e;
        }
    }

    // 后置通知：方法执行后
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturningController(JoinPoint joinPoint, Object result) {
        log.info("[Controller] Method: {} returned: {}", joinPoint.getSignature().getName(), result);
        // 清理ThreadLocal
        requestThreadLocal.remove();
    }

    //异常已在@around中记录
/*
    // 异常通知：方法抛出异常时
    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("[{}] Method: {} threw exception: {}",
                joinPoint.getSignature().getDeclaringTypeName().contains("controller") ? "Controller" : "Service",
                joinPoint.getSignature().getName(),
                e.getMessage());
        
        // 如果是Controller层异常，清理ThreadLocal（请求处理已完成）
        if (joinPoint.getSignature().getDeclaringTypeName().contains("controller")) {
            requestThreadLocal.remove();
        }
    }
*/

    // 提供静态方法获取请求
    public static HttpServletRequest getCurrentRequest() {
        return requestThreadLocal.get();
    }
}