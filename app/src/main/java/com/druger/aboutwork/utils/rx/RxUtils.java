package com.druger.aboutwork.utils.rx;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by druger on 20.07.2017.
 */

public class RxUtils {

    public static <T> ObservableTransformer<T, T> httpSchedulers() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
