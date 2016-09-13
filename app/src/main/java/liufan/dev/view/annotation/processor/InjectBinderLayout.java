package liufan.dev.view.annotation.processor;

import android.support.annotation.LayoutRes;

import liufan.dev.view.annotation.InjectBindLayout;
import liufan.dev.view.annotation.InjectLayout;

/**
 * Created by liufan on 16/5/9.
 */
public class InjectBinderLayout {

    public static @LayoutRes int process(Object object) {
        final Class<?> aClass = object.getClass();
        if(aClass.isAnnotationPresent(InjectBindLayout.class)) {
            final InjectBindLayout annotation = aClass.getAnnotation(InjectBindLayout.class);
            return annotation.value();
        }else{
            return -1;
        }
    }
}
