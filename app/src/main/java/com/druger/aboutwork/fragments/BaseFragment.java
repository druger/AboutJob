package com.druger.aboutwork.fragments;

import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.druger.aboutwork.App;
import com.druger.aboutwork.BuildConfig;
import com.druger.aboutwork.R;
import com.druger.aboutwork.enums.TypeMessage;
import com.druger.aboutwork.interfaces.view.NetworkView;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by druger on 06.08.2017.
 */

public abstract class BaseFragment extends MvpFragment implements NetworkView {
    protected String TAG = getClass().getSimpleName();

    protected View rootView;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    protected LinearLayout ltError;
    protected Button btnRetry;

    @SuppressWarnings("unchecked")
    protected <T extends View> T bindView(@IdRes int id) {
        return (T) rootView.findViewById(id);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showMessage(String message, TypeMessage typeMessage) {
        switch (typeMessage) {
            case SUCCESS:
                showToast(message);
                break;
            case ERROR:
                showToast(message);
                break;
            case UNKNOW:
                showToast(getString(R.string.network_error));
                break;
        }
    }

    @Override
    public void showErrorScreen(boolean show) {
        if (show) {
            ltError.setVisibility(View.VISIBLE);
        } else {
            ltError.setVisibility(View.GONE);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected void setActionBar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            RefWatcher refWatcher = App.getRefWatcher(getActivity());
            refWatcher.watch(this);
        }
    }
}
