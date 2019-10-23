package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.interfaces.view.ChangeEmailView;
import com.druger.aboutwork.presenters.ChangeEmailPresenter;

import moxy.presenter.InjectPresenter;

import static com.druger.aboutwork.Const.Bundles.EMAIL;

public class ChangeEmailFragment extends BaseSupportFragment implements ChangeEmailView {

    @InjectPresenter
    ChangeEmailPresenter changeEmailPresenter;

    private EditText etEmail;
    private Button btnChangeEmail;
    private String email;

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
        getActionBar().setTitle(R.string.change_email);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showEmail() {
        email = getArguments().getString(EMAIL);
        if (email != null && email.equals(getString(R.string.add_email))) {
           email = "";
        } else etEmail.setText(email);
    }

    private void setupUX() {
        btnChangeEmail.setOnClickListener(v -> changeEmailPresenter.changeEmail(email, getActivity()));
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
            }
        });
    }

    private void setupUI() {
        etEmail = bindView(R.id.etEmail);
        btnChangeEmail = bindView(R.id.btnChangeEmail);
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
            etEmail.setVisibility(View.INVISIBLE);
            btnChangeEmail.setVisibility(View.INVISIBLE);
        } else {
            etEmail.setVisibility(View.VISIBLE);
            btnChangeEmail.setVisibility(View.VISIBLE);
        }
    }
}
