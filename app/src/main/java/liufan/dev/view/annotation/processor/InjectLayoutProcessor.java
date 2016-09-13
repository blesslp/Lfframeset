package liufan.dev.view.annotation.processor;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import liufan.dev.view.annotation.InjectLayout;

/**
 * Created by liufan on 16/5/9.
 */
public final class InjectLayoutProcessor {

    public static @LayoutRes int process(Object act) {
        final Class<?> aClass = act.getClass();
        if(aClass.isAnnotationPresent(InjectLayout.class)) {
            final InjectLayout annotation = aClass.getAnnotation(InjectLayout.class);
            return annotation.value();
        }else{
            return -1;
        }
    }
}
