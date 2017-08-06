package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.ResetPasswordView;
import com.druger.aboutwork.presenters.ResetPasswordPresenter;
import com.squareup.leakcanary.RefWatcher;

public class ResetPasswordActivity extends MvpAppCompatActivity implements ResetPasswordView {

    @InjectPresenter
    ResetPasswordPresenter resetPasswordPresenter;

    private EditText etEmail;
    private ProgressBar progressBar;
    private Button btnResetPass;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordPresenter.setAuth();
        setUI();
        setUX();
    }

    private void setUX() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                resetPasswordPresenter.resetPassClick(email);
            }
        });
    }

    private void setUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnResetPass = (Button) findViewById(R.id.btnResetPass);
        btnBack = (Button) findViewById(R.id.btnBack);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void doResetPass() {
        finish();
    }
}
