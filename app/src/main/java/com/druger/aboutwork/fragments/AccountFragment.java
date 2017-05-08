package com.druger.aboutwork.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.presenters.AccountPresenter;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends MvpAppCompatFragment implements View.OnClickListener, AccountView{

    @InjectPresenter
    AccountPresenter accountPresenter;

    private  TextView tvName;
    private ImageView ivEditName;
    private TextView tvMyReviews;
    private TextView tvSettings;
    private TextView tvLogout;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setupUI(view);
        accountPresenter.setupAuth();
        setupListeners();
        setupToolbar();

        return view;
    }

    private void setupListeners() {
        tvSettings.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivEditName.setOnClickListener(this);
        tvMyReviews.setOnClickListener(this);
    }

    private void setupToolbar() {
        ((MainActivity) getActivity()).setActionBarTitle(R.string.app_name);
        ((MainActivity) getActivity()).resetBackArrowActionBar();
    }

    private void setupUI(View view) {
        tvName = (TextView) view.findViewById(R.id.tvName);
        ivEditName = (ImageView) view.findViewById(R.id.ivEditName);
        tvMyReviews = (TextView) view.findViewById(R.id.tvMyReviews);
        tvSettings = (TextView) view.findViewById(R.id.tvSettings);
        tvLogout = (TextView) view.findViewById(R.id.tvLogout);

        tvName.setText(SharedPreferencesHelper.getUserName(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        accountPresenter.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        accountPresenter.removeAuthListener();
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
                accountPresenter.clickChangeName();
                break;
            case R.id.tvSettings:
                accountPresenter.clickOpenSettings();
                break;
            case R.id.tvLogout:
                accountPresenter.logout();
                break;
            case R.id.tvMyReviews:
                accountPresenter.clickOpenMyReviews();
                break;
        }
    }

    @Override
    public void openMyReviews(String userId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, MyReviewsFragment.newInstance(userId));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void openSettings() {
        SettingsFragment settings = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_container, settings)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void changeName(final String userId) {
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
                    accountPresenter.changeUserName(userName, userId);
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
