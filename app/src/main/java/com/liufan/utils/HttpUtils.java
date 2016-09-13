package com.liufan.utils;

import java.io.IOException;
import java.net.CookieManager;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@TargetApi(value=9)
public final class HttpUtils {

	private static OkHttpClient _httpClient = new OkHttpClient();

	static {
		_httpClient.setRetryOnConnectionFailure(false);
		_httpClient.setReadTimeout(30, TimeUnit.SECONDS);
		_httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
		_httpClient.setWriteTimeout(3, TimeUnit.MINUTES);
		_httpClient.setCookieHandler(new CookieManager());
	}

	public static Call POST(Request req) {
		return _httpClient.newCall(req);
	}
	
	
	public static String POST_MIX(String url, Map<String, String> params) {
		try {
			MultipartBuilder builder = null;
			if (params != null && params.size() > 0) {
				builder = new MultipartBuilder();
				builder.type(MediaType.parse("multipart/form-data"));
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					builder.addFormDataPart(entry.getKey(), entry.getValue());
				}
			}
			Request.Builder req = new Request.Builder();
			req.url(HttpUrl.parse(url));
			if (builder != null) {
				req.post(builder.build());
			}
			Call post = POST(req.build());
			Response execute = post.execute();
			if (execute.isSuccessful()) {
				return execute.body().string();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	

	public static String POST(String url, Map<String, String> params) {
		try {
			FormEncodingBuilder builder = null;
			if (params != null && params.size() > 0) {
				builder = new FormEncodingBuilder();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					builder.add(entry.getKey(), entry.getValue());
				}
			}
			Request.Builder req = new Request.Builder();
			req.url(HttpUrl.parse(url));
			if (builder != null) {
				req.post(builder.build());
			}
			Call post = POST(req.build());
			Response execute = post.execute();
			if (execute.isSuccessful()) {
				return execute.body().string();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void POST(String url, Map<String, String> params, Callback callback) {
		FormEncodingBuilder builder = null;
		if (params != null && params.size() > 0) {
			builder = new FormEncodingBuilder();
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				builder.add(entry.getKey(), entry.getValue());
			}
		}
		Request.Builder req = new Request.Builder();
		req.url(HttpUrl.parse(url));
		if (builder != null) {
			req.post(builder.build());
		}
		Call post = POST(req.build());
		post.enqueue(callback);
	}

}
