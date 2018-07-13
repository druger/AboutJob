package com.druger.aboutwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.druger.aboutwork.interfaces.view.LoginView;
import com.druger.aboutwork.presenters.LoginPresenter;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.squareup.leakcanary.RefWatcher;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {
    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectPresenter
    LoginPresenter loginPresenter;

    private EditText etEmailPhone;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvSignup;
    private EditText etForgot;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter.setAuth(this);
        setContentView(R.layout.activity_login);
        setupUI();
        setupUX();
//        setTextWatcher();
    }

    private void setTextWatcher() {
        etEmailPhone.addTextChangedListener(new TextWatcher() {
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
        String email = etEmailPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validate(email, password)) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    private void setupUX() {
        btnLogin.setOnClickListener(view -> {
            String emailPhone = etEmailPhone.getText().toString().trim();

            if (Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches()) {
                emailAuth(emailPhone);
            } else if (Patterns.PHONE.matcher(emailPhone).matches()) {
                phoneAuth(emailPhone);
            }
        });

        tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
        });

        etForgot.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));
    }

    private void phoneAuth(String phone) {
        initVerificationCallback();
        FirebaseAuth.getInstance().setLanguageCode("ru");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallback
        );
    }

    private void initVerificationCallback() {
        verificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                String smsCode = phoneAuthCredential.getSmsCode();
                Toast.makeText(LoginActivity.this, smsCode, Toast.LENGTH_SHORT).show();

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                LoginActivity.this.verificationId = verificationId;
                resendtoken = forceResendingToken;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        onLoginSuccess();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void emailAuth(String email) {
        String password = etPassword.getText().toString().trim();

        if (!validate(email, password)) {
            onLoginFailed();
        } else {
            loginPresenter.loginClick(email, password);
        }
    }

    private void setupUI() {
        etEmailPhone = findViewById(R.id.etEmail);
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
        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public void onLoginSuccess() {
//        btnLogin.setEnabled(true);
        showMainActivity();
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
//        btnLogin.setEnabled(true);
    }

    @Override
    public void showProgress() {
//        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private boolean validate(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || !Patterns.PHONE.matcher(email).matches()) {
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
