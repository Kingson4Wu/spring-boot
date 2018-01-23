package com.kxw.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/1/23.
 */
@EnableAutoConfiguration
@RestController
@RequestMapping("/http")
public class HttpController {

    @RequestMapping("/redirect")
    public void redirect(HttpServletRequest request, HttpServletResponse response) {

        //请求重定向: 发送302状态码+location响应头
        //浏览器读到302状态码之后，会再次自动向服务器发送一个请求，请求的地址是location的value值
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("location", "http://baidu.com");

    }

    @RequestMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {

        //定时刷新
        //浏览器读到refresh头后重新向服务器发出请求
        //response.setHeader("refresh", "3");

        //隔一定时间跳转到指定的URI
        response.setHeader("refresh", "3 ,http://baidu.com");

    }
}
