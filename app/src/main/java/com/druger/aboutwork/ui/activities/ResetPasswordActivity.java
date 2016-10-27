package com.druger.aboutwork.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.leakcanary.RefWatcher;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.input_email);
        Button resetPass = (Button) findViewById(R.id.btn_reset_pass);
        Button back = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), R.string.error_email, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, R.string.sent_reset_pass, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, R.string.failed_reset_pass, Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
