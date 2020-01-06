package com.druger.aboutwork.presenters

import com.druger.aboutwork.rest.RestApi

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import timber.log.Timber

/**
 * Created by druger on 20.07.2017.
 */

open class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    protected lateinit var restApi: RestApi

    private val compositeDisposable = CompositeDisposable()

    protected fun unSubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    protected open fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
