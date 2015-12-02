package com.sishuok;

import com.sishuok.controller.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>User: Zhang Kaitao 
 * <p>Date: 13-12-22 
 * <p>Version: 1.0 
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    /**第二种方式
     通过@Configuration+@ComponentScan开启注解扫描并自动注册相应的注解Bean
     http://localhost:8080/user/1
     **/

    /**java -jar ...your.jar  --Dserver.port=8081 **/

    /**
     * which port the server runs on, can be configured by specifying properties either through the command line (as --D-style arguments) or through a loaded property file (Spring Boot will automatically consult any properties in a file named application.properties on the CLASSPATH, for example). Thus, to change the port on which Tomcat listens, you might specify --Dserver.port=8081, to have it listen on port 8081. If you specify server.port=0, it’ll automatically find an unused port to listen on, instead.
     * **/
} 