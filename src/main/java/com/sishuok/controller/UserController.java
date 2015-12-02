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


    /**第一种方式
     通过在UserController中加上@EnableAutoConfiguration开启自动配置，
     然后通过SpringApplication.run(UserController.class);运行这个控制器；
     这种方式只运行一个控制器比较方便；**/
}  