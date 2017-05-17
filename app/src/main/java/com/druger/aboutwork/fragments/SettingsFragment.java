package com.druger.aboutwork.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.activities.SignupActivity;
import com.druger.aboutwork.interfaces.view.SettingsView;
import com.druger.aboutwork.presenters.SettingPresenter;
import com.squareup.leakcanary.RefWatcher;

public class SettingsFragment extends MvpFragment implements View.OnClickListener, SettingsView {
    private final String TAG = SettingsFragment.class.getSimpleName();

    @InjectPresenter
    SettingPresenter settingPresenter;

    private EditText editText;
    private Button changeEmail;
    private Button changePass;
    private ProgressBar progressBar;
    private Button btnChangeEmail;
    private Button btnChangePass;
    private Button removeAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingPresenter.setupAuth();

        setupToolbar();
        setupUI(view);
        setupUX();
        return view;
    }

    private void setupToolbar() {
        ((MainActivity) getActivity()).setActionBarTitle(R.string.settings);
        ((MainActivity) getActivity()).setBackArrowActionBar();
    }

    private void setupUX() {
        changeEmail.setOnClickListener(this);
        changePass.setOnClickListener(this);
        btnChangeEmail.setOnClickListener(this);
        btnChangePass.setOnClickListener(this);
        removeAccount.setOnClickListener(this);
    }

    private void setupUI(View view) {
        editText = (EditText) view.findViewById(R.id.editText);
        changeEmail = (Button) view.findViewById(R.id.change_email);
        changePass = (Button) view.findViewById(R.id.change_pass);
        btnChangeEmail = (Button) view.findViewById(R.id.btn_change_email);
        btnChangePass = (Button) view.findViewById(R.id.btn_change_pass);
        removeAccount = (Button) view.findViewById(R.id.btnRemoveAccount);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        editText.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePass.setVisibility(View.GONE);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_email:
                editText.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.VISIBLE);
                changePass.setVisibility(View.GONE);

                editText.setHint(getString(R.string.new_email));
                editText.setText("");
                editText.setError(null);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case R.id.change_email:
                progressBar.setVisibility(View.VISIBLE);

                String newEmail = editText.getText().toString().trim();
                settingPresenter.checkEmail(newEmail, getActivity());
                break;
            case R.id.btn_change_pass:
                editText.setVisibility(View.VISIBLE);
                changePass.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);

                editText.setHint(getString(R.string.new_password));
                editText.setText("");
                editText.setError(null);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case R.id.change_pass:
                progressBar.setVisibility(View.VISIBLE);

                String newPass = editText.getText().toString().trim();
                settingPresenter.checkPassword(newPass, getActivity());
                break;
            case R.id.btnRemoveAccount:
                progressBar.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.remove_account_ask);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingPresenter.deleteAccount();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        settingPresenter.addAuthStateListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
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
    public void showError(String error) {
        editText.setError(error);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
