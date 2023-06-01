package com.fectivnfy.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 核心控制器
 *
 * @author 贾晓源
 * @date 2023-06-01 17:58:24
 */
@RestController
@RequestMapping("/core")
public class CoreController {

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/testExceptionHandler")
    public String testExceptionHandler() {
        throw new NullPointerException();
    }
}
