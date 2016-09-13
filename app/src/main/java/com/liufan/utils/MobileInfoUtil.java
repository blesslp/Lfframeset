package com.liufan.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * 获取手机基本信息的工具类
 * 
 * @author Administrator
 * 
 */
public class MobileInfoUtil {

	/**
	 * 获取手机的串号
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		// 1.获取手机的imei号
		String uniqueId;
		uniqueId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();
		// 2.若为pad，获取不到imei，可以获取android_id
		if (uniqueId == null || "".equals(uniqueId)) {
			uniqueId = Secure.getString(
					context.getContentResolver(),
					Secure.ANDROID_ID);
		}
		return uniqueId;
	}

	/**
	 * 获取手机号码(不能百分百获取到)
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String number = tm.getLine1Number();
		return number;
	}

	/**
	 * imsi是每张sim卡的唯一编号。手机号不能百分百取到，但只要手机有sim卡就能获取到imsi
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSimSerialNumber();
		return imsi;
	}

	public static String getVersionName(Context context) throws Exception{   
		//获取packagemanager的实例     
		PackageManager packageManager = context.getPackageManager();   
		//getPackageName()是你当前类的包名，0代表是获取版本信息    
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);   
		return packInfo.versionName;    
	}  
	
	public static void installApk(Context context,File file) {  
	    Intent intent = new Intent();  
	    //执行动作   
	    intent.setAction(Intent.ACTION_VIEW);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    //执行的数据类型   
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了    
	    context.startActivity(intent);  
	}  
	
}
