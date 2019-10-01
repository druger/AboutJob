package com.druger.aboutwork.utils.rx;

import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by druger on 20.07.2017.
 */

public class RxUtils {

    private RxUtils() {
    }

    public static <T> SingleTransformer<T, T> httpSchedulers() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
