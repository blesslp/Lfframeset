package com.liufan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {
	/**
	 * 匹配是否手机号码，是 True
	 * @param tel
	 * @return
	 */
	public static boolean matchTel(String tel){
		Pattern p = Pattern.compile("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");
		Matcher m = p.matcher(tel);
		return m.matches();
	}
	
	/**
	 * 匹配是否电子邮件，是True
	 * @param email
	 * @return
	 */
	public static boolean matchEmail(String email){
		Pattern p = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	/**
	 * 匹配是否中文，是True
	 * @param chinese
	 * @return
	 */
	public static boolean matchChinese(String chinese){
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(chinese);
		return m.matches();
	}
	
	public static void main(String[] args) {
		String tel = "15997466739";
		System.out.println(tel+"-- is Tel?:" + matchTel(tel));
	}
	
	
	
	
}
