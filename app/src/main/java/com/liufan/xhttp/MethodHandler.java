package com.liufan.xhttp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.liufan.utils.StringUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import rx.Observable;

public class MethodHandler {
	private NetAdapter adater;
	private Method m;
	private Annotation[] methodAnnotations;
	private RequestBuilder requestBuilder;
	private Annotation[][] parameterAnnotations;
	private Callback _callback;
	private boolean returnResponse; // 返回Response
	private boolean returnCall; // 返回Call
	private boolean returnVoid; // 返回空
	private boolean isObserver; //是否用Observer
	private String mappingMethod;

	public String getMappingMethod() {
		if (StringUtils.isEmpty(mappingMethod)) {
			return m.getName();
		}
		return mappingMethod;
	}
	
	public String toDebugString() {
		return requestBuilder.toString();
	}
	
	public void setMappingMethod(String mappingMethod) {
		this.mappingMethod = mappingMethod;
	}

	public boolean isReturnVoid() {
		return returnVoid;
	}

	public boolean isReturnResponse() {
		return returnResponse;
	}

	public boolean isObserver() {
		return isObserver;
	}

	public boolean isReturnCall() {
		return returnCall;
	}

	private Type genericReturnType;

	public Type getReturnType() {
		return genericReturnType;
	}

	public Callback getCallback() {
		return _callback;
	}

	MethodHandler(NetAdapter adater, Method m, Annotation[] methodAnnotations, Annotation[][] parameterAnnotations) {
		super();
		this.adater = adater;
		this.m = m;
		this.methodAnnotations = methodAnnotations;
		this.requestBuilder = new RequestBuilder();
		this.parameterAnnotations = parameterAnnotations;
		this.genericReturnType = m.getGenericReturnType();
		init();
	}

	private void init() {
		for (Annotation anno : methodAnnotations) {
			ParamHandler.doChain(this, anno, requestBuilder, null);
		}

		if (genericReturnType != null) {
			if(!(genericReturnType instanceof ParameterizedType)) {

				this.returnResponse = genericReturnType == Response.class;
				this.returnCall = genericReturnType == Call.class;
				this.returnVoid = genericReturnType == void.class;
			}else{
				try {
					final Class<?> aClass = Class.forName("rx.Observable");
					this.isObserver = ((ParameterizedType)genericReturnType).getRawType() == aClass;
				} catch (ClassNotFoundException e) {

				}
			}
		}





	}

	public Request toRequest() {
		if (requestBuilder != null) {
			return requestBuilder.build();
		}
		return null;
	}

	public void parseParameters(Object... args) {
		this._callback = null;
		requestBuilder.clearBody();
		requestBuilder.addParam(StringUtils.uuid(), "");
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			final Object obj = args[i];
			final Annotation[] annotations = this.parameterAnnotations[i];
			if (annotations == null || annotations.length == 0) {
				// 没有加任何注解，
				if (obj instanceof Callback) {
					// 是一个CallBack对象
					if (this._callback != null) {
						throw new IllegalArgumentException("只能指定一个Callback对象");
					}
					this._callback = (Callback) obj;
					continue;
				}
				throw new NullPointerException(String.format("你必须给%s类中的%s方法中的参数%s加注解",
						this.m.getDeclaringClass().getName(), this.m.getName(), args[i].getClass().getName()));
			}

			for (Annotation ann : annotations) {
				ParamHandler.doChain(this, ann, requestBuilder, obj);
			}
		}

		// for(Object obj : args) {
		// Class<? extends Object> clazz = obj.getClass();
		// Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
		// if(declaredAnnotations == null || declaredAnnotations.length==0) {
		// throw new
		// NullPointerException(String.format("你必须给%s类中的%s方法中的参数%s加注解",
		// this.m.getDeclaringClass().getName(),
		// this.m.getName(),
		// clazz.getName()));
		// }
		// for(Annotation ann : declaredAnnotations) {
		// ParamHandler.doChain(ann, this.requestBuilder, obj);
		// }
		// }
	}

	public static class Builder {
		private NetAdapter adater;
		private Method m;
		private Annotation[] methodAnnotations;
		private Annotation[][] parameterAnnotations;

		public Builder(NetAdapter adapt, Method m) {
			this.adater = adapt;
			this.m = m;
			this.methodAnnotations = m.getDeclaredAnnotations();
			this.parameterAnnotations = m.getParameterAnnotations();
		}

		public MethodHandler build() {
			return new MethodHandler(adater, m, methodAnnotations, parameterAnnotations);
		}
	}
}
