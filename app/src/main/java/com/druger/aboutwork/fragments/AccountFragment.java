package com.druger.aboutwork.fragments;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.presenters.AccountPresenter;
import com.druger.aboutwork.utils.PreferencesHelper;
import com.firebase.ui.auth.AuthUI;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;


public class AccountFragment extends BaseSupportFragment implements AccountView{

    @InjectPresenter
    AccountPresenter accountPresenter;
    @Inject
    PreferencesHelper preferencesHelper;

    private TextView tvName;
    private LinearLayout cvLogout;
    private LinearLayout cvEmail;
    private LinearLayout cvName;
    private LinearLayout cvPassword;
    private LinearLayout cvRemoveAccount;
    private TextView tvEmail;
    private RelativeLayout ltAuthAccount;
    private ConstraintLayout content;
    private Button btnLogin;
    private TextView tvAuth;
    private TextView tvPhone;
    private LinearLayout ltPhone;
    private View line3;
    private TextView tvWriteToDev;

    @ProvidePresenter
    AccountPresenter getAccountPresenter() {
        return App.Companion.getAppComponent().getAccountPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Companion.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        setupUI();
        setupToolbar();
        accountPresenter.getUserInfo();
        setupListeners();

        return rootView;
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setTitle(R.string.settings);
    }

    private void setupListeners() {
        cvLogout.setOnClickListener(v -> showLogoutDialog());
        cvEmail.setOnClickListener(v -> showChangeEmail());
        cvName.setOnClickListener(v -> showChangeName());
        cvPassword.setOnClickListener(v -> showChangePassword());
        cvRemoveAccount.setOnClickListener(v -> showRemoveDialog());
        btnLogin.setOnClickListener(v -> showLogin());
        tvWriteToDev.setOnClickListener(v ->
                accountPresenter.writeToDevelopers(getString(R.string.email_support)));
    }

    private void showLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void setupUI() {
        tvName = bindView(R.id.tvName);
        cvLogout = bindView(R.id.cvLogout);
        cvEmail = bindView(R.id.cvEmail);
        tvEmail = bindView(R.id.tvEmail);
        cvName = bindView(R.id.cvName);
        cvRemoveAccount = bindView(R.id.cvRemoveAcc);
        cvPassword = bindView(R.id.cvPassword);
        ltAuthAccount = bindView(R.id.ltAuthAccount);
        content = bindView(R.id.content);
        btnLogin = bindView(R.id.btnLogin);
        tvAuth = bindView(R.id.tvAuth);
        tvPhone = bindView(R.id.tvPhone);
        ltPhone = bindView(R.id.ltPhone);
        line3 = bindView(R.id.line3);
        tvWriteToDev = bindView(R.id.tvWriteToDev);
    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        builder.setTitle(R.string.remove_account_ask);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            accountPresenter.removeAccount();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showChangePassword() {
        ChangePasswordFragment passwordFragment = new ChangePasswordFragment();
        replaceFragment(passwordFragment, R.id.main_container, true);
    }

    private void showChangeName() {
        ChangeNameFragment nameFragment = new ChangeNameFragment();
        replaceFragment(nameFragment, R.id.main_container, true);
    }

    private void showChangeEmail() {
        String email = tvEmail.getText().toString().trim();
        ChangeEmailFragment changeEmail = ChangeEmailFragment.newInstance(email);
        replaceFragment(changeEmail, R.id.main_container, true);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.message_log_out);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            AuthUI.getInstance()
                    .signOut(getActivity())
                    .addOnCompleteListener(task -> {
                        Timber.tag("Log out").d("result: %s", task.isSuccessful());
                        accountPresenter.logout();
                    });
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @Override
    public void showName(String name) {
        if (name == null || name.isEmpty()) {
            tvName.setText(R.string.add_name);
        } else tvName.setText(name);
    }

    @Override
    public void showEmail(String email) {
        if (email== null || email.isEmpty()) {
            tvEmail.setText(R.string.add_email);
        } else tvEmail.setText(email);

    }

    @Override
    public void showAuthAccess() {
        content.setVisibility(View.INVISIBLE);
        ltAuthAccount.setVisibility(View.VISIBLE);
        tvAuth.setText(R.string.account_login);
    }

    @Override
    public void showPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            ltPhone.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        } else tvPhone.setText(phone);
    }

    @Override
    public void sendEmail(@NotNull Intent emailIntent) {
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
            showToast(R.string.no_email_apps);
        }
    }
}
