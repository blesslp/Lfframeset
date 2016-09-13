package com.liufan.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class StringUtils {
	
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static boolean isEmpty(String dest) {
		return dest==null||"".equals(dest);
	}
	
	public static String thenNull(String dest,String placeholder) {
		return isEmpty(dest)?placeholder:dest;
	}
	
	public static String thenNull(String dest) {
		return thenNull(dest,"");
	}
	
	public static String join(List<String> strArray,String prefix) {
		prefix = isEmpty(prefix)?",":prefix;
		StringBuilder sb = new StringBuilder();
		for(String string : strArray) {
			sb.append(string).append(prefix);
		}
		int lastIndexOf = sb.lastIndexOf(prefix);
		if(lastIndexOf == -1) {
			return sb.toString();
		}
		return sb.substring(0, lastIndexOf);
	}
	
	public static String join(String...strings) {
		return join(Arrays.asList(strings),null);
	}
	
	
}
