package com.liufan.utils;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class GsonUtils {

	private static Gson gson = new Gson();

	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static <T> T fromJson(String json, Type type) {
		if(String.class == type) {
			return (T) json;
		}
		return gson.fromJson(json, type);
	}

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static JsonElement toJsonTree(Object obj) {
		return gson.toJsonTree(obj);
	}

}
