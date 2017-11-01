package com.druger.aboutwork.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.SignupActivity;
import com.druger.aboutwork.interfaces.view.SettingsView;
import com.druger.aboutwork.presenters.SettingPresenter;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, SettingsView {

    @InjectPresenter
    SettingPresenter settingPresenter;

    private TextView tvEmail;
    private LinearLayout ltEmail;
    private TextView tvChangePass;
    private TextView tvRemoveAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        settingPresenter.setupAuth();
        setupUI();
        setupToolbar();
        setupUX();
        return rootView;
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(R.string.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUX() {
        ltEmail.setOnClickListener(this);
        tvChangePass.setOnClickListener(this);
        tvRemoveAccount.setOnClickListener(this);
    }

    private void setupUI() {
        tvEmail = bindView(R.id.tvEmail);
        ltEmail = bindView(R.id.ltEmail);
        tvChangePass = bindView(R.id.tvChangePass);
        tvRemoveAccount = bindView(R.id.tvRemoveAccount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRemoveAccount:
                showRemoveDialog();
                break;
            case R.id.ltEmail:
                showChangeEmail();
                break;
            case R.id.tvChangePass:
                showChangePassword();
                break;
            default:
                break;
        }
    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_account_ask);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            settingPresenter.deleteAccount();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));

    }

    private void showChangePassword() {
        ChangePasswordFragment passwordFragment = new ChangePasswordFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, passwordFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showChangeEmail() {
        String email = tvEmail.getText().toString().trim();
        ChangeEmailFragment changeEmail = ChangeEmailFragment.newInstance(email);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, changeEmail)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        settingPresenter.addAuthStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        settingPresenter.removeAuthStateListener();
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void showSignupActivity() {
        startActivity(new Intent(getActivity(), SignupActivity.class));
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmail(String email) {
        tvEmail.setText(email);
    }
}
