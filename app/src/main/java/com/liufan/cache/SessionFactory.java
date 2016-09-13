package com.liufan.cache;

import android.content.Context;

public final class SessionFactory {

	public enum SessionType {
		/**
		 * 用于持久化一些小量的、临时的变量
		 */
		TYPE_DISK,
		/**
		 * 用于持久化网络返回的数据，适用于脱机操作的环境
		 */
		TYPE_SQLITE;
	};

	/**
	 * 对几种不同缓存策略统一进行初始化 <br>
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		DiskCache.init(context);
	}

	/**
	 * 用于生产缓存的工厂方法<br>
	 * 
	 * @param type
	 * <br>
	 *            {@link SessionType#TYPE_DISK} <br>
	 *            {@link SessionType#TYPE_SQLITE}
	 * @return 返回一个由SessionType指定的缓存类型
	 */
	public static Session getSession(SessionType type) {
		Session obj = null;
//		switch (type) {
//		case TYPE_DISK:
			obj = DiskCache.getInstance();
//			break;

//		}
		return obj;
	}
}
