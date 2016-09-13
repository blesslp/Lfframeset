package com.liufan.xhttp;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.os.Build;

import com.liufan.utils.GsonUtils;
import com.liufan.utils.HttpUtils;
import com.liufan.xhttp.utils.RxJavaAdapt;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NetAdapter {
	private Map<Method, MethodHandler> methodCache = new LinkedHashMap<Method, MethodHandler>();
	private WeakReference<Object> subscribe;
	private static DealVoidCallBack _dealVoidCallBack = new DealVoidCallBack();
	private static String baseURL;
	private static List<Interceptor> interceptors = new ArrayList<Interceptor>();

	public static String getBaseURL() {
		return baseURL;
	}

	public static void initBaseURL(String URL) {
		baseURL = URL;
	}
	
	public static void addInterceptor(Interceptor i) {
		for (Interceptor inter : interceptors) {
			if (inter.getClass() == i.getClass()) {
				return;
			}
		}
		interceptors.add(i);
	}

	NetAdapter(Object subscribe) {
		this.subscribe = new WeakReference<Object>(subscribe);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> clazz) {
		Utils.validateServiceInterface(clazz);
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class[] { clazz }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, final Method method,
							Object[] args) throws Throwable {
						if (method.getDeclaringClass() == Object.class) {
							return method.invoke(this, args);
						}
						MethodHandler loadMethodHandler = loadMethodHandler(method);
						loadMethodHandler.parseParameters(args);
						Request request = loadMethodHandler.toRequest();
						
						
						final String mappingMethod = loadMethodHandler.getMappingMethod();
						final String url = request.urlString();
						final Type returnType = loadMethodHandler.getReturnType();
						boolean isAccept= true;
						if(interceptors != null && interceptors.size() > 0) {
							//挂载拦截器
							for(Interceptor interceptor : interceptors) {
								isAccept = interceptor.accept(mappingMethod, request.urlString());
								if(!isAccept) {
									Logger.wtf("请求:%s，被:%s拦截，将不会响应", request.urlString(),interceptor.getClass().getName());
									break;
								}
							}

						}
						
						//挂载拦截器

						final String json = loadMethodHandler.toDebugString();
						Logger.wtf("请求:%s", json);

						Call post = HttpUtils.POST(request);
						if (loadMethodHandler.getCallback() != null) {
							post.enqueue(loadMethodHandler.getCallback());
							return null;
						}
						if (loadMethodHandler.isReturnCall()) {
							return post;
						}
						if (loadMethodHandler.isReturnResponse()) {
							return post.execute();
						}
						if (loadMethodHandler.isReturnVoid()) {
							post.enqueue(_dealVoidCallBack);
							return null;
						}
						if (loadMethodHandler.isObserver()) {
							return RxJavaAdapt.adaptRx(post,returnType);
						}

						if(!isAccept) {
							//直接回调回去
							Object obj = subscribe.get();
							if(obj == null)return null;
							Method targetMethod = getMethod(mappingMethod, returnType,
									obj.getClass());
							invokeMethod(obj, (Object) null, targetMethod);
							return null;
						}
						if (subscribe.get() != null) {
							post.enqueue(new SubScriber(post, subscribe.get(),
									loadMethodHandler));
						}
						return null;

					}
				});
	}

	MethodHandler loadMethodHandler(Method m) {
		MethodHandler handler = null;
		synchronized (methodCache) {
			handler = methodCache.get(m);
			if (handler == null) {
				handler = new MethodHandler.Builder(this, m).build();
				methodCache.put(m, handler);
			}
		}
		return handler;
	}
	
	static Method getMethod(String mappingMethod, Type returnType,
			Class<?> obj) throws NoSuchMethodException, SecurityException {
		Method declaredMethod = obj.getDeclaredMethod(mappingMethod,
				Utils.getRawType(returnType));
		return declaredMethod;
	}
	
	static void invokeMethod(final Object obj, final Object fromJson,
			final Method method) {
		final Platform platform = Platform.get();
		if (platform != null) {
			platform.getExecutor().execut(new Runnable() {
				@Override
				public void run() {
					try {
						method.invoke(obj, fromJson);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public static class Builder {
		private Object subscribe;

		public Builder setSubScribe(Object subscribe) {
			this.subscribe = subscribe;
			return this;
		}

		

		public NetAdapter build() {
			return new NetAdapter(subscribe);
		}

	}

	private static class SubScriber extends DealVoidCallBack {
		WeakReference<Object> subScriber;
		WeakReference<MethodHandler> methodHandler;
		WeakReference<Call> call;
		private final Platform platform = Platform.get();

		public SubScriber(Call call, Object subscrbe, MethodHandler handler) {
			this.subScriber = new WeakReference<Object>(subscrbe);
			this.methodHandler = new WeakReference<MethodHandler>(handler);
			this.call = new WeakReference<Call>(call);
		}

		@Override
		public void onResponse(Response res) throws IOException {
			MethodHandler mh = methodHandler.get();
			Object obj = subScriber.get();
			if (mh == null || obj == null) {
				return;
			}
			String mappingMethod = mh.getMappingMethod();
			Type returnType = mh.getReturnType();
			try {
				if (res.isSuccessful()) {
					final String ret = res.body().string();
					Logger.wtf("返回：%s", ret);
					Object fromJson = null;
					try {
						fromJson = GsonUtils.fromJson(ret, returnType);
					} catch (Exception e) {
						e.printStackTrace();
						throw new IllegalAccessError(String.format(
								"%s解释失败,错误原因:%s", ret, e.getMessage()));
					}
					Method method = getMethod(mappingMethod, returnType,
							obj.getClass());
					invokeMethod(obj, fromJson, method);
				} else {
					Method method = getMethod(mappingMethod, returnType,
							obj.getClass());
					invokeMethod(obj, (Object) null, method);
				}
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(String.format("%s中未找到方法：%s", obj
						.getClass().getName(), mappingMethod));
			}catch(SecurityException e) {
				throw new RuntimeException(String.format("%s中未找到方法：%s", obj
						.getClass().getName(), mappingMethod));
			}
		}

		

		

		@Override
		public void onFailure(Request res, IOException ioE) {
			MethodHandler mh = methodHandler.get();
			Object obj = subScriber.get();
			if (mh == null || obj == null) {
				return;
			}
			String mappingMethod = mh.getMappingMethod();
			Type returnType = mh.getReturnType();
			try {
				Method method;
				method = getMethod(mappingMethod, returnType, obj.getClass());
				invokeMethod(obj, (Object) null, method);
			} catch (Exception e) {
				throw new RuntimeException(String.format("%s中未找到方法：%s", obj
						.getClass().getName(), mappingMethod));
			}
			// finally {
			// Call c = call.get();
			// if (c != null && !c.isCanceled()) {
			// c.cancel();
			// }
			// }

		}
	}

	private static class DealVoidCallBack implements Callback {
		@Override
		public void onFailure(Request arg0, IOException arg1) {
		}

		@Override
		public void onResponse(Response arg0) throws IOException {
		}
	}

}
