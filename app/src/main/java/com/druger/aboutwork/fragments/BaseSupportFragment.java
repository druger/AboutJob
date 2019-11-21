package com.druger.aboutwork.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.druger.aboutwork.R;
import com.druger.aboutwork.enums.TypeMessage;
import com.druger.aboutwork.interfaces.view.NetworkView;

import org.jetbrains.annotations.NotNull;

import moxy.MvpAppCompatFragment;

/**
 * Created by druger on 06.08.2017.
 */

public abstract class BaseSupportFragment extends MvpAppCompatFragment implements NetworkView {
    protected String TAG = getClass().getSimpleName();
    protected View rootView;
    protected ProgressBar mProgressBar;
    protected Toolbar mToolbar;
    protected LinearLayout mLtError;

    @SuppressWarnings("unchecked")
    protected <T extends View> T bindView(@IdRes int id) {
        return (T) rootView.findViewById(id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showMessage(@StringRes int message, TypeMessage typeMessage) {
        switch (typeMessage) {
            case SUCCESS:
                showToast(message);
                break;
            case ERROR:
                showToast(message);
                break;
            default:
                showToast(R.string.network_error);
                break;
        }
    }

    @Override
    public void showMessage(@NotNull String message) {
        showToast(message);
    }

    @Override
    public void showErrorScreen(boolean show) {
        if (show) {
            mLtError.setVisibility(View.VISIBLE);
        } else {
            mLtError.setVisibility(View.GONE);
        }
    }

    private void showToast(@StringRes Integer message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected void setActionBar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    protected void replaceFragment(Fragment fragment, @IdRes int container, boolean addToBackStack) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(container, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void addFragment(Fragment fragment, @IdRes int container, boolean addToBackStack) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(container, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (BuildConfig.DEBUG) {
//            RefWatcher refWatcher = App.Companion.getRefWatcher(getActivity());
//            refWatcher.watch(this);
//        }
//    }
}
