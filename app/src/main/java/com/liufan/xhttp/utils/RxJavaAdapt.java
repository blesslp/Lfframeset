package com.liufan.xhttp.utils;

import com.liufan.utils.GsonUtils;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liufan on 16/9/13.
 */
public class RxJavaAdapt {

    public static Object adaptRx(final Call post, Type returnType) {
        ParameterizedType actType = (ParameterizedType) returnType;
        final Type[] actualTypeArguments = actType.getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length == 1) {
            final Type actualTypeArgument = actualTypeArguments[0];
            return Observable.create(new Observable.OnSubscribe<Call>() {
                @Override
                public void call(Subscriber<? super Call> subscriber) {
                    subscriber.onNext(post);
                }
            })
                    .observeOn(Schedulers.io())
                    .map(new Func1<Call, Object>() {

                        @Override
                        public Object call(Call call) {
                            try {
                                final String string = call.execute().body().string();
                                return GsonUtils.fromJson(string, actualTypeArgument);
                            } catch (IOException e) {
                                return null;
                            }
                        }
                    });


        } else {
            return Observable.create(new Observable.OnSubscribe<Call>() {
                @Override
                public void call(Subscriber<? super Call> subscriber) {
                    subscriber.onNext(post);
                }
            })
                    .observeOn(Schedulers.io())
                    .map(new Func1<Call, Object>() {

                        @Override
                        public Object call(Call call) {
                            try {
                                final String string = call.execute().body().string();
                                return string;
                            } catch (IOException e) {
                                return null;
                            }
                        }
                    });
        }

    }
}
