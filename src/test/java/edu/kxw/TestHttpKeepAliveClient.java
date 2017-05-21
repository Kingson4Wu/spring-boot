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
