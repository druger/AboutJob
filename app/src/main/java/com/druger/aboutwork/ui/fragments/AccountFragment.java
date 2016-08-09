package com.druger.aboutwork.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.ui.activities.LoginActivity;
import com.druger.aboutwork.ui.activities.SettingsActivity;
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


    public static AccountFragment newInstance(int index) {
        AccountFragment account = new AccountFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        account.setArguments(bundle);
        return  account;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };

        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView editName = (ImageView) view.findViewById(R.id.edit_name);
        TextView settings = (TextView) view.findViewById(R.id.settings);
        TextView logout = (TextView) view.findViewById(R.id.logout);

        settings.setOnClickListener(this);
        logout.setOnClickListener(this);


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
            case R.id.edit_name:
                changeName();
                break;
            case R.id.settings:
                openSettings();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        auth.signOut();
    }

    private void openSettings() {
        startActivity(new Intent(getActivity(), SettingsActivity.class));

    }

    private void changeName() {

    }
}
