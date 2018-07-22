package com.kxw.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/7/22.
 */
@RestController
@RequestMapping("/nginx")
public class NginxController {

    /**
     * GET: curl -i "http://localhost:8080/nginx/hello"
     * POST: curl -i -d "" "http://localhost:8080/nginx/hello"
     * @return
     */
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * curl -i "http://localhost:8080/nginx/serverError/502"
     * @param code
     * @param response
     */
    @RequestMapping("/serverError/{code}")
    public void serverError(@PathVariable("code") int code, HttpServletResponse response) {
        response.setStatus(code);
    }

}
