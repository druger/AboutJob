package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.interfaces.view.ChangeEmailView;
import com.druger.aboutwork.presenters.ChangeEmailPresenter;

import static com.druger.aboutwork.Const.Bundles.EMAIL;

public class ChangeEmailFragment extends BaseSupportFragment implements ChangeEmailView {

    @InjectPresenter
    ChangeEmailPresenter changeEmailPresenter;

    private EditText etEmail;
    private Button btnChangeEmail;
    private String email;

    public ChangeEmailFragment() {
        // Required empty public constructor
    }

    public static ChangeEmailFragment newInstance(String email) {

        Bundle args = new Bundle();
        args.putString(EMAIL, email);

        ChangeEmailFragment fragment = new ChangeEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_change_email, container, false);
        setupUI();
        setupUX();
        setupToolbar();
        showEmail();
        return rootView;
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setTitle(R.string.change_name);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showEmail() {
        email = getArguments().getString(EMAIL);
        etEmail.setText(email);
    }

    private void setupUX() {
        btnChangeEmail.setOnClickListener(v -> changeEmailPresenter.changeEmail(email, getActivity()));
    }

    private void setupUI() {
        etEmail = bindView(R.id.etEmail);
        btnChangeEmail = bindView(R.id.btnChangeEmail);
        progressBar = bindView(R.id.progressBar);
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show) {
            etEmail.setVisibility(View.INVISIBLE);
            btnChangeEmail.setVisibility(View.INVISIBLE);
        } else {
            etEmail.setVisibility(View.VISIBLE);
            btnChangeEmail.setVisibility(View.VISIBLE);
        }
    }
}
