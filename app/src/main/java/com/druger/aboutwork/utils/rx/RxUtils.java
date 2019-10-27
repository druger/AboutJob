package com.druger.aboutwork.utils.rx;

import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by druger on 20.07.2017.
 */

public class RxUtils {

    private RxUtils() {
    }

    public static <T> SingleTransformer<T, T> singleTransformers() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> observableTransformer() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
}
