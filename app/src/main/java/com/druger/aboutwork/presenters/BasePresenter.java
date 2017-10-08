package com.druger.aboutwork.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.rest.RestApi;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by druger on 20.07.2017.
 */

public class BasePresenter<View extends MvpView> extends MvpPresenter<View> {

    protected String TAG = getClass().getSimpleName();

    protected RestApi restApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected final void unSubscribeOnDestroy(@NonNull Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void handleError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}