HTTPS（HTTP over TLS/SSL）,TLS/SSL(会话层)
SSL（Secure Socket Layer）是安全套接层，TLS（Transport Layer Security）是传输层安全协议，建立在SSL3.0协议规范，是 SSL3.0 的后续版本。
-------------------------------------
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

keytool -alias tomcat -exportcert -keystore keystore.p12 -file keystore.cer

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

---

nginx 对一个端口同时支持 http 和 https
<https://www.web-tinker.com/article/21227.html>
浏览器默认对 http 和 https 使用不同的端口，所以一般省略端口的 Web 服务器可以在 80 和 443 上分别开服务。但有些基于端口的 Web 服务怎么办呢？如果开多个端口，用户可能需要自己去记住端口对应的 scheme。为什么不试试把 http 和 https 都在同一个端口上实现呢？
　　TLS 也是基于 TCP，当服务器建立 TCP 连接后，可以根据收到的第一份数据来判断到底客户端是希望建立 TLS 还是直接就发了一个 http 请求过来。nginx 就做了这件事（代码），它会判断 TCP 请求建立后接收到的首个字节是什么。如果是 0x80 或 0x16 就可能是 SSL 或 TLS，可以尝试进行 https 握手。

node如何让一个端口同时支持https与http
<http://www.cnblogs.com/dojo-lzz/p/5479870.html?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io>
让同一个端口或地址既支持http协议又支持https协议，这时候我们该怎么办，有的同学很可能想到用nginx做反向代理，这不失为一个解决方案，但这也同样意味着增加了产品的复杂度，用户并不想去折腾ngnix。
HTTP与HTTPS都属于应用层协议，所以只要我们在底层协议中进行反向代理，就可以解决这个问题! 刚好node可以让我们很方便的创建一个tcp服务器！

总结:
HTTPS和HTTP是应用层的,是以ip+端口传递数据的,要实现同个端口实现两种协议,需要在TCP层进行转发处理.(错了?https默认端口是443)
nginx已经实现,或者自己基于TCP实现

```node
var net = require('net');
var http = require('http');
var https = require('https');
var fs = require('fs');

var httpPort = 3345;
var httpsPort = 3346;

var server = http.createServer(function(req, res){
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('hello world!');
}).listen(httpPort);

var options = {
  key: fs.readFileSync('./cakey.pem'),
  cert: fs.readFileSync('./cacert.pem')
};

var sserver = https.createServer(options, function(req, res){
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('secured hello world');
}).listen(httpsPort);

net.createServer(function(socket){
    socket.once('data', function(buf){
        console.log(buf[0]);
        // https数据流的第一位是十六进制“16”，转换成十进制就是22
        var address = buf[0] === 22 ? httpsPort : httpPort;
        //创建一个指向https或http服务器的链接
        var proxy = net.createConnection(address, function() {
            proxy.write(buf);
            //反向代理的过程，tcp接受的数据交给代理链接，代理链接服务器端返回数据交由socket返回给客户端
            socket.pipe(proxy).pipe(socket);
        });
        
        
        proxy.on('error', function(err) {
            console.log(err);
        });
    });
    
    socket.on('error', function(err) {
        console.log(err);
    });
}).listen(3344);
```

---

#### 关于证书

java用的存储密钥的容器。可以同时容纳n个公钥或私钥，后缀一般是.jks或者.keystore或.truststore等

####  证书,jks、pfx和cer后缀都是什么文件
<http://blog.csdn.net/baidu_18607183/article/details/51565240>
jks是JAVA的keytools证书工具支持的证书私钥格式。
pfx是微软支持的私钥格式。

cer是证书的公钥。

如果是你私人要备份证书的话记得一定要备份成jks或者pfx格式，否则恢复不了。

简单来说，cer就是你们家邮箱的地址，你可以把这个地址给很多人让他们往里面发信。
pfx或jks就是你家邮箱的钥匙，别人有了这个就可以冒充你去你家邮箱看信，你丢了这个也没法开邮箱了。

#### What's the difference between a .jks and a .cer certificate file? Is it possible to convert from one to the other?
<https://www.quora.com/Whats-the-difference-between-a-jks-and-a-cer-certificate-file-Is-it-possible-to-convert-from-one-to-the-other>
JKS - Java key store.  Its java way of storing the relevant information. 
.CER - Certificate file i.e. public ckey. 
You can convert from CER to JKS   as you are only converting the public key. Both command line tools and API will allow you do that. 
I don't believe you can access the private key from JKS file even with password ( for the purpose of exporting). I could be wrong.  A while ago I ran into issues of converting and ended up using P12 format as I was able to use the same P12 file from both Java and .NET and can even use OpenSSL tools. 
If you are looking for platform independent way to use Asymmetric key, P12 is your best choice.  You can import them into any tool/platform without any issue and use native API to extract the public key (.CER).

#### ssl证书生成：cer&jks文件生成摘录 (命令)
<http://www.cnblogs.com/xiaowenchao/p/3218055.html>

#### TLS、SSL、HTTPS以及证书
<http://www.cnblogs.com/kyrios/p/tls-and-certificates.html>
TLS是传输层安全协议（Transport Layer Security）的缩写，是一种对基于网络的传输的加密协议，可以在受信任的第三方公证基础上做双方的身份认证。TLS可以用在TCP上，也可以用在无连接的UDP报文上。协议规定了身份认证、算法协商、密钥交换等的实现。
SSL是TLS的前身，现在已不再更新
HTTPS是在基于TLS/SSL的安全套接字上的的应用层协议，除了传输层进行了加密外，其它与常规HTTP协议基本保持一致
证书是TLS协议中用来对身份进行验证的机制，是一种数字签名形式的文件，包含证书拥有者的公钥及第三方的证书信息。
证书分为2类：自签名证书和CA证书。一般自签名证书不能用来进行身份认证，如果一个server端使用自签名证书，client端要么被设置为无条件信任任何证书，要么需要将自签名证书的公钥和私钥加入受信任列表。但这样一来就增加了server的私钥泄露风险。

TLS基于CA的身份认证基本原理是：首先验证方需要信任CA提供方自己的证书(CAcert)，比如证书在操作系统的受信任证书列表中，或者用户通过“安装根证书”等方式将 CA的公钥和私钥加入受信任列表；然后CA对被验证方的原始证书进行签名（私钥加密），生成最终的证书；验证方得到最终的证书后，利用CAcert中包含的公钥进行解密，得到被验证方的原始证书。

根据RSA的加密原理，如果用CA的公钥解密成功，说明该证书的确是用CA的私钥加密的，可以认为被验证方是可信的。


花钱购买证书机构的签名
利用上述方法，受信任的机构就可以用自己的私钥(sign.key)对其他人的证书进行签名。我们看到，只需要把证书请求(ssl.csr)发给证书机构，证书机构就可以生成出签名过的证书(ssl.crt)

---
#### JDK中keytool常用命令
<http://www.cnblogs.com/kungfupanda/archive/2010/09/01/1815047.html>

#### Linux使用curl访问https站点时报错汇总
<http://www.linuxde.net/2014/12/15619.html>

java和php都可以编程来访问https网站。例如httpclient等。

其调用的CA根证书库并不和操作系统一致。

JAVA的CA根证书库是在 JRE的$JAVA_HOME/jre/lib/security/cacerts，该文件会随着JRE版本的升级而升级。可以使用keytool工具进行管理。


#### 将安全证书导入到java的cacerts证书库
<http://blog.csdn.net/wangjunjun2008/article/details/37662851>

#### 代码加载证书
<http://blog.chenxiaosheng.com/posts/2013-12-26/java-use-self_signed_certificate.html>

```java
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class LoadCert {
    public static void main(String[] args) throws Exception {

        X509TrustManager sunJSSEX509TrustManager;
        // 加载 Keytool 生成的证书文件
        char[] passphrase;
        String p = "changeit";
        passphrase = p.toCharArray();
        File file = new File("java.cnnic.cacert");
        System.out.println("Loading KeyStore " + file + "...");
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();
        // 构造 javax.net.ssl.TrustManager 对象
        TrustManagerFactory tmf =
        TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(ks);
        TrustManager tms [] = tmf.getTrustManagers();
        // 使用构造好的 TrustManager 访问相应的 https 站点
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tms, new java.security.SecureRandom());
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL myURL = new URL("https://replace.to.your.site.real.url/");
        HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
        int respInt = insr.read();
        while (respInt != -1) {
            System.out.print((char) respInt);
            respInt = insr.read();
        }
    }
}
```

`keytool -import -trustcacerts -alias casserver -keystore "%JAVA_HOME%/jre/lib/security/cacerts" -file F:\code\deploy\cer\cas.oa.vipshop.com.cer -storepass changeit`

##### HttpClient 设置信任证书(不用手动导入证书)
<http://blog.csdn.net/shenyunsese/article/details/41075579>
Utils4Java - httpclient

---
curl -ki https://localhost:8443/user/1
-k/--insecure	允许不使用证书到SSL站点

curl会通过服务器的证书来证明服务器声明的身份，如果验证失败curl将拒绝和这个服务器连接，
可以使用参数--insecure(-k)忽略服务器不能被验证。

<pre>
curl  https://localhost:8443/user/1
curl: (60) SSL certificate problem: Invalid certificate chain
More details here: http://curl.haxx.se/docs/sslcerts.html

curl performs SSL certificate verification by default, using a "bundle"
 of Certificate Authority (CA) public keys (CA certs). If the default
 bundle file isn't adequate, you can specify an alternate file
 using the --cacert option.
If this HTTPS server uses a certificate signed by a CA represented in
 the bundle, the certificate verification probably failed due to a
 problem with the certificate (it might be expired, or the name might
 not match the domain name in the URL).
If you'd like to turn off curl's verification of the certificate, use
 the -k (or --insecure) option.
</pre>

<pre>
curl -ki https://localhost:8443/user/1
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 27 Oct 2016 15:05:51 GMT

{"id":1,"name":"zhang"}%
</pre>

curl https://www.baidu.com/
则不会有证书问题

<pre>
curl -i https://www.baidu.com/
HTTP/1.1 200 OK
Server: bfe/1.0.8.18
Date: Thu, 27 Oct 2016 15:06:22 GMT
Content-Type: text/html
Content-Length: 2443
Connection: keep-alive
Last-Modified: Mon, 25 Jul 2016 11:13:23 GMT
ETag: "5795f453-98b"
Cache-Control: private, no-cache, no-store, proxy-revalidate, no-transform
Pragma: no-cache
Set-Cookie: BDORZ=27315; max-age=86400; domain=.baidu.com; path=/
Accept-Ranges: bytes
Set-Cookie: __bsi=11833468724267330491_00_282_N_N_2_0303_C027_N_N_N_0; expires=Thu, 27-Oct-16 15:06:27 GMT; domain=www.baidu.com; path=/

<!DOCTYPE html>
<!--STATUS OK--><html> <head><meta htt...
</pre>

HTTPS 本来就是基于http

---

Netty SSL性能调优
<http://weibo.com/ttarticle/p/show?id=2309404017836463922508>
Java 对SSL的支持

JDK7的client端只支持TLS1.0，服务端则支持TLS1.2。

JDK8完全支持TLS1.2。

JDK7不支持GCM算法。

JDK8支持GCM算法，但性能极差极差极差，按Netty的说法：

 Java 8u60以前多版本，只能处理1 MB/s。
 Java 8u60 开始，10倍的性能提升，10-20 MB/s。
 但比起 OpenSSL的 ~200 MB/s，还差着一个量级。
1.4 Netty 对SSL的支持

Netty既支持JDK SSL，也支持Google的boringssl， 这是OpenSSL 的一个fork，更少的代码，更多的功能。

依赖netty-tcnative-boringssl-static-linux-x86_64.jar即可，它里面已包含了相关的so文件，再也不用管Linux里装没装OpenSSL，OpenSSL啥版本了。

OpenSSL(boringssl)在我们的测试用例里，比JDK SSL 快10倍，10倍!!! 所以Netty下尽量都要使用OpenSSL。



---
在迁移到HTTPS之前需要考虑的事情
<https://zhuanlan.zhihu.com/p/25558110?from=timeline>
对于 QPS 比较高的入口, 从 HTTP 迁移到 HTTPS, 最重要的因素是要先判断这台机器的 TLS/SSL 计算能力. 你可能从一台 64G 内存的普通机器, 放个 nginx, 就能抗住百万并发, 迁移到 HTTPS 之后, CPU 的 load 完全就无法应对那么高的 TLS/SSL 握手 , 导致用户无法连接.
如何简单地判断从 HTTP 切换到 HTTPS 的成本呢? 主要看下面几点:

机器对 RSA 的私钥解密 (或签名) 的计算能力
是长连接还是短连接
机器对 AES 的计算能力
面向的客户端是否支持 SSL 的 session reuse

RSA 的解密速度 : 
`openssl speed rsa2048` 这个命令去检查所在物理机的解密速度

单核的 RSA 解密能力在 800 ~ 1000 左右, 所以一台 16 个物理核心(没超线程), 大致能接受的 HTTPS 新建连接数是 16k. 
OpenSSL 对 AES 对称加密的能力 很快

----
Let's Encrypt 使用教程，让你的网站拥抱 HTTPS:<http://mp.weixin.qq.com/s/vGYlu5K9df_MqlAX0fysbQ>


---

#### STS的header

+ Strict Transport Security (STS) 是用来配置浏览器和服务器之间安全的通信。
它主要是用来防止中间人攻击，因为它强制所有的通信都走TLS。目前IE还不支持 STS头。需要注意的是，在普通的http请求中配置STS是没有作用的，
因为攻击者很容易就能更改这些值。为了防止这样的现象发生，很多浏览器内置了一 个配置了STS的站点list。

+ 当用户浏览地址栏输入域名访问时，浏览器大部分缺省是通过HTTP访问的，这时我们的用户自己的网站就要做一个配置，做302的跳转，
所有的HTTP请求都让它访问HTTPS，这可以实现HTTPS的访问，如果仅仅 302 的话，以后的访问请求每次都需要从HTTP跳转到HTTPS，
多了一次 HTTP 请求的处理。对此，还需要有另外的配置，浏览器的访问跟随了跳转后，重新发起一个HTTPS请求，服务器除了正常给到内容外，
会多加一个STS的header，header标识告诉浏览器以后这个域名就要强制走HTTPS，并给出一个超大的超时时间。
<br/>
用户关掉浏览器，下次又要访问网站时，浏览器直接会把HTTP请求在内部转成HTTPS，实现了真正的HTTPS，
以后浏览器在这个超时时间内都会通过HTTPS进行访问。

<https://mp.weixin.qq.com/s/D62UqQye8NLbVzdwF5zjVQ>

+ 网站采用 HSTS 后，用户访问时无需手动在地址栏中输入 HTTPS，浏览器会自动采用 HTTPS 访问网站地址，从而保证用户始终访问到网站的加密链接，保护数据传输安全。
首先在服务器响应头中添加 HSTS 响应头：Strict-Transport-Security: max-age=expireTime [; includeSubDomains] [; preload] 
此响应头只有在 https 访问返回时才生效，其中[ ]中的参数表示可选；
☉ 设置 max-age 参数，时间设置不宜过长，建议设置时间为 6 个月；
☉ 当用户下次使用 HTTP 访问，客户端就会进行内部跳转，并且能够看到 307 Redirect Internel 的响应码；
☉ 网站服务器变成了 HTTPS 访问源服务器。

 
---
+ 也许这样理解 HTTPS 更容易:<https://mp.weixin.qq.com/s/O-A6D7VVu3eBDjyi7M7EbA>
+ HTTPS科普扫盲帖:<https://segmentfault.com/a/1190000004523659> 
+ 全站 HTTPS 来了:<http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=417>
+ 数字证书原理:<> 

+ 中间人攻击:<https://urlify.cn/zQj6f2>
+ 既然证书是公开的，如果要发起中间人攻击，我在官网上下载一份证书作为我的服务器证书，那客户端肯定会认同这个证书是合法的，如何避免这种证书冒用的情况？
其实这就是非加密对称中公私钥的用处，虽然中间人可以得到证书，但私钥是无法获取的，一份公钥是不可能推算出其对应的私钥，中间人即使拿到证书也无法伪装成合法服务端，因为无法对客户端传入的加密数据进行解密。
+ 只要客户端是我们自己的终端，我们授权的情况下，便可以组建中间人网络，而抓包工具便是作为中间人的代理。
<pre>
Q: 为什么需要证书？
A: 防止”中间人“攻击，同时可以为网站提供身份证明。

Q: 使用 HTTPS 会被抓包吗？
A: 会被抓包，HTTPS 只防止用户在不知情的情况下通信被监听，如果用户主动授信，是可以构建“中间人”网络，代理软件可以对传输内容进行解密。
</pre>  


---
NGINX 配置Https：https://blog.csdn.net/weixin_35884835/article/details/52588157

