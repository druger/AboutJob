package com.druger.aboutwork.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.SettingsView;
import com.druger.aboutwork.presenters.SettingPresenter;

public class SettingsFragment extends BaseSupportFragment implements View.OnClickListener, SettingsView {

    @InjectPresenter
    SettingPresenter settingPresenter;

    private TextView tvEmail;
    private TextView tvName;
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
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setTitle(R.string.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUX() {
        tvEmail.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvChangePass.setOnClickListener(this);
        tvRemoveAccount.setOnClickListener(this);
    }

    private void setupUI() {
        tvEmail = bindView(R.id.tvEmail);
        tvName = bindView(R.id.tvName);
        tvChangePass = bindView(R.id.tvChangePass);
        tvRemoveAccount = bindView(R.id.tvRemoveAccount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRemoveAccount:
                showRemoveDialog();
                break;
            case R.id.tvEmail:
                showChangeEmail();
                break;
            case R.id.tvChangePass:
                showChangePassword();
                break;
            case R.id.tvName:
                showChangeName();
                break;
            default:
                break;
        }
    }

    private void showChangeName() {
        ChangeNameFragment nameFragment = new ChangeNameFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, nameFragment)
                .addToBackStack(null)
                .commit();

    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        builder.setTitle(R.string.remove_account_ask);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            settingPresenter.deleteAccount();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
    public void showLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void showMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmail(String email) {
        tvEmail.setText(email);
    }

    @Override
    public void showName(String name) {
        tvName.setText(name);
    }
}
