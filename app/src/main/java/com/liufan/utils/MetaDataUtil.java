package com.liufan.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public final class MetaDataUtil {

	/**
	 * 获取 Application节点内的meta-data
	 * @param context    Application节点上下文 
	 * @param dataName	 对应的键值
	 * @return  返回value
	 * @throws NameNotFoundException
	 */
	public static String getDataFromApplication(Context context,String dataName) throws NameNotFoundException {
		ApplicationInfo appInfo = context.getPackageManager()
				.getApplicationInfo(context.getPackageName(),
						PackageManager.GET_META_DATA);
		Bundle bundle = appInfo.metaData;
		return bundle!=null?bundle.getString(dataName):null;
	}
	
	
}
