package com.kxw.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kxw.entity.User;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-12-22
 * <p>Version: 1.0
 */
@EnableAutoConfiguration
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/{id}")
    public User view(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(id);
        user.setName("zhang");

        System.out.println(User.class.getClassLoader());
        try {
            User user2 = (User) Class.forName("com.kxw.entity.User").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

   /* public static void main(String[] args) {
        SpringApplication.run(UserController.class);
    }*/


    /**
     * 第一种方式
     * 通过在UserController中加上@EnableAutoConfiguration开启自动配置，
     * 然后通过SpringApplication.run(UserController.class);运行这个控制器；
     * 这种方式只运行一个控制器比较方便；
     **/

    /**
     * http://blog.csdn.net/Kingson_Wu/article/details/51175732
     * 在idea的java compiler中加入 -g:none ，再跑起来，调用url，会报错：
     * java.lang.IllegalArgumentException: Name for argument type [int] not available, and parameter name information not found in class file either.
     * 由此说明，使用maven编译打包时回把方法的参数名信息加入class文件的，也就是说asm读取参数名字时从class文件获取的。
     *
     * http://localhost:8080/user/baseType.sdod?count=3&id=44 也能访问
     * Spring MVC中，在路径参数中如果带”.”的话“.”后面的值将会被忽略。例如访问http://http://localhost:8080/pathvar/xx.yy。Spring MVC就会把“.”后面的yy忽略。
     *  configurer.setUseSuffixPatternMatch(false);能解决
     *  {@link com.kxw.WebMvcConfig#configurePathMatch}
     */
    @RequestMapping("/baseType")
    public User baseType(int count, long id) {
        User user = new User();
        user.setId(id);
        user.setName("zhang");

        System.out.println("1:" + this.getClass().getClassLoader());
        System.out.println("2:" + User.class.getClassLoader());
        System.out.println("22:" + UserController.class.getClassLoader());
        System.out.println("3:" + Thread.currentThread().getContextClassLoader());
        System.out.println("---------");

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            cl.loadClass("com.kxw.apisign.RSACoder");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * 1:sun.misc.Launcher$AppClassLoader@b4c966a
         2:sun.misc.Launcher$AppClassLoader@b4c966a
         22:sun.misc.Launcher$AppClassLoader@b4c966a
         3:TomcatEmbeddedWebappClassLoader
         */
        return user;
    }


    @RequestMapping("/object")
    public User object(User user, HttpServletResponse response) {
        //response.setHeader("Connection", "Close");

        return user;
    }


    @RequestMapping("/receiveTask")
    public String receiveTask(HttpServletRequest request) throws IOException {
        logger.info("request method: {}", request.getMethod());
        Map<String, String[]> map = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            logger.info("key:{}, value: {}, values length: {}", entry.getKey(), entry.getValue()[0], entry.getValue().length);
        }
        if ("POST".equals(request.getMethod())) {
            StringBuilder jb = new StringBuilder();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            logger.info("post content: {}", jb);
        }


        return "success";
    }


    @RequestMapping("/http2Test")
    String http2Test() {
        RestTemplate http2Template = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        RestTemplate http11Template = new RestTemplate();

        String http11Response = http11Template.getForObject("https://127.0.0.1:8443/user/1", String.class);
        String http2Response = http2Template.getForObject("https://127.0.0.1:8443/user/1", String.class);

        return "HTTP/1.1 : " + http11Response.contains("You are using HTTP/2 right now!") + "<br/>" +
                "HTTP/2 : " + http2Response.contains("You are using HTTP/2 right now!");
    }

    @RequestMapping("/http2push/{enable}")
    String http2push(HttpServletRequest request, @PathVariable("enable") String enable) {

        Request jettyRequest = (Request) request;
        if ("true".equals(enable) && jettyRequest.isPushSupported()) {
            logger.info("server push");
            jettyRequest.getPushBuilder()
                    .path("/sunway.jpg")
                    .push();
        }
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <img src=\"https://localhost:8443/sunway.jpg\">\n" +
                "</body>\n" +
                "</html>";

    }
}  