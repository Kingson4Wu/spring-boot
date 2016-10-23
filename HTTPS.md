
<http://blog.csdn.net/ro_wsy/article/details/51319963>

如果你使用Spring Boot，并且想在内嵌tomcat中添加HTTPS，需要如下步骤

要有一个证书，买的或者自己生成的
在Spring Boot中启动HTTPS
将HTTP重定向到HTTPS（可选）
获取SSL证书

有两种方式

自己通过keytool生成
通过证书授权机构购买
这里作为演示，采用keytool生成

输入下面的命令，根据提示输入信息

keytool -genkey -alias tomcat  -storetype PKCS12 -keyalg RSA -keysize 2048  -keystore keystore.p12 -validity 3650

<pre>
Enter keystore password:
Re-enter new password:
What is your first and last name?
[Unknown]:
What is the name of your organizational unit?
[Unknown]:
What is the name of your organization?
[Unknown]:
What is the name of your City or Locality?
[Unknown]:
What is the name of your State or Province?
[Unknown]:
What is the two-letter country code for this unit?
[Unknown]:
Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
[no]: yes
</pre>

会生成一个PKCS12格式的叫做keystore.p12的证书，之后启动Spring Boot时会引用这个证书

Spring Boot 中开启HTTPS

默认情况下Spring Boot内嵌的Tomcat服务器会在8080端口启动HTTP服务，Spring Boot允许在application.properties中配置HTTP或HTTPS，但是不可同时配置，如果两个都启动，至少有一个要以编程的方式配置，Spring Boot官方文档建议在application.properties中配置HTTPS，因为HTTPS比HTTP更复杂一些，可以参考spring-boot-sample-tomcat-multi-connectors的实例

在application.properties中配置HTTPS

<pre>
server.port: 8443
server.ssl.key-store: classpath:keystore.p12
server.ssl.key-store-password: mypassword
server.ssl.keyStoreType: PKCS12
server.ssl.keyAlias: tomcat
</pre>
这就够了

将HTTP请求重定向到HTTPS（可选）

让我们的应用支持HTTP是个好想法，但是需要重定向到HTTPS，上面说了不能同时在application.properties中同时配置两个connector，所以要以编程的方式配置HTTP connector，然后重定向到HTTPS connector

这需要在配置类中配置一个TomcatEmbeddedServletContainerFactory bean，代码如下

```java
  @Bean
  public EmbeddedServletContainerFactory servletContainer() {

    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {

        @Override
        protected void postProcessContext(Context context) {

          SecurityConstraint securityConstraint = new SecurityConstraint();
          securityConstraint.setUserConstraint("CONFIDENTIAL");
          SecurityCollection collection = new SecurityCollection();
          collection.addPattern("/*");
          securityConstraint.addCollection(collection);
          context.addConstraint(securityConstraint);
        }
    };
    tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
    return tomcat;
  }

  private Connector initiateHttpConnector() {

    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(8080);
    connector.setSecure(false);
    connector.setRedirectPort(8443);
    return connector;
  }
```
搞定！


---

cookie中的secure属性的研究
<https://www.oschina.net/question/8676_3423>

但凡做web项目的，或多或少的会接触cookie。做j2ee也不例外。本人也一直使用，不能说不熟，但今天有人问.setSecure();方法的作 用时，还真不敢说出一二来。因为我没使用过。只是看书时，有这样的一句话：

     Set-Cookie 的 secure 属性就是处理这方面的情况用的，它表示创建的 cookie 只能在 HTTPS 连接中被浏览器传递到服务器端进行会话验证，如果是 HTTP 连接则不会传递该信息，所以绝对不会被窃听到

也 很好理解：setSecure(true); 的情况下，只有https才传递到服务器端。http是不会传递的。

做j2ee的人都知道servlet的接口中也定义了Cookie对 象，也有其方法setSecue（false）；

现在我提出问题：在http连接下

  当setSecure（true）时，浏览器端的cookie会不会传递到服务器端？
  当setSecure（false）时，服务器端的cookie会不会传递到浏览器端？
答案：1）不会 ； 2）会

原理：服务器端的Cookie对象是java中的对象，请不要和浏览器端的cookie文件混淆了。服务器端的Cookie对象是方便java程序员包装 一个浏览器端的cookie文件。一但包装好，就放到response对象中，在转换成http头文件。在传递到浏览器端。这时就会在浏览器的临时文件中 创建一个cookie文件。
         但我们再次访问网页时，才查看浏览器端的cookie文件中的secure值，如果是true，但是http连接。这个cookie就不会传到服务器端。 当然这个过程对浏览器是透明的。其他人是不会知道的。

总结如下：secure值为true时，在http中是无效的；在https中才有效。


---
Spring Boot基于Tomcat的HTTP和HTTPS协议配置
<http://kchu.me/2015/08/19/Spring-Boot%E5%9F%BA%E4%BA%8ETomcat%E7%9A%84HTTP%E5%92%8CHTTPS%E5%8D%8F%E8%AE%AE%E9%85%8D%E7%BD%AE/>

目录：
1. 生成并安装证书
2. 配置SSL支持
3. 配置HTTP支持
4. 配置HTTP自动跳转到HTTPS
5. 配置部分链接允许http访问
按照官方的说法，Spring Boot无法使用配置同时支持http和https两种协议访问应用。替代方案是可以配置一种协议，然后另外一种协议的支持，通过编码来实现。推荐的方案是使用配置支持https，而写代码支持http，原因是编码写http会比较复杂一些。官方的samples示例中演示了如果通过代码支持https（也就是官方认为比较复杂的那种情况。）

下面我们来使用另外一种实现方案，就是通过配置支持https，通过编码支持http。

1. 生成并安装证书
首先生成SSL证书，这里选择P12格式。

keytool -genkey -alias xgsdk -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
按提示输入各项之后，可以得到一个keystore.p12文件。可以通过以下命令验证其中的信息。

keytool -list -v -keystore keystore.p12 -storetype pkcs12
如果ssl链接仅通过浏览器访问，可以跳过下面这步，如果需要通过客户端访问，可以从浏览器中导出cer或者crt格式的公钥，然后通过以下命令添加证书到信任列表。

keytool -import -alias xgsdk -keystore $JAVA_HOME/jre/lib/security/cacerts -file xgsdk.com.crt

2. 配置SSL支持
将keystore.p12放到工程根目录下，并在application.properties文件配置即可：

<pre>
server.ssl.key-store = keystore.p12
server.ssl.key-store-password = your_password
server.ssl.keyStoreType = PKCS12
server.ssl.keyAlias = xgsdk
<pre>

还可以额外定义端口，例如到8443：

server.port=8443
此时服务器已经打开8443端口，需要用https访问。访问http端口会发现已经不通了。

3. 配置HTTP支持
接下来通过编码的方式支持HTTP协议访问，可以在application.java入口中加入以下代码创建一个新德Connector：

```java
@Value("${xgsdk.http.port}")
private int httpPort;

@Bean
public EmbeddedServletContainerFactory servletContainer() {
    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
    tomcat.addAdditionalTomcatConnectors(createHttpConnector());
    return tomcat;
}

private Connector createHttpConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(httpPort);
    connector.setSecure(false);
    return connector;
}
```
另外在application.properties文件配置http端口即可：

xgsdk.http.port=8090
此时系统已经完全支持http和https同时访问。

4. 配置HTTP自动跳转到HTTPS
我们希望用户访问http端口的时候会自动跳转到https协议来访问，可以在创建connect的时候进行端口重定向。（当然还有其他方式，比如可以在应用中通过filter进行重定向处理）。

```java
@Bean
public EmbeddedServletContainerFactory servletContainer() {
    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
    	  SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/");
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
    };
    tomcat.addAdditionalTomcatConnectors(createHttpConnector());
    return tomcat;
}

private Connector createHttpConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(httpPort);
    connector.setSecure(false);
    connector.setRedirectPort(httpsPort);
    return connector;
}
```

5. 配置部分链接允许http访问
这个需求源自一些静态资源，使用http协议访问可以获得更高效率。这里需要再增加一个SecurityConstraint对象进行处理，我们先设置一些url-pattern，然后将这些pattern加入到NONE策略的SecurityConstraint中，以便允许这部分链接通过http访问。而剩下的仍然走CONFIDENTIAL策略。

```java
private static final String HTTP_URL_PATTERNS[] = {
        "/static/*", 
        "/download/*", 
        "/doc/*"
        };
@Bean
public EmbeddedServletContainerFactory servletContainer() {
    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
        @Override
        protected void postProcessContext(Context context) {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("NONE");
            SecurityCollection collection = new SecurityCollection();
            for (String pattern : HTTP_URL_PATTERNS) {
                collection.addPattern(pattern);
            }
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
            
            SecurityConstraint securityConstraint2 = new SecurityConstraint();
            securityConstraint2.setUserConstraint("CONFIDENTIAL");
            SecurityCollection collection2 = new SecurityCollection();
            collection2.addPattern("/");
            securityConstraint2.addCollection(collection2);
            context.addConstraint(securityConstraint2);
            
            LOGGER.info("Constraints length = " + context.findConstraints().length);
        }
    };
    tomcat.addAdditionalTomcatConnectors(createHttpConnector());
    return tomcat;
}

private Connector createHttpConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(httpPort);
    connector.setSecure(false);
    connector.setRedirectPort(httpsPort);
    return connector;
}
```