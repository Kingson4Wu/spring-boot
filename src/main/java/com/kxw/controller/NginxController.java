package com.kxw.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/7/22.
 *
 * java -jar -Dserver.port=8082 ./target/spring-boot-1.0-SNAPSHOT.jar
 *
 * hosts:
 * 127.0.0.1 nginx.inner.proxy.com
 * 127.0.0.1 nginx.outer.proxy.com
 *
 * mvn clean package
 *
 * nohup java -jar -Dserver.port=8081 ./target/spring-boot-1.0-SNAPSHOT.jar >8081.log 2>&1  &
 * nohup java -jar -Dserver.port=8082 ./target/spring-boot-1.0-SNAPSHOT.jar >8082.log 2>&1  &
 * nohup java -jar -Dserver.port=8083 ./target/spring-boot-1.0-SNAPSHOT.jar >8083.log 2>&1  &
 * nohup java -jar -Dserver.port=8084 ./target/spring-boot-1.0-SNAPSHOT.jar >8084.log 2>&1  &
 *
 * ps -ef|grep spring-boot-1.0-SNAPSHOT.jar
 *
 * curl -i -H "Host:nginx.inner.proxy.com" "http://nginx.inner.proxy.com/nginx/hello"
 *
 * curl -i "http://nginx.inner.proxy.com/nginx/hello"
 *
 * /usr/local/var/log/nginx/logs
 *
 * nginx利用第三方模块nginx_upstream_check_module来检查后端服务器的健康情况
 *
 * /Users/kingsonwu/Personal/github/kingson4wu.github.io.wiki/Technical-points/loadbalance/nginx.md
 *
 * wrk  -t12 -c40 -d30s "http://nginx.inner.proxy.com/nginx/hello"
 *
 * 记录成功失败的结果,还是使用java 测试测一下
 *
 * curl -i "http://nginx.inner.proxy.com/nginx/serverError/502" (nginx 会透传5xx返回)
 *
 * 499 Client Closed Request
 一般原因:客户端在为等到服务器相应返回前就关闭了客户端描述符。一般出现在客户端设置超时后，主动关闭socket.
 解决方法：根据实际Nginx后端服务器的处理时间修改客户端超时时间。

 *
 upstream nginx.inner.proxy.com {
 server localhost:8081 weight=999 max_fails=5 fail_timeout=10s;
 server localhost:8082 weight=999 max_fails=5 fail_timeout=10s;
 server localhost:8083 weight=1 max_fails=5 fail_timeout=10s;
 server localhost:8084 weight=1 max_fails=5 fail_timeout=10s;
 check interval=3000 rise=2 fall=3 timeout=2500 type=tcp;# 每3s检查一次，成功2次标记up，失败3次则标记down，超时时间2.5s
 keepalive 32;
 }
 *
 *➜  logs ps -ef|grep spring-boot-1.0
 502 47206   571   0 10:05下午 ttys000    0:00.00 grep --color=auto spring-boot-1.0
 502 65215 63912   0  5:55下午 ttys002    1:26.23 java -jar -Dserver.port=8081 ./target/spring-boot-1.0-SNAPSHOT.jar
 502 65271 63912   0  5:56下午 ttys002    1:32.95 java -jar -Dserver.port=8082 ./target/spring-boot-1.0-SNAPSHOT.jar
 502 65286 63912   0  5:56下午 ttys002    1:19.56 java -jar -Dserver.port=8083 ./target/spring-boot-1.0-SNAPSHOT.jar
 502 65298 63912   0  5:56下午 ttys002    1:13.69 java -jar -Dserver.port=8084 ./target/spring-boot-1.0-SNAPSHOT.jar
 ➜  logs kill -9 65215
 ➜  logs kill -9 65271
 ➜  logs ps -ef|grep spring-boot-1.0
 502 47252   571   0 10:06下午 ttys000    0:00.00 grep --color=auto spring-boot-1.0
 502 65286 63912   0  5:56下午 ttys002    1:21.92 java -jar -Dserver.port=8083 ./target/spring-boot-1.0-SNAPSHOT.jar
 502 65298 63912   0  5:56下午 ttys002    1:22.62 java -jar -Dserver.port=8084 ./target/spring-boot-1.0-SNAPSHOT.jar

 /Users/kingsonwu/Personal/github/Utils4Java/http-client/src/test/java/com/kxw/http/httpclient/TestNginxCheck.java

 count: 0
 application:8084:35900
 application:8082:5938
 application:8083:35901
 application:8081:2713

 修改nginx权重貌似要停掉nginx,不能单纯的reload

 pstream nginx.inner.proxy.com {
 server localhost:8081 weight=999 max_fails=5 fail_timeout=10s;
 server localhost:8082 weight=999 max_fails=5 fail_timeout=10s;
 server localhost:8083 weight=1 max_fails=5 fail_timeout=10s backup;
 server localhost:8084 weight=1 max_fails=5 fail_timeout=10s backup;
 check interval=3000 rise=2 fall=3 timeout=2500 type=tcp;# 每3s检查一次，成功2次标记up，失败3次则标记down，超时时间2.5s
 keepalive 32;
 }

 backup不会使用到
 count: 0
 5675
 5674
 0
 0
 application:8082:5674
 application:8081:5675

 只使用1,2节点,kill了1节点之后,流量全部跑到2节点,2节点也kill了之后,3,4节点同时启用

 application:8084:6020
 application:8082:33938
 application:8083:6022
 application:8081:6982
 *
 */
@RestController
@RequestMapping("/nginx")
public class NginxController {

    private  final Logger logger = LoggerFactory.getLogger(getClass());

         /**
          * GET: curl -i "http://localhost:8080/nginx/hello"
          * POST: curl -i -d "" "http://localhost:8080/nginx/hello"
          * @return
          */

    @RequestMapping("/hello")
    public String hello() {
        logger.info("hello ...");
        return "hello";
    }

    /**
     * curl -i "http://localhost:8080/nginx/serverError/502"
     * @param code
     * @param response
     */
    @RequestMapping("/serverError/{code}")
    public void serverError(@PathVariable("code") int code, HttpServletResponse response) {

        logger.info("error :{} ...", code);
        response.setStatus(code);
    }

}
