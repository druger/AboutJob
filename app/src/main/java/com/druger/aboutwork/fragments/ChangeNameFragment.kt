package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.druger.aboutwork.presenters.ChangeNamePresenter

class ChangeNameFragment : BaseSupportFragment(), ChangeNameView {

    @InjectPresenter
    lateinit var changeNamePresenter: ChangeNamePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_change_name, container, false)
    }
}
