package com.fectivnfy.common.handler;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * 全局异常处理程序
 *
 * @author 贾晓源
 * @date 2023-06-01 17:15:37
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public String exceptionHandle(NativeWebRequest request, Exception e) {
        return "全局异常处理器捕获到了异常";
    }

}
