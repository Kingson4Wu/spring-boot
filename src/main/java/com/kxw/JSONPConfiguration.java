package com.kxw;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice(basePackageClasses = com.kxw.controller.UserController.class)
public class JSONPConfiguration extends AbstractJsonpResponseBodyAdvice {

    public JSONPConfiguration() {
        super("jsonpcallback", "jsonp");
    }
}
