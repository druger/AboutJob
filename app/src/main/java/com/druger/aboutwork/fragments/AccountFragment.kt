package com.druger.aboutwork.fragments


import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.databinding.FragmentAccountBinding
import com.druger.aboutwork.utils.PreferenceHelper.Companion.DARK_MODE_FOLLOW_SYSTEM
import com.druger.aboutwork.utils.PreferenceHelper.Companion.DARK_MODE_KEY
import com.druger.aboutwork.utils.PreferenceHelper.Companion.DARK_MODE_NO
import com.druger.aboutwork.utils.PreferenceHelper.Companion.DARK_MODE_YES
import com.druger.aboutwork.viewmodels.AccountViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class AccountFragment : BaseSupportFragment() {

    private val viewModel: AccountViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private var name: String? = null

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        observeAuthSetting()
        observeEmail()
        observeName()
        observePhone()
        observeSendEmail()
        observeRemoveAccount()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        viewModel.getUserInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkDarkMode()
        setupToolbar()
        setupListeners()
        showVersion()
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeTheme(isChecked)
        }
    }

    private fun checkDarkMode() {
        val darkMode = sharedPref.getInt(DARK_MODE_KEY, DARK_MODE_FOLLOW_SYSTEM)
        when (darkMode) {
            DARK_MODE_FOLLOW_SYSTEM -> checkConfiguration()
            DARK_MODE_YES -> switchDarkMode(true)
            DARK_MODE_NO -> switchDarkMode(false)
        }
    }

    private fun checkConfiguration() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> switchDarkMode(false)
            Configuration.UI_MODE_NIGHT_YES -> switchDarkMode(true)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> switchDarkMode(false)
        }
    }

    private fun switchDarkMode(checked: Boolean) {
        binding.darkModeSwitch.isChecked = checked
    }

    private fun changeTheme(dark: Boolean) {
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switchDarkMode(true)
            saveDarkMode(DARK_MODE_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            switchDarkMode(false)
            saveDarkMode(DARK_MODE_NO)
        }
    }

    private fun saveDarkMode(darkMode: Int) {
        with(sharedPref.edit()) {
            putInt(DARK_MODE_KEY, darkMode)
            apply()
        }
    }

    override fun onDestroyView() {
        _binding = null
        viewModel.removeAuthListener()
        super.onDestroyView()
    }

    private fun showVersion() {
        binding.tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun setupToolbar() {
        actionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar?.toolbar?.let { setActionBar(it) }
        actionBar?.setTitle(R.string.settings)
    }

    private fun setupListeners() {
        binding.apply {
            cvLogout.setOnClickListener { showLogoutDialog() }
            cvName.setOnClickListener { showChangeName() }
            cvRemoveAcc.setOnClickListener { showRemoveDialog() }
            tvWriteToDev.setOnClickListener { viewModel.writeToDevelopers(getString(R.string.email_support)) }
        }
    }

    private fun showRemoveDialog() {
        val builder = AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
        builder.setTitle(R.string.remove_account_ask)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            viewModel.removeAccount()
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

    private fun showToast(@StringRes resId: Int) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show()
    }

    private fun showMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
    }

    private fun observeName() {
        viewModel.name.observe(this) {
            showName(it)
        }
    }

    private fun observeEmail() {
        viewModel.email.observe(this) {
            showEmail(it)
        }
    }

    private fun observePhone() {
        viewModel.phone.observe(this) {
            showPhone(it)
        }
    }

    private fun observeRemoveAccount() {
        viewModel.removeAccountState.observe(this) { state ->
            if (state) {
                showToast(R.string.profile_deleted)
                showMainActivity()
            } else {
                showToast(R.string.failed_delete_user)
            }
        }
    }

    private fun observeSendEmail() {
        viewModel.sendEmailState.observe(this) {
            sendEmail(it)
        }
    }

    private fun showName(name: String?) {
        this.name = name
        if (name.isNullOrEmpty()) {
            binding.tvName.setText(R.string.add_name)
        } else
            binding.tvName.text = name
    }

    private fun showEmail(email: String) {
        binding.apply {
            ltEmail.isVisible = true
            line2.root.isVisible = true
            tvEmail.text = email
        }
    }

    private fun showPhone(phone: String) {
        binding.apply {
            ltPhone.isVisible = true
            line3.root.isVisible = true
            tvPhone.text = phone
        }
    }

    private fun observeAuthSetting() {
        viewModel.authSetting.observe(this) { auth ->
            if (auth) showAuthSetting() else showNotAuthSetting()
        }
    }

    private fun showNotAuthSetting() {
        binding.apply {
            authGroup.isVisible = false
            ltPhone.isVisible = false
            line3.root.isVisible = false
            ltEmail.isVisible = false
            line2.root.isVisible = false
        }
    }

    private fun showAuthSetting() {
        binding.authGroup.isVisible = true
    }

    private fun sendEmail(emailIntent: Intent) {
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
            showToast(R.string.no_email_apps)
        }
    }
}