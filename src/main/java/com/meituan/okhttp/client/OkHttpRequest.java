package com.meituan.okhttp.client;

import com.meituan.okhttp.client.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Slf4j
public class OkHttpRequest {
	private static final String URL = "http://www.baidu.com";
	private static final MediaType MEDIA_TYPE_JSON = MediaType
			.parse("application/json; charset = utf-8");

	//OkHttpClient client = new OkHttpClient();
	private static OkHttpClient client = new OkHttpClient.Builder().build();

	public void syncGet() {//同步get方法
		Request request = new Request.Builder().url(URL).build();
		final Call call = client.newCall(request);

		//因为同步阻塞会阻塞主方法的运行，所以另起线程，当然也可以用线程池，还不如用异步get方法
		new Thread(new Runnable() {
			public void run() {
				try {
					Response response = call.execute();//调用call拿到返回结果
					log.info("syncGet: " + response.body().string());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void aSyncGet() {//异步get方法
		Request request = new Request.Builder()
				.url(URL)
				.get()//默认是get 可以不写
				.build();
		Call call = client.newCall(request);

		call.enqueue(new Callback() {//异步发起的请求会被加入到队列中通过线程池来执行。通过回调方式拿到结果
			public void onFailure(Call call, IOException e) {
				log.info("onFailure");
			}

			public void onResponse(Call call, Response response)
					throws IOException {
				Request request1 = call.request();//这个只是说明可以拿到request。
				log.info("request: " + request1);
				log.info("aSyncGet onSuccess: " + response.body().string());
			}
		});

	}

	public void aSyncPost() {//异步post方法
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
		Request request = new Request.Builder()
				.url(URL)
				.post(requestBody)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call,
			                      @NotNull IOException e) {
				log.info("onFailure: post");
			}

			@Override
			public void onResponse(@NotNull Call call,
			                       @NotNull Response response) throws IOException {
				log.info("onSuccess: post" + response.body().string());
			}
		});
	}

	public void testInterceptor() {//添加自定义拦截器
		OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build();
		Request request = new Request.Builder()
				.url("http://www.publicobject.com/helloworld.txt")
				.header("User-Agent", "OkHttp Example")
				.build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				log.info("onFailure: " + e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				ResponseBody body = response.body();
				if (body != null) {
					log.info("onResponse: " + response.body().string());
					body.close();
				}
			}
		});

	}
}
