<http://genchilu-blog.logdown.com/posts/746243>
雖然 http/2 沒規定一定要加密協定（例如 SSL），但目前大部分瀏覽器的 http/2 都需要跑在 https 上面。

<https://vanwilgenburg.wordpress.com/2016/04/01/spring-boot-http2/>

实践个人网站迁移HTTPS与HTTP/2
<https://www.freemindworld.com/blog/2016/160301_migrate_to_https_and_http2.shtml>


HTTP,HTTP2.0,SPDY,HTTPS你应该知道的一些事
<http://www.alloyteam.com/2016/07/httphttp2-0spdyhttps-reading-this-is-enough/>
HTTP2.0的前世今生
顾名思义有了HTTP1.x，那么HTTP2.0也就顺理成章的出现了。HTTP2.0可以说是SPDY的升级版（其实原本也是基于SPDY设计的），但是，HTTP2.0 跟 SPDY 仍有不同的地方，主要是以下两点：
HTTP2.0 支持明文 HTTP 传输，而 SPDY 强制使用 HTTPS
HTTP2.0 消息头的压缩算法采用 HPACK，而非 SPDY 采用的 DEFLATE


<http://genchilu-blog.logdown.com/posts/746243>
http/2 的 Request and Response Multiplexing 和 server push 功能測試
http/2 在 TCP/IP 四層中的 Application 層中多塞了一層 Binnary Framing Layer，而這機制也改變了 server 和 client 交換資料的方式：

Request and Response Multiplexing
server push


#### Mac OS Curl HTTP/2 支持
` brew install curl --with-nghttp2`

<pre>
/usr/local/Cellar/curl/7.50.3/bin/curl --http2 -kI  https://localhost:8443/user/1
HTTP/2 200
server: Jetty(9.3.10.v20160621)
date: Sun, 30 Oct 2016 02:08:46 GMT
content-type: application/json;charset=UTF-8
content-length: 23
</pre>

linux:<https://www.sysgeek.cn/curl-with-http2-support/>

#### HTTP2 资料
<https://imququ.com/post/http2-resource.html>


---
http2的请求头压缩
http2的多路复用
http2的服务端推送

