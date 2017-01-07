package com.druger.aboutwork.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.User;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.RefWatcher;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();

    private EditText emailText;
    private EditText passwordText;
    private Button btnSignup;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        TextView loginLink = (TextView) findViewById(R.id.link_login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void signup() {
        Log.d(TAG, "Signup");

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!validate(email, password)) {
            onSignupFailed();
            return;
        }

        btnSignup.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            onSignupFailed();
                        } else {
                            saveNewUser(task.getResult().getUser());
                            onSignupSuccess();
                        }

                    }
                });

    }

    private void saveNewUser(FirebaseUser firebaseUser) {
        String id = firebaseUser.getUid();
        String name = Utils.getNameByEmail(firebaseUser.getEmail());
        User user = new User(id, name);
        FirebaseHelper.addUser(user, firebaseUser.getUid());
        SharedPreferencesHelper.saveUserName(name, this);
    }

    private void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.signup_failed, Toast.LENGTH_SHORT).show();

        btnSignup.setEnabled(true);
    }

    private boolean validate(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.error_email));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordText.setError(getString(R.string.pass_error));
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
