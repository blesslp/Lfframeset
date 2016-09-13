package com.liufan.xhttp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.liufan.utils.GsonUtils;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;

public class RequestBuilder {

	private String reqUrl;
	private MultipartBuilder multipartBuilder;
	private Request.Builder builder;

	private Map<String, String> debugBody = new LinkedHashMap<String, String>();

	@Override
	public String toString() {
		return GsonUtils.toJson(debugBody);
	}

	public void clearBody() {
		multipartBuilder = null;
		setInitRequest();
		Request.Builder temp = new Request.Builder();
		initBuilder(temp);
		builder = temp;
	}

	public void setUrl(String url) {
		Utils.checkNotNull(url, "URL地址不能为空");
		this.reqUrl = url;
		setInitRequest();
		
	}

	private void setInitRequest() {
		if (multipartBuilder == null) {
			multipartBuilder = new MultipartBuilder();
			multipartBuilder.type(MultipartBuilder.FORM);
			debugBody.clear();
			debugBody.put("url", reqUrl);
		}
		if (builder == null) {
			builder = new Request.Builder();
		}
	}

	public void addParam(String key, String value) {
		setInitRequest();
		if (key == null || "".equals(key))
			return;
		multipartBuilder.addFormDataPart(key, value);
//		multipartBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), 
//				RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), key));
		debugBody.put(key, value);
	}

	public void addPart(String key, String fileName, File file) {
		setInitRequest();
		multipartBuilder.addFormDataPart(key, fileName,
				RequestBody.create(MediaType.parse("application/octet-stream"), file));
		debugBody.put(key, "文件:"+fileName);
	}

	public void addPart(String key, String fileName, byte[] file) {
		setInitRequest();
		multipartBuilder.addFormDataPart(key, fileName,
				RequestBody.create(MediaType.parse("application/octet-stream"), file));
		debugBody.put(key, "文件流:"+fileName);
	}

	private Map<String, String> headers = new HashMap<String, String>();

	public void addHeader(String key, String value) {
		setInitRequest();
		headers.put(key, value);
	}

	private CacheControl cacheControl;

	public void setCache(boolean cache) {
		cacheControl = cache ? new CacheControl.Builder().onlyIfCached().build() : CacheControl.FORCE_NETWORK;
	}

	public Request build() {
		initBuilder(builder);
		return builder.post(multipartBuilder.build()).build();
	}

	private void initBuilder(Request.Builder builder) {
		if (cacheControl != null) {
			builder.cacheControl(cacheControl);
		}
		Set<Entry<String, String>> entrySet = headers.entrySet();
		for (Entry<String, String> entry : entrySet) {
			builder.addHeader(entry.getKey(), entry.getValue());
		}
		builder.url(reqUrl);
	}

}
