http://spring.io/blog/2014/03/07/deploying-spring-boot-applications

<http://www.infoq.com/cn/articles/microframeworks1-spring-boot/>
<http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/>
<http://projects.spring.io/spring-boot/>


构建 Spring Boot API 的 5 个实用技巧
<https://www.oschina.net/translate/5-practical-tips-for-building-your-spring-boot-api-1>

@采蘑菇的大叔 贡献的 DEMO http://git.oschina.net/icer/iblog。用 SpringBoot 搭了个架子，整合了 freemarker 和 mybatis。

---

spring boot executable jar/war 原理:<http://blog.csdn.net/hengyunabc/article/details/51050219>

spring boot里其实不仅可以直接以 java -jar demo.jar的方式启动，还可以把jar/war变为一个可以执行的脚本来启动，比如./demo.jar。

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
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#deployment-install
 
---
 <http://swagger.io/swagger-ui/>
 <https://github.com/swagger-api/swagger-ui>
 