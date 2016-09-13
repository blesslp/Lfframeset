package com.liufan.xhttp;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liufan.utils.GsonUtils;
import com.liufan.utils.StringUtils;
import com.liufan.xhttp.annotation.FileParam;
import com.liufan.xhttp.annotation.Get;
import com.liufan.xhttp.annotation.Header;
import com.liufan.xhttp.annotation.JsonParam;
import com.liufan.xhttp.annotation.MapParam;
import com.liufan.xhttp.annotation.Mapping;
import com.liufan.xhttp.annotation.Param;
import com.liufan.xhttp.annotation.Post;

public abstract class ParamHandler {

	protected static List<? extends ParamHandler> chainList;

	static {
		chainList = Arrays.asList(new MappingHandler(), new PostHandler(),
				new GetHandler(), new FieldHandler(), new FileHandler(),
				new HeaderHandler(), new MapParamHandler(),
				new JsonParamHandler());
	}

	final static void doChain(MethodHandler methodHandler, Annotation anno,
			RequestBuilder builder, Object arg) {
		for (ParamHandler param : chainList) {
			if (param.filter(anno)) {
				param.apply(methodHandler, anno, builder, arg);
			}
		}
	}

	public abstract void apply(MethodHandler methodHandler,
			Annotation annotation, RequestBuilder builder, Object arg);

	public abstract boolean filter(Annotation anno);

	public static class MappingHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder builder, Object arg) {
			Mapping mapping = (Mapping) annotation;
			methodHandler.setMappingMethod(mapping.value());
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == Mapping.class;
		}

	}

	public static class PostHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder request, Object arg) {
			final String url = ((Post) annotation).value();
			final String baseURL = NetAdapter.getBaseURL();
			request.setUrl(Utils.comboURL(baseURL, url));
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == Post.class;
		}

	}

	public static class GetHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder request, Object arg) {
			final String url = ((Get) annotation).value();
			final String baseURL = NetAdapter.getBaseURL();
			request.setUrl(Utils.comboURL(baseURL, url));
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == Get.class;
		}
	}

	public static class FileHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder builder, Object arg) {
			if(arg == null)return;
			FileParam fileAnno = (FileParam) annotation;
			String formName = fileAnno.formName();
			Utils.checkNotNull(formName, "@File注解必须指定formName");
			String fileName = fileAnno.fileName();
			if (arg instanceof File) {
				File file = (File) arg;
				fileName = StringUtils.isEmpty(fileName) ? file.getName()
						: fileName;
				builder.addPart(formName, fileName, file);
			} else if (arg instanceof byte[]) {
				fileName = StringUtils.isEmpty(fileName) ? System.nanoTime()
						+ ".png" : fileName;
				builder.addPart(formName, fileName, (byte[]) arg);
			}
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == FileParam.class;
		}

	}

	public static class FieldHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder request, Object arg) {
			final String key = ((Param) annotation).value();
			if (arg instanceof File) {
				if (arg == null)
					return;
				File file = (File) arg;
				request.addPart(key, file.getName(), file);
			} else if (arg instanceof byte[]) {
				if (arg == null)
					return;
				request.addPart(key, System.nanoTime() + ".png", (byte[]) arg);
			} else {

				final String defaultValue = ((Param) annotation).defaultValue();
				if (arg == null && !StringUtils.isEmpty(defaultValue)) {
					request.addParam(key, defaultValue);
					return;
				}
				if(arg == null)return;
				request.addParam(key, String.valueOf(arg));
			}

		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == Param.class;
		}

	}

	public static class HeaderHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder request, Object arg) {
			final String[] headerString = ((Header) annotation).value();
			for (String header : headerString) {
				final String key = header.split("=")[0];
				final String value = header.split("=")[1];
				request.addHeader(key, value);
			}
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == Header.class;
		}

	}

	public static class JsonParamHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder builder, Object arg) {
			if (arg == null)
				return;
			final String key = ((JsonParam) annotation).value();
			if (arg != null) {
				if (arg.getClass() == String.class) {
					builder.addParam(key, arg.toString());
				} else {
					builder.addParam(key, GsonUtils.toJson(arg));
				}
			}
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == JsonParam.class;
		}
	}

	public static class MapParamHandler extends ParamHandler {

		@Override
		public void apply(MethodHandler methodHandler, Annotation annotation,
				RequestBuilder builder, Object arg) {
			if (arg == null)
				return;
			Class<?> clazz = arg.getClass();
			if (Collection.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format(
						"方法：%s 中 @MapParam不能是集合，且必须是一个单层的JavaBean",
						methodHandler.getMappingMethod()));
			}
			JsonObject obj = GsonUtils.toJsonTree(arg).getAsJsonObject();
			Set<Entry<String, JsonElement>> entrySet = obj.entrySet();
			for (Entry<String, JsonElement> entry : entrySet) {
				final String key = entry.getKey();
				final JsonElement value = entry.getValue();
				if (value != null && !value.isJsonNull()) {
					builder.addParam(key, value.getAsString());
				}
			}
		}

		@Override
		public boolean filter(Annotation anno) {
			return anno.annotationType() == MapParam.class;

		}

	}

}
