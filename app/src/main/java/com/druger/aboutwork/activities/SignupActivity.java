package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.SignupView;
import com.druger.aboutwork.presenters.SignupPresenter;
import com.squareup.leakcanary.RefWatcher;

public class SignupActivity extends MvpAppCompatActivity implements SignupView {
    private static final String TAG = SignupActivity.class.getSimpleName();

    @InjectPresenter
    SignupPresenter signupPresenter;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignup;
    private ProgressBar progressBar;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupPresenter.setAuth(this);
        setUI();
        setUX();
    }

    private void setUX() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Signup");

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!validate(email, password)) {
                    onSignupFailed();
                } else {
                    signupPresenter.signupClick(email, password);
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.signup_failed, Toast.LENGTH_SHORT).show();

        btnSignup.setEnabled(true);
    }

    @Override
    public void showProgress() {
        btnSignup.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_email));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError(getString(R.string.pass_error));
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
