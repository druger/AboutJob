package com.druger.aboutwork.fragments


import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import com.druger.aboutwork.App
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.interfaces.view.AccountView
import com.druger.aboutwork.presenters.AccountPresenter
import com.druger.aboutwork.utils.PreferencesHelper
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.auth_layout.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import timber.log.Timber
import javax.inject.Inject


class AccountFragment : BaseSupportFragment(), AccountView {

    @InjectPresenter
    lateinit var accountPresenter: AccountPresenter
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @ProvidePresenter
    internal fun getAccountPresenter(): AccountPresenter {
        return App.appComponent.accountPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_account, container, false)
        accountPresenter.getUserInfo()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        showVersion()
    }

    private fun showVersion() {
        tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        mToolbar?.let { setActionBar(it) }
        actionBar?.setTitle(R.string.settings)
    }

    private fun setupListeners() {
        cvLogout.setOnClickListener { showLogoutDialog() }
        cvEmail.setOnClickListener { showChangeEmail() }
        cvName.setOnClickListener { showChangeName() }
        cvPassword.setOnClickListener { showChangePassword() }
        cvRemoveAcc.setOnClickListener { showRemoveDialog() }
        btnLogin.setOnClickListener { showLogin() }
        tvWriteToDev.setOnClickListener { accountPresenter.writeToDevelopers(getString(R.string.email_support)) }
    }

    private fun showLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showRemoveDialog() {
        val builder = AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
        builder.setTitle(R.string.remove_account_ask)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            accountPresenter.removeAccount()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showChangePassword() {
        val passwordFragment = ChangePasswordFragment()
        replaceFragment(passwordFragment, R.id.main_container, true)
    }

    private fun showChangeName() {
        val nameFragment = ChangeNameFragment()
        replaceFragment(nameFragment, R.id.main_container, true)
    }

    private fun showChangeEmail() {
        val email = tvEmail.text.toString().trim { it <= ' ' }
        val changeEmail = ChangeEmailFragment.newInstance(email)
        replaceFragment(changeEmail, R.id.main_container, true)
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
        builder.setTitle(R.string.log_out)
        builder.setMessage(R.string.message_log_out)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            context?.let {
                AuthUI.getInstance()
                    .signOut(it)
                    .addOnCompleteListener { task ->
                        Timber.tag("Log out").d("result: %s", task.isSuccessful)
                        accountPresenter.logout()
                    }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    override fun showToast(@StringRes resId: Int) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show()
    }

    override fun showMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
    }

    override fun showName(name: String?) {
        if (name == null || name.isEmpty()) {
            tvName.setText(R.string.add_name)
        } else
            tvName.text = name
    }

    override fun showEmail(email: String?) {
        if (email == null || email.isEmpty()) {
            tvEmail.setText(R.string.add_email)
        } else
            tvEmail.text = email

    }

    override fun showAuthAccess() {
        content.visibility = View.INVISIBLE
        ltAuthAccount.visibility = View.VISIBLE
        tvAuth.setText(R.string.account_login)
    }

    override fun showPhone(phone: String?) {
        if (phone == null || phone.isEmpty()) {
            ltPhone.visibility = View.GONE
            line3.visibility = View.GONE
        } else {
            tvPhone.text = phone
            cvPassword.visibility = View.GONE
        }
    }

    override fun sendEmail(emailIntent: Intent) {
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
            showToast(R.string.no_email_apps)
        }
    }
}
