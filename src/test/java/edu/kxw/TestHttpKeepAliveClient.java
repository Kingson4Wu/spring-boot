package edu.kxw;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Created by kingsonwu on 17/5/18.
 */
public class TestHttpKeepAliveClient {


    @Test
    public void testHeader() throws InterruptedException {
        String url = "http://localhost:8080/user/object?name=ds&id=2&";

        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();


        HttpGet httpGet = new HttpGet(url);
        //httpGet.addHeader("Connection", "Close");
        httpGet.addHeader("Keep-alive", "3000");

        //httpGet.addHeader("Connection", "keep-alive");

        try {
            //执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            System.out.println("status:" + httpResponse.getStatusLine());
            //判断响应实体是否为空
            if (entity != null) {
                System.out.println("contentEncoding:" + entity.getContentEncoding());
                String content = EntityUtils.toString(entity);
                System.out.println("response content:" + content);

            }


            Thread.sleep(5000);

            closeableHttpClient.execute(httpGet);

            Thread.sleep(1000);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

/**
 <pre>
 ~ netstat -na |grep 8080
 tcp4       0      0  127.0.0.1.8080         127.0.0.1.61869        ESTABLISHED
 tcp4       0      0  127.0.0.1.61869        127.0.0.1.8080         ESTABLISHED
 tcp46      0      0  *.8080                 *.*                    LISTEN
 </pre>

 */

/**
 *
 * https://my.oschina.net/flashsword/blog/80037
 * Keep-alive只是HTTP1.0时代对持久化连接的叫法，目前HTTP1.1已经默认所有请求都是持久化的，RFC规范是正确的。
 *
 * 头部不设置Connection: keep-alive，依然会进行持久化连接。
 *
 * 如果设置Connection:close，则不进行持久化连接。
 *
 * 目前连接过期时间在服务端设置，Keep-Alive头设置超时时间的做法已经不再有效。
 */