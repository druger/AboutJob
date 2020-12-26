package com.druger.aboutwork.fragments


import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.interfaces.view.AccountView
import com.druger.aboutwork.presenters.AccountPresenter
import com.firebase.ui.auth.AuthUI
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import timber.log.Timber


class AccountFragment : BaseSupportFragment(), AccountView {

    @InjectPresenter
    lateinit var accountPresenter: AccountPresenter

    private var name: String? = null

    @ProvidePresenter
    internal fun getAccountPresenter() = AccountPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_account, container, false)
        accountPresenter.getUserInfo()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkDarkMode()
        setupToolbar()
        setupListeners()
        showVersion()
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeTheme(isChecked)
        }
    }

    private fun checkDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> darkModeSwitch.isChecked = false
            Configuration.UI_MODE_NIGHT_YES -> darkModeSwitch.isChecked = true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> darkModeSwitch.isChecked = false
        }
    }

    private fun changeTheme(dark: Boolean) {
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkModeSwitch.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeSwitch.isChecked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        accountPresenter.removeAuthListener()
    }

    private fun showVersion() {
        tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun setupToolbar() {
        actionBar?.setDisplayShowTitleEnabled(true)
        setActionBar(toolbar)
        actionBar?.setTitle(R.string.settings)
    }

    private fun setupListeners() {
        cvLogout.setOnClickListener { showLogoutDialog() }
        cvName.setOnClickListener { showChangeName() }
        cvRemoveAcc.setOnClickListener { showRemoveDialog() }
        tvWriteToDev.setOnClickListener { accountPresenter.writeToDevelopers(getString(R.string.email_support)) }
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

    private fun showChangeName() {
        val nameFragment = ChangeNameFragment.newInstance(name)
        replaceFragment(nameFragment, R.id.main_container, true)
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
        this.name = name
        if (name.isNullOrEmpty()) {
            tvName.setText(R.string.add_name)
        } else
            tvName.text = name
    }

    override fun showEmail(email: String) {
        ltEmail.isVisible = true
        line2.isVisible = true
        tvEmail.text = email
    }

    override fun showPhone(phone: String) {
        ltPhone.isVisible = true
        line3.isVisible = true
        tvPhone.text = phone
    }

    override fun showNotAuthSetting() {
        authGroup.isVisible = false
        ltPhone.isVisible = false
        line3.isVisible = false
        ltEmail.isVisible = false
        line2.isVisible = false
    }

    override fun showAuthSetting() {
        authGroup.isVisible = true
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
