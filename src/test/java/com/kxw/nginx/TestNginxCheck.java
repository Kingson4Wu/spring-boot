package com.kxw.nginx;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

/**
 * Created by kingsonwu on 18/7/22.
 */
public class TestNginxCheck {

    @Test
    public void testNginxCheckout() {
        int failCount = 0;
        int[] count = new int[4];
        Map<String, Integer> map = new HashMap<>();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url("http://nginx.inner.proxy.com/nginx/hello")
                .build();

            long stopTime = Instant.now().plusSeconds(120).toEpochMilli();
            while (System.currentTimeMillis() < stopTime) {
                Response response = client.newCall(request).execute();

                System.out.println("code: " + response.code() + ", remote: " + response.header("X-Application-Context")
                    + ", resp:" + response.body().string());
                if (response.code() != 200) {
                    failCount++;
                }
                if (Objects.nonNull(response.header("X-Application-Context"))) {
                    count[Integer.valueOf(response.header("X-Application-Context").split(":")[1]) - 8081]++;
                    if (map.containsKey(response.header("X-Application-Context"))) {
                        map.put(response.header("X-Application-Context"), map.get(response.header(
                            "X-Application-Context"))
                            + 1);
                    } else {
                        map.put(response.header("X-Application-Context"), 1);
                    }
                }

            }

            System.out.println("count: " + failCount);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < count.length; i++) {
                System.out.println(count[i]);
            }

            for (Entry<String, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        }

    }

    /**
     * upstream nginx.inner.proxy.com {
     * server localhost:8081 weight=999 max_fails=5 fail_timeout=10s;
     * server localhost:8082 weight=999 max_fails=5 fail_timeout=10s;
     * server localhost:8083 weight=1 max_fails=5 fail_timeout=10s backup;
     * server localhost:8084 weight=1 max_fails=5 fail_timeout=10s backup;
     * check interval=3000 rise=2 fall=3 timeout=2500 type=tcp;# 每3s检查一次，成功2次标记up，失败3>次则标记down，超时时间2.5s
     * keepalive 32;
     *
     * }
     *
     * less 808* |grep 'a6ba8bff-146f-40b4-81d2-0007e8adefd0'
     *
     * 502 的情况并不会重试...
     *
     * type= tcp,50x 是http,并不会剔除
     *
     */
    @Test
    public void testNginx50xRetry() {

        try {

            OkHttpClient client = new OkHttpClient();
            long stopTime = Instant.now().plusSeconds(10).toEpochMilli();
            while (System.currentTimeMillis() < stopTime) {
                String uuid = UUID.randomUUID().toString();
                Request request = new Request.Builder()
                    .url("http://nginx.inner.proxy.com/nginx/serverSuccessOrError/502?uuid=" + uuid)
                    .build();
                Response response = client.newCall(request).execute();

                System.out.println("code: " + response.code() + ", remote: " + response.header("X-Application-Context")
                    + ", resp:" + response.body().string() + " , " + uuid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    @Test
    public void testNginx50xOut() {

        try {

            OkHttpClient client = new OkHttpClient();
            long stopTime = Instant.now().plusSeconds(10).toEpochMilli();
            while (System.currentTimeMillis() < stopTime) {
                String uuid = UUID.randomUUID().toString();
                Request request = new Request.Builder()
                    .url("http://nginx.inner.proxy.com/nginx/server50xOut/502?uuid=" + uuid)
                    .build();
                Response response = client.newCall(request).execute();

                System.out.println("code: " + response.code() + ", remote: " + response.header("X-Application-Context")
                    + ", resp:" + response.body().string() + " , " + uuid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }


}
