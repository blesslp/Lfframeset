package com.liufan.cache;

import com.google.gson.reflect.TypeToken;

/**
 * {@inheritDoc}
 * @author liufan
 *
 */
public interface Session {
	/**
	 * 存入缓存数据
	 * @param k   键
	 * @param v	  值
	 */
	public <V> void put(String k, V v);
	
	/**
	 * 获取缓存的数据
	 * @param k			键
	 * @param clazz		要转化的类型
	 * @return 			此处返回的类型与该方法第二个参数传入类型相同
	 */
	public <V> V get(String k, Class<V> clazz);
	
	/**
	 * 获取缓存，可以设置为空值
	 */
	public <V> V get(String k, Class<V> clazz, V placeHolder);
	
	/**
	 * 获取泛型化的缓存数据
	 * @param k				键
	 * @param typeToken		TypeToken是Gson的API，用于得到泛型真实类型。用法如:new TypeToken<List<XXBean>>(){}
	 * @return				返回typeToken的泛型指定的类型
	 */
	
	public <V> V get(String k, TypeToken<V> typeToken);
	
	/**
	 * 移除缓存中key映射的数据
	 * @param key
	 */
	public void remove(String key);
	
	/**
	 * 移除所有的缓存数据
	 */
	public void removeAll();
	
	/**
	 * 为兼容DBCache
	 */
	
	public String get(String k) ;
}
