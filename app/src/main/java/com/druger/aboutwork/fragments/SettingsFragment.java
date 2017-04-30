package com.druger.aboutwork.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.activities.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.RefWatcher;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = SettingsFragment.class.getSimpleName();

    private EditText editText;
    private Button changeEmail;
    private Button changePass;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        };

        ((MainActivity) getActivity()).setActionBarTitle(R.string.settings);
        ((MainActivity) getActivity()).setBackArrowActionBar();

        editText = (EditText) view.findViewById(R.id.editText);
        changeEmail = (Button) view.findViewById(R.id.change_email);
        changePass = (Button) view.findViewById(R.id.change_pass);
        Button btnChangeEmail = (Button) view.findViewById(R.id.btn_change_email);
        Button btnChangePass = (Button) view.findViewById(R.id.btn_change_pass);
        Button removeAccount = (Button) view.findViewById(R.id.btnRemoveAccount);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        editText.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePass.setVisibility(View.GONE);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        changeEmail.setOnClickListener(this);
        changePass.setOnClickListener(this);
        btnChangeEmail.setOnClickListener(this);
        btnChangePass.setOnClickListener(this);
        removeAccount.setOnClickListener(this);
        return view;
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

                if (user != null && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
                        && !TextUtils.isEmpty(newEmail)) {
                    changeEmail(newEmail);
                } else if (TextUtils.isEmpty(newEmail)) {
                    editText.setError(getString(R.string.valid_email));
                    progressBar.setVisibility(View.GONE);
                }
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

                if (user != null && !TextUtils.isEmpty(newPass)) {
                    changePassword(newPass);
                } else if (TextUtils.isEmpty(newPass)) {
                    editText.setError(getString(R.string.enter_pass));
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case R.id.btnRemoveAccount:
                progressBar.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.remove_account_ask);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
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

    private void deleteAccount() {
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                                Toast.makeText(getActivity(), R.string.profile_deleted, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), SignupActivity.class));
                            } else {
                                Toast.makeText(getActivity(), R.string.failed_delete_user, Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void changePassword(String newPass) {
        if (newPass.length() < 6) {
            editText.setError(getString(R.string.pass_error));
            progressBar.setVisibility(View.GONE);
        } else {
            user.updatePassword(newPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                                Toast.makeText(getActivity(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                logout();
                            } else {
                                Toast.makeText(getActivity(), R.string.failed_update_pass, Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void changeEmail(String newEmail) {
        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                            Toast.makeText(getActivity(), R.string.updated_email, Toast.LENGTH_SHORT).show();
                            logout();
                        } else {
                            Toast.makeText(getActivity(), R.string.failed_update_email, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
