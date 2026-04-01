package com.qg.dorm.exception;

import com.qg.dorm.aspect.LogAspect;
import com.qg.dorm.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理数据库异常
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public Result<?> handleSQLException(SQLException e) {
        HttpServletRequest request = LogAspect.getCurrentRequest();
        log.error("数据库异常 [{}]: {}", request != null ? request.getRequestURI() : "unknown", e.getMessage(), e);
        return Result.error("数据库操作失败，请稍后重试");
    }

    // 处理参数验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpServletRequest request = LogAspect.getCurrentRequest();
        log.error("参数验证异常 [{}]: {}", request != null ? request.getRequestURI() : "unknown", e.getMessage(), e);
        return Result.error("参数格式错误");
    }

    // 处理运行时异常
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<?> handleRuntimeException(RuntimeException e) {
        HttpServletRequest request = LogAspect.getCurrentRequest();
        log.error("运行时异常 [{}]: {}", request != null ? request.getRequestURI() : "unknown", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    // 处理所有异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        HttpServletRequest request = LogAspect.getCurrentRequest();
        log.error("系统异常 [{}]: {}", request != null ? request.getRequestURI() : "unknown", e.getMessage(), e);
        return Result.error("系统错误，请稍后重试");
    }
}