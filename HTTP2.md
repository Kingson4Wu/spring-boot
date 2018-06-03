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
1. 二进制格式传输：HTTP/2 采用二进制格式传输数据，而非 HTTP/1.x 的文本格式。二进制格式在协议的解析和优化扩展上带来更多的优势和可能。
2. http2的请求头压缩: HTTP/2 对消息头采用 HPACK 进行压缩传输，能够节省消息头占用的网络的流量。而 HTTP/1.x 每次请求，都会携带大量冗余头信息，浪费了很多带宽资源。头压缩能够很好的解决该问题。
3. http2的多路复用: 直白的说就是所有的请求都是通过一个 TCP 连接并发完成。HTTP/1.x 虽然通过 pipeline 也能并发请求，但是多个请求之间的响应会被阻塞的，所以 pipeline 至今也没有被普及应用，而 HTTP/2 做到了真正的并发请求。同时，流还支持优先级和流量控制。
4. http2的服务端推送: 服务端能够更快的把资源推送给客户端。例如服务端可以主动把 JS 和 CSS 文件推送给客户端，而不需要客户端解析 HTML 再发送这些请求。当客户端需要的时候，它已经在客户端了。
+ 重用连接：HTTP/2协议本身能够复用tcp连接，可以减少创建连接和减少无效连接。HTTP/1.x 使用keep-alive实现。

---
####  HTTP/2协议特性

+ HTTP/2也是非常关键的协议，在2015年已经正式的公布了，它是为了解决HTTP原先版本的低效不安全等问题而产生，并不是为了要完全颠覆HTTP,
而是在HTTP基础上做了加强，它的特性有二进制协议，支持头部压缩，多路复用以及服务器推送。服务器推送指在Client端发送请求时，
Server端会根据Client请求来做一些判断，会把Client请求中页面包含的一些资源提前推送给Client端，提升了传输效率。
HTTP/2上更主要的是加强协议的安全性。
+ HTTP/2头已经变成二进制格式，并且分为消息头、消息体都封装成二进制格式传输。
+ 头部压缩是HTTP/2中非常重要的特点，它针对同一个Client和一个server之间进行数据传输时，有一些header在多次的请求中是相同的，
这样多次请求就会出现多次传输相同的 header。HTTP/2协议针对这种情况对所有header信息建立索引，
如果在下一次传输时相同的header直接用索引的编号去传输，这样就不会传输一长串的字符串，减少了网络传输信息量，提升了传输效率。
当然，头部压缩也存在一些缺点 ，因为不管是Client端还是server端，都要维持索引表，确定每个索引值对应HTTP header的信息，
通过占用更多内存换取数据量传输的减少，也可以认为是通过空间换时间。对于现在内存日益扩大的情况下，增加传输效率才是更重要的。
+ HTTP/2协议多路复用的功能，HTTP之前的版本最多支持keep live，可以在一个TCP连接上传输多个HTTP请求，对于最基本的keep live，
只能在一个请求传输完进行下一个请求的传输，以及在这个基础上还有pipeline，可以在请求方向上同时传输多个get请求，但都不是真正的多路复用。
HTTP/2在 TCP 连接的基础上，增加了stream的概念，每个 stream 都可以处理单独的一个 HTTP 请求。
在这个基础上，在一条TCP连接上可以同时传输多个 stream，而且不同 stream都有对应编号。因此就支持了真正的多路复用。

#### 优化方法如下：

减少握手：SSL Session ID/Session Ticket，TCP KeepAlive也是需要的；
HTTP/2：多路复用和头部压缩可以有效提升数据传输效率；
域名合并：减少 SSL 握手，提升重用；
协议栈优化：调整TCP 初始化窗口，快速重传；
优先算法：ECDSA > RSA。

<https://mp.weixin.qq.com/s/D62UqQye8NLbVzdwF5zjVQ>

---
+ http2 协议: <http://www.jianshu.com/p/47d02f10757f>
+ HTTP/2 资料汇总:<https://imququ.com/post/http2-resource.html>
