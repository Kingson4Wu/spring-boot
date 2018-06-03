package com.kxw.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/1/23.
 */
@EnableAutoConfiguration
@RestController
@RequestMapping("/baidumap")
public class BaiduMapController {

    @RequestMapping("/data.json")
    public String data(HttpServletRequest request, HttpServletResponse response) {

        String content = null;
        try {
            content = FileUtils.readFileToString(new File(BaiduMapController.class.getResource("/data.json")
                .getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //response.addHeader("Content-Encoding", "gzip");
        response.addHeader("Content-Type", "application/json");
        response.addHeader("Access-Control-Allow-Origin","*");

        return content;
    }


    @RequestMapping("/redirect")
    public void redirect(HttpServletRequest request, HttpServletResponse response) {

        //请求重定向: 发送302状态码+location响应头
        //浏览器读到302状态码之后，会再次自动向服务器发送一个请求，请求的地址是location的value值
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("location", "http://baidu.com");

    }
}
