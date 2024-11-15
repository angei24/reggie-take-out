package com.itheima.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //全局异常处理
//    @ExceptionHandler(Exception.class)//捕获所有异常
//    public R exception(Exception e) {
//        e.printStackTrace();
//        return R.error(e.getMessage());
//    }
    //捕获自定义异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
