package com.druger.aboutwork.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private final String TAG = AccountFragment.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    private  TextView tvName;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvName = (TextView) view.findViewById(R.id.tvName);
        ImageView ivEditName = (ImageView) view.findViewById(R.id.ivEditName);
        TextView tvMyReviews = (TextView) view.findViewById(R.id.tvMyReviews);
        TextView tvSettings = (TextView) view.findViewById(R.id.tvSettings);
        TextView tvLogout = (TextView) view.findViewById(R.id.tvLogout);

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };

        ((MainActivity) getActivity()).setActionBarTitle(R.string.app_name);
        ((MainActivity) getActivity()).resetBackArrowActionBar();

        tvName.setText(SharedPreferencesHelper.getUserName(getActivity()));

        tvSettings.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivEditName.setOnClickListener(this);
        tvMyReviews.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
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
            case R.id.ivEditName:
                changeName();
                break;
            case R.id.tvSettings:
                openSettings();
                break;
            case R.id.tvLogout:
                logout();
                break;
            case R.id.tvMyReviews:
                openMyReviews();
                break;
        }
    }

    private void openMyReviews() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, MyReviewsFragment.newInstance(user.getUid()));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logout() {
        auth.signOut();
    }

    private void openSettings() {
        SettingsFragment settings = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_container, settings)
                .addToBackStack(null)
                .commit();

    }

    private void changeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_name, null);
        final EditText etName = (EditText) view.findViewById(R.id.etUserName);
        etName.setText(tvName.getText());

        builder.setTitle(R.string.name);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = etName.getText().toString();
                if (!userName.trim().isEmpty()) {
                    tvName.setText(userName);
                    Utils.hideKeyboard(getActivity(), etName);
                    SharedPreferencesHelper.saveUserName(userName, getActivity());
                    FirebaseHelper.changeUserName(userName, user.getUid());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.hideKeyboard(getActivity(), etName);
                dialog.cancel();
            }
        });
        builder.show();
        Utils.showKeyboard(getActivity());
    }
}
