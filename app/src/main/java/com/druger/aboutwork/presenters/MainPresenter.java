package com.druger.aboutwork.presenters;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.MainView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by druger on 30.04.2017.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView>
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_companies:
                getViewState().showCompanies();
                break;
            case R.id.action_ratings:
                getViewState().showRatings();
                break;
            case R.id.action_account:
                getViewState().showAccount();
                break;
        }
        return true;
    }

    public void checkAuthUser() {
        FirebaseAuth.getInstance();
        new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    getViewState().showLoginActivity();
                }
            }
        };
    }
}
