### code
+ 打包方式
	- 根据自己的需要决定是打jar包还是war包 `<packaging>war</packaging>`, pom.xml文件
	- 如果我们不需要用打war包的方式进行部署(pom中打包方式要改成jar) 可以通过main()方法来运行
	- 如果需要通过打包的方式在web容器中进行部署，则需要继承 SpringBootServletInitializer 覆盖configure(SpringApplicationBuilder)方法
	- jar包方式 使用`SpringApplication.run(Application.class, args);`启动，本质上还是启动一个嵌入式的servlet容器，
	- 把spring-boot-starter-tomcat依赖变成provided
+ 动态生成类，动态注册到Spring中
	1. 实现BeanDefinitionRegistryPostProcessor接口
	2. 处理postProcessBeanDefinitionRegistry方法
	3. 生成BeanDefinition

+ HttpClientMapperDefinitionScanner 这个类怎么想出这么写？？？
了解整个扫描机制，查找相关文章
 definition.setBeanClass(HttpClientMapperFactoryBean.class);


### Spring CLI
+ spring run app.groovy

### Spring Boot Notes
+ SpringApplication执行流程：<https://my.oschina.net/bigsloth/blog/754486>
+ spring-boot-maven-plugin 插件的作用:<http://blog.csdn.net/hotdust/article/details/51404828>
,<http://blog.csdn.net/jsyxcjw/article/details/46763827>
+  Spring Boot基础3-配置文件详解：Properties和YAML:<http://blog.csdn.net/whs_321/article/details/52663973>
+ Spring Boot应用程序在启动后，会遍历CommandLineRunner接口的实例并运行它们的run方法。也可以利用@Order注解（或者实现Order接口）来规定所有CommandLineRunner实例的运行顺序。


### 注解
+ 在 SpringBootApplication 上使用@ServletComponentScan 注解后，Servlet、Filter、Listener 可以直接通过 @WebServlet、@WebFilter、@WebListener 注解自动注册，无需其他代码。
+ @SpringBootApplication: @Configuration,@EnableAutoConfiguration,@ComponentScan。由于这些注解一般都是一起使用，spring boot提供了一个统一的注解@SpringBootApplication。
@SpringBootApplication = (默认属性)@Configuration + @EnableAutoConfiguration + @ComponentScan。
	-  @Configuration的注解类标识这个类可以使用Spring IoC容器作为bean定义的来源。@Bean注解告诉Spring，一个带有@Bean的注解方法将返回一个对象，该对象应该被注册为在Spring应用程序上下文中的bean。
	- @EnableAutoConfiguration：能够自动配置spring的上下文，试图猜测和配置你想要的bean类，通常会自动根据你的类路径和你的bean定义自动配置。
	- @ComponentScan：会自动扫描指定包下的全部标有@Component的类，并注册成bean，当然包括@Component下的子注解@Service,@Repository,@Controller。
+ 通过@EnableScheduling注解开启对计划任务的支持
	- @Scheduled(fixedRate = 5000) //通过@Scheduled声明该方法是计划任务，使用fixedRate属性每隔固定时间执行
	- @Scheduled(cron = "0 07 20 ? * \*" ) //使用cron属性可按照指定时间执行，本例指的是每天20点07分执行；
+ @EnableAspectJAutoProxy注解 激活Aspect自动代理
`<aop:aspectj-autoproxy/> `
+ @EnableAsync注解开启异步方法的支持。
+ @EnableWebMVC注解用来开启Web MVC的配置支持。
也就是写Spring MVC时的时候会用到。 相当于`<mvc:annotation-driven/>`
+ @EnableTransactionManagement注解开启注解式事务的支持。
注解@EnableTransactionManagement通知Spring，@Transactional注解的类被事务的切面包围。这样@Transactional就可以使用了。

+ 动态注册Bean:AspectJAutoProxyRegistrar 事先了ImportBeanDefinitionRegistrar接口，ImportBeanDefinitionRegistrar的作用是在运行时自动添加Bean到已有的配置类????
+

+ @Import({
        PropertyPlaceholderAutoConfiguration.class ????
+  @Bean     @Autowired 自动注入import该类的父类的成员变量
+  @Bean的函数可以有这些变量？ ConfigurableEnvironment， ApplicationContext
+   application.yaml怎么读取的？
+ WebMvcConfigurerAdapter的各种配置用法
+ Spring Boot CLI安装
Spring Boot CLI是一个命令行工具，可用于快速搭建基于Spring的原型。它支持运行Groovy脚本，这也就意味着你可以使用类似Java的语法，但不用写很多的模板代码。
Spring Boot不一定非要配合CLI使用，但它绝对是Spring应用取得进展的最快方式（你咋不飞上天呢？）。  
+ @RestController ??


### Spring-MVC
+ springMVC使用HandlerMethodArgumentResolver 自定义解析器实现请求参数绑定方法参数