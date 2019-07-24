package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.interfaces.view.ChangePasswordView;
import com.druger.aboutwork.presenters.ChangePasswordPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends BaseSupportFragment implements ChangePasswordView {

    @InjectPresenter
    ChangePasswordPresenter passwordPresenter;

    private EditText etPassword;
    private Button btnChangePass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        setupUI();
        setupUX();
        setupToolbar();
        return rootView;
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setTitle(R.string.change_password);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUX() {
        btnChangePass.setOnClickListener(v -> passwordPresenter
                .changePassword(etPassword.getText().toString().trim(), getActivity()));
    }

    private void setupUI() {
        etPassword = bindView(R.id.etPassword);
        btnChangePass = bindView(R.id.btnChangePass);
        mProgressBar = bindView(R.id.progressBar);
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show) {
            etPassword.setVisibility(View.INVISIBLE);
            btnChangePass.setVisibility(View.INVISIBLE);
        } else {
            etPassword.setVisibility(View.VISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
        }
    }

}
