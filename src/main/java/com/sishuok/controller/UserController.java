package com.sishuok.controller;

import com.sishuok.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-12-22
 * <p>Version: 1.0
 */
@EnableAutoConfiguration
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/{id}")
    public User view(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(id);
        user.setName("zhang");
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
     */
    @RequestMapping("/baseType")
    public User baseType(int count, long id) {
        User user = new User();
        user.setId(id);
        user.setName("zhang");
        return user;
    }


    @RequestMapping("/object")
    public User object(User user) {

        return user;
    }
}  