package com.liufan.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.LruCache;

import com.google.gson.reflect.TypeToken;
import com.liufan.utils.GsonUtils;

import java.util.Date;
/**
 * 在应用运行期间，会有一些小量对象需要持久化。<br>
 * 所有的put<K,V>操作均会把V持久化保存到 SharedPreferences<br>
 * 在取值的时候<br>
 * 对于简单类型（如：基本数据类型，Date,某个非泛型化对象）可以用get(K,Class)直接得到<br>
 * 对于复合型类型，如自定义泛型类（用于统一接口返回的数据）：
 * <pre>
 * public class BaseRequest<T> {
 * 	private int code;
 * 	private String msg;
 * 	private T data;
 * 	....setters and getters
 * }
 * </pre>
 * 以上对象在持久化后，被Gson转成原对象的时候，若直接使用get(K,Class)<br>
 * 则泛型类型T会被Gson默认的赋值为LinkedHashMap,使原BaseRequest的指定泛型出错<br>
 * 这种情况应该使用BaseRequest<List<Goods>>(){} request =  get(K,new TypeToken<BaseRequest<List<Goods>>(){});
 * 对应的返回数据格式如下:
 * <pre>
 * {
 * 	code : 0,
 * 	msg  : "商品列表获取成功",
 * 	data : [
 * 		{
 * 		...Goods 对象相关
 * 		},
 * 		{
 * 		...Goods 对象相关
 * 		}
 * 	]
 * }
 * </pre>
 * @author liufan
 */
@SuppressLint("NewApi")
public class DiskCache implements Session{
	private Cache mCache;
	private final String pref_name = "static_preference";
	protected SharedPreferences mPreference;
	protected Context mContext;
	private static boolean isInit;
	private static DiskCache _instance;
	
	/**
	 * 用作初始化DiskCache,初始化由{@link SessionFactory#init(Context) SessionFactory}统一做<br>
	 * 一般使用只需要调用{@link SessionFactory#getSession(com.liufan.cache.SessionFactory.SessionType) SessionFactory.getSession()}
	 * 
	 * @param context
	 */
	protected static void init(Context context) {
		if(_instance == null) {
			synchronized (DiskCache.class) {
				if(_instance == null) {
					_instance = new DiskCache(context,20);
					isInit = true;
				}
			}
		}
	}
	
	/**
	 * 静态方法，获取一个单例的DiskCache实例
	 * @return
	 */
	protected static DiskCache getInstance() {
		if(!isInit) throw new IllegalArgumentException("DiskCache未调用init初始化");
		return _instance;
	}
	
	private SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(pref_name,
				Context.MODE_PRIVATE);
	}

	private void putToSharedPrefences(String k,String v) {
		Editor edit = mPreference.edit();
		edit.putString(k, v);
//		edit.commit();
		edit.apply();
	}  
	
	private String getSharedPrefencesString(String k) {
		return mPreference.getString(k, "");
	}

	private DiskCache(Context context,int maxMemoryCount) {
		this.mCache = new Cache(maxMemoryCount);
		this.mPreference = getSharedPreferences(context);
		this.mContext = context;
	}


	
	protected class Cache extends LruCache<String, String> {
		//得到共享参数对象

		public Cache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected String create(String key) {
			return getSharedPrefencesString(key);
		}

		@Override
		protected int sizeOf(String key, String value) {
			return 1;
		}

	}

	/**
	 * 不支持android API的对象的存储<br>
	 * 此处存储的数据会被序列化为JSON并保存到SharedPreferences<br>
	 * 只建议保存数据实体或基本类型数据，否则可能会有不可预知的后果
	 * 
	 * @author liufan
	 */
	@Override
	public <V> void put(String k, V v) {
		Class<?> clazz = v.getClass();
		String value;
		if(clazz.equals(Date.class)) {
			Date tempDate = (Date)v;
			value = String.valueOf(tempDate.getTime());
		}else {
			value = GsonUtils.toJson(v);
		}
		putToSharedPrefences(k, value);
		mCache.put(k, value);
	}

	/**
	 * 必须为以下基本数据类型，否则可能会类型转换错误
	 * <ul>
	 *	<li>Integer</li>
	 *  <li> Byte</li>
	 *  <li> Long</li>
	 *  <li> Double  </li>
	 *  <li> Float</li>
	 *  <li> Character  </li>
	 *  <li> Short  </li>
	 *  <li> Boolean</li>
	 *  <li> BigDecimal</li>
	 *  <li> BigInteger</li>
	 *  <li> Date</li>
	 *  <li> 非泛型化的对象</li>
	 * </ul>
	 */
	@Override
	public <V> V get(String k, Class<V> clazz) {
		if(TextUtils.isEmpty(k)) {
			throw new IllegalArgumentException("DiskCache.get(key,clazz)  # key不能为empty ");
		}
		String v = mCache.get(k);  //从内存或者内存卡中取出一个对象
		if(TextUtils.isEmpty(v)) {
			return null;
		}
		if(clazz.equals(Date.class)) {
			long timestamp = GsonUtils.fromJson(v, Long.class);
			return (V) new Date(timestamp);
		}
			//基本数据类型直接返回
		return GsonUtils.fromJson(v, clazz);
		
	}
	
	@Override
	public <V> V get(String k, TypeToken<V> typeToken) {
		if(TextUtils.isEmpty(k)) {
			throw new IllegalArgumentException("DiskCache.get(key,clazz)  # key不能为empty ");
		}
		if(typeToken == null) {
			throw new IllegalArgumentException("DiskCache.get(key,typeToken)  # typeToken不能为null ");
		}
		String v = mCache.get(k);
		if(TextUtils.isEmpty(v)) {
			return null;
		}
		return GsonUtils.fromJson(v, typeToken.getType());
	}

	/**
	 * 移除操作，将同时移除SharedPreperences内的数据
	 * @author liufan
	 */
	@Override
	public void remove(String key) {
		mCache.remove(key);
		Editor edit = mPreference.edit();
		edit.remove(key);
//		edit.commit();
		edit.apply();
	}

	
	/**
	 * 移除全部，将同时移除全部保存在SharedPreperences的数据
	 * @author liufan
	 */
	@SuppressLint("NewApi")
	@Override
	public void removeAll() {
		mCache.evictAll();
		Editor edit = mPreference.edit();
		edit.clear();
		edit.apply();
	}

	
	@Override
	public String get(String k) {
		if(TextUtils.isEmpty(k)) {
			throw new IllegalArgumentException("DiskCache.get(key)  # key不能为empty ");
		}
		return mCache.get(k);  //从内存或者内存卡中取出字符串
	}

	@Override
	public <V> V get(String k, Class<V> clazz, V placeHolder) {
		V v = get(k, clazz);
		return v==null?placeHolder:v;
	}

}
