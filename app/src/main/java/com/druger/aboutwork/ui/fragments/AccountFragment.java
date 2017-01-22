package com.druger.aboutwork.ui.fragments;


import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.ui.activities.LoginActivity;
import com.druger.aboutwork.ui.activities.MainActivity;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
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

    private  TextView name;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        name = (TextView) view.findViewById(R.id.name);
        ImageView editName = (ImageView) view.findViewById(R.id.edit_name);
        TextView myReviews = (TextView) view.findViewById(R.id.my_reviews);
        TextView settings = (TextView) view.findViewById(R.id.settings);
        TextView logout = (TextView) view.findViewById(R.id.logout);

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

        name.setText(SharedPreferencesHelper.getUserName(getActivity()));

        settings.setOnClickListener(this);
        logout.setOnClickListener(this);
        editName.setOnClickListener(this);
        myReviews.setOnClickListener(this);

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
            case R.id.my_reviews:
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
        final EditText etName = (EditText) view.findViewById(R.id.user_name);
        etName.setText(name.getText());

        builder.setTitle(R.string.name);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = etName.getText().toString();
                if (!userName.trim().isEmpty()) {
                    name.setText(userName);
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                    SharedPreferencesHelper.saveUserName(userName, getActivity());
                    FirebaseHelper.changeUserName(userName, user.getUid());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                dialog.cancel();
            }
        });
        builder.show();
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
