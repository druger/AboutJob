package com.druger.aboutwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.druger.aboutwork.interfaces.view.LoginView;
import com.druger.aboutwork.presenters.LoginPresenter;
import com.squareup.leakcanary.RefWatcher;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {
    private static final int REQUEST_SIGNUP = 0;

    @InjectPresenter
    LoginPresenter loginPresenter;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvSignup;
    private EditText etForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter.setAuth(this);
        setContentView(R.layout.activity_login_new);
        setupUI();
        setupUX();
        setTextWatcher();
    }

    private void setTextWatcher() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCorrectFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCorrectFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkCorrectFields() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validate(email, password)) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    private void setupUX() {
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validate(email, password)) {
                onLoginFailed();
            } else {
                loginPresenter.loginClick(email, password);
            }
        });

        tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
        });

        etForgot.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));
    }

    private void setupUI() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        etForgot = findViewById(R.id.etForgot);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK) {
            showMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        showMainActivity();
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        btnLogin.setEnabled(true);
    }

    @Override
    public void showProgress() {
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private boolean validate(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void showMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
