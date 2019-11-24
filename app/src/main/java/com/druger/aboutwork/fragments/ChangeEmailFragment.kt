package com.druger.aboutwork.fragments


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.druger.aboutwork.Const.Bundles.EMAIL
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.interfaces.view.ChangeEmailView
import com.druger.aboutwork.presenters.ChangeEmailPresenter
import kotlinx.android.synthetic.main.fragment_change_email.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter

class ChangeEmailFragment : BaseSupportFragment(), ChangeEmailView {

    @InjectPresenter
    lateinit var changeEmailPresenter: ChangeEmailPresenter

    private var email: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_change_email, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupUX()
        setupToolbar()
        showEmail()
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        setActionBar(mToolbar)
        actionBar.setTitle(R.string.change_email)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun showEmail() {
        email = arguments?.getString(EMAIL)
        if (email != null && email == getString(R.string.add_email)) {
            email = ""
        } else
            etEmail.setText(email)
    }

    private fun setupUX() {
        btnChangeEmail.setOnClickListener { email?.let { it -> changeEmailPresenter.changeEmail(it) } }
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                email = s.toString()
            }
        })
    }

    private fun setupUI() {
        mProgressBar = progressBar
    }

    override fun showLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            etEmail.visibility = View.INVISIBLE
            btnChangeEmail.visibility = View.INVISIBLE
        } else {
            etEmail.visibility = View.VISIBLE
            btnChangeEmail.visibility = View.VISIBLE
        }
    }

    companion object {

        fun newInstance(email: String): ChangeEmailFragment {

            val args = Bundle()
            args.putString(EMAIL, email)

            val fragment = ChangeEmailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
