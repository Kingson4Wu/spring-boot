+ Spring Cloud<http://projects.spring.io/spring-cloud/>
<https://github.com/spring-cloud-samples>
Spring Cloud是一个基于Spring Boot实现的云应用开发工具，它为基于JVM的云应用开发中的
配置管理、服务发现、断路器、智能路由、微代理、控制总线、全局锁、决策竞选、分布式会话和集群状态管理等操作提供了一种简单的开发方式。


+ Spring Cloud的微服务框架的组件中就包含了一个Circuit Breaker组件。
<http://dwz.cn/3R5BIV>
熔断行为的核心是提供Fallback方法来实现降级逻辑。Spring Cloud提供了很多种模式来适应不同场景的要求。


+ 深度剖析服务发现组件Netflix Eureka
<http://geek.csdn.net/news/detail/130223>


+ 如何将 Spring Cloud Netflix 框架集成到现有 API 中
<http://mp.weixin.qq.com/s/Gw2Rf6kQ0YfyUvf6-58xbg>
Netflix (OSS) Spring Cloud项目

随着Netflix's (OSS) Spring Cloud项目的流行和成功，或许可以考虑将它集成到那些以任务为关键的API里。Netflix的项目建立在Spring Boot框架之上，提供了如下组件：
+ Eureka用于Spring管理下的bean服务发现
+ Zuul处理路由服务，被视为请求的”守门员“
+ Ribbon用于动态路由和负载均衡
+ Hystrix提供了断路器功能，以处理无响应的API调用
+ Turbine提供了关于Hystrix的全部可用断路器的信息

同时推荐使用Spring Cloud Config，它把应用配置集中到了一个Git仓库。

---


#### 配置中心
Spring Cloud Config
<http://mp.weixin.qq.com/s/EMYtRQUqagl9qLYav2xIyQ>

#### 服务注册中心
Spring Cloud Consul
<http://mp.weixin.qq.com/s/MRac4s8yN_PWDNa5PnC48Q>

(开源项目做注册中心)
 
ZooKeeper
etcd
Consul
Eureka


---
Spring Cloud是一个基于Spring Boot实现的云应用开发工具，它为基于JVM的云应用开发中的配置管理、服务发现、断路器、智能路由、微代理、控制总线、全局锁、决策竞选、分布式会话和集群状态管理等操作提供了一种简单的开发方式。

Spring Cloud包含了多个子项目（针对分布式系统中涉及的多个不同开源产品），比如：Spring Cloud Config、Spring Cloud Netflix、Spring Cloud CloudFoundry、Spring Cloud AWS、Spring Cloud Security、Spring Cloud Commons、Spring Cloud Zookeeper、Spring Cloud CLI等项目。

Spring Cloud构建微服务架构（一）服务注册与发现:<http://mp.weixin.qq.com/s/bXo2u2ODpKknrubF_IXqvQ>

Spring Cloud 系列文章: <http://www.ityouknow.com/spring-cloud>

