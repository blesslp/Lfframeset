package liufan.dev.view.annotation.processor;

import com.liufan.xhttp.NetAdapter;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

import liufan.dev.view.annotation.InjectSrv;

/**
 * Created by liufan on 16/5/9.
 */
public class InjectSrvProcessor {
    public static void process(Object object) {
        final Class<?> aClass = object.getClass();
        final Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(InjectSrv.class)) {
                final InjectSrv annotation = declaredField.getAnnotation(InjectSrv.class);
                Class<?> intf = annotation.value();
                final Object intfProxy = new NetAdapter.Builder().setSubScribe(object).build().create(intf);
                declaredField.setAccessible(true);
                try {
                    declaredField.set(object, intfProxy);
                } catch (IllegalAccessException e) {
                    Logger.e(e,"%s的%s不能被注入%s",declaredField.getDeclaringClass(),declaredField.getType(),intfProxy);
                }
            }
        }

    }
}
