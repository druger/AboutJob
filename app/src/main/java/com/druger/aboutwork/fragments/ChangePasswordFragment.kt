package com.druger.aboutwork.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.interfaces.view.ChangePasswordView
import com.druger.aboutwork.presenters.ChangePasswordPresenter
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter

class ChangePasswordFragment : BaseSupportFragment(), ChangePasswordView {

    @InjectPresenter
    lateinit var passwordPresenter: ChangePasswordPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_change_password, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUX()
        setupToolbar()
        mProgressBar = progressBar
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        mToolbar?.let { setActionBar(it) }
        actionBar?.setTitle(R.string.change_password)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupUX() {
        btnChangePass.setOnClickListener {
            passwordPresenter.changePassword(etPassword.text.toString().trim { it <= ' ' })
        }
    }

    override fun showLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            etPassword.visibility = View.INVISIBLE
            btnChangePass.visibility = View.INVISIBLE
        } else {
            etPassword.visibility = View.VISIBLE
            btnChangePass.visibility = View.VISIBLE
        }
    }
}
