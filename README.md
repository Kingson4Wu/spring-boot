+ spring boot 和 微服务 spring cloud 的使用例子
+ HTTPS 和 HTTP2在Spring boot的实现以及md笔记


---

+ <http://spring.io/blog/2014/03/07/deploying-spring-boot-applications>
+ <http://www.infoq.com/cn/articles/microframeworks1-spring-boot/>
+ <http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/>
+ <http://projects.spring.io/spring-boot/>

---

+ 构建 Spring Boot API 的 5 个实用技巧: <https://www.oschina.net/translate/5-practical-tips-for-building-your-spring-boot-api-1>
+ @采蘑菇的大叔 贡献的 DEMO http://git.oschina.net/icer/iblog。用 SpringBoot 搭了个架子，整合了 freemarker 和 mybatis。
+ spring-retry重试与熔断详解:<https://mp.weixin.qq.com/s/G0iUJuII02aYKe97yIYo1w>

---

+ 使用注解 @EnableRetry @Retryable 

+ spring boot executable jar/war 原理:<http://blog.csdn.net/hengyunabc/article/details/51050219>

+ spring boot里其实不仅可以直接以 java -jar demo.jar的方式启动，还可以把jar/war变为一个可以执行的脚本来启动，比如./demo.jar。
把这个executable jar/war 链接到/etc/init.d下面，还可以变为linux下的一个service。
只要在spring boot maven plugin里配置：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <executable>true</executable>
    </configuration>
</plugin>
这样子打包出来的jar/war就是可执行的。更多详细的内容可以参考官方的文档。
```
+ http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#deployment-install
+ spring boot / cloud 之 使用RestTemplate来构建远程调用服务:<https://mp.weixin.qq.com/s/-HPOaG8z_v0EcwBkJ2d7hQ>
+ 使用 Keyclock 轻松保护 Spring Boot 应用程序:<https://mp.weixin.qq.com/s/j0GO2bAQIByqjaWaXqDLAA>
+ spring boot 有一个@CrossOrigin注解，可以直接添加上就解决了
---
+ <http://swagger.io/swagger-ui/>
+ <https://github.com/swagger-api/swagger-ui>

---

+ 将 Spring Boot 应用程序迁移到 Java 9：兼容性: <https://mp.weixin.qq.com/s/jM_It9--dkEW0Bu5vaKIIA>

+ Spring 接口支持返回多种格式:<https://mp.weixin.qq.com/s/U-Z9Hoj3n-lsRvBC08-zdg>
    - SpringMVC输出格式判定
    SpringMVC使用ContentNegotationStrategy来判定用户请求希望得到什么格式的数据。
    ContentNegotationStrategy通过三种方式来识别用户想要返回什么样的数据
        1. 通过请求URL后缀：http://myserver/myapp/accounts/list.html 返回html格式
        2. 通过请求的参数：http://myserver/myapp/accounts/list?format=xls 该设置默认不开启，默认key是format。
        3. 通过HTTP Header的Accept：Accept:application/xml 优先级由上至下
    - 代码
    ```java
       @Override
       
       public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
       
           configurer.favorPathExtension(false)
       
                   .favorParameter(true)
       
                   .parameterName("mediaType")
       
                   .defaultContentType(MediaType.APPLICATION_JSON)
       
                   .mediaType("xml", MediaType.APPLICATION_XML)
       
                   .mediaType("html", MediaType.TEXT_HTML)
       
                   .mediaType("json", MediaType.APPLICATION_JSON);
       
       } 
    ``` 
       
    - 这三个组件是用来处理返回不同格式输出的关键
     Request Mappings: 决定不同的请求到不同的方法并返回不同的格式.
     View Resolution: 根据类型返回合适的表示层.
     HttpMessageConverters: 将request中的参数转换成java对象，将java对象转换成相应的输出格式到response.
       
+ 深入Spring Boot：那些注入不了的Spring占位符（${}表达式）:<http://blog.csdn.net/hengyunabc/article/details/75453307>  
     
+ 深入Spring Boot：Spring Context 的继承关系和影响 :<https://mp.weixin.qq.com/s/8Ms1SkhzrJVBqbz9a0vz0Q>    

+ 浅析分布式下的事件驱动机制（ PubSub 模式 ）:<https://www.cnkirito.moe/2017/09/13/event-2/>
    - spring-boot-starter-data-redis, spring-boot-starter-activemq
    
+ 基于 Redis 的分布式锁组件 spring-boot-klock-starter:<https://mp.weixin.qq.com/s/TEPR5u3UVaRys667xPSfSg>


---       

 
