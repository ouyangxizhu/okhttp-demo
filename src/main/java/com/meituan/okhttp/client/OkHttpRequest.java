package com.meituan.okhttp.client;

import com.meituan.okhttp.client.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
@Slf4j
public class OkHttpRequest {
    public static final String URL = "http://www.baidu.com";
    public static final MediaType MEDIA_TYPE_JSON = MediaType
            .parse("application/json; charset = utf-8");

    //OkHttpClient client = new OkHttpClient();
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Test
    public void aSyncGet () {
        Request request = new Request.Builder()
                .url(URL)
                .get()//默认是get 可以不写
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {//异步发起的请求会被加入到 Dispatcher 中的 runningAsyncCalls双端队列中通过线程池来执行。
            public void onFailure(Call call, IOException e) {
                 System.out.println("onFailure");

            }

            public void onResponse(Call call, Response response)
                    throws IOException {
                 System.out.println("aSyncGet onSuccess: " + response.body().string());
            }
        });

    }

    @org.junit.Test
    public void syncGet() {
        Request request = new Request.Builder().url(URL).build();
        final Call call = client.newCall(request);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Response response = call.execute();

                    log.info("syncGet: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void aSyncPost() {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType
                        .parse("application/json; charset = utf-8");
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink)
                    throws IOException {
                bufferedSink.write("zcz".getBytes());

            }
        };
        Request request = new Request.Builder().url(URL).post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NotNull Call call,
                    @NotNull IOException e) {
                 System.out.println("onFailure: post");
            }

            @Override public void onResponse(@NotNull Call call,
                    @NotNull Response response) throws IOException {
                 System.out.println("onSuccess: post" + response.body().string());
            }
        });
    }

    public void testInterceptor (){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build();
        Request request = new Request.Builder()
                .url("http://www.publicobject.com/helloworld.txt")
                .header("User-Agent", "OkHttp Example")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    System.out.println("onResponse: " + response.body().string());
                    body.close();
                }
            }
        });


    }
}
