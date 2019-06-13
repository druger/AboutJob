package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.fragments.AccountFragment;
import com.druger.aboutwork.fragments.CompaniesFragment;
import com.druger.aboutwork.fragments.MyReviewsFragment;
import com.druger.aboutwork.interfaces.view.MainView;
import com.druger.aboutwork.presenters.MainPresenter;
import com.squareup.leakcanary.RefWatcher;

public class MainActivity extends MvpAppCompatActivity implements MainView,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    MainPresenter mainPresenter;

    private Fragment fragment;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        checkAuthUser();
        setupUI();
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void checkAuthUser() {
        mainPresenter.checkAuthUser();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        initRefWatcher();
        removeAuthListener();
    }

    private void removeAuthListener() {
        mainPresenter.removeAuthListener();
    }

    private void initRefWatcher() {
        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private void setupUI() {
        bottomNavigation = findViewById(R.id.bottom_navigation);

        fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment == null) {
            fragment = new CompaniesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment).commit();
        }
    }

    private void showCompanies() {
        fragment = new CompaniesFragment();
        replaceFragment(fragment);
    }

    @Override
    public void showMyReviews(String userId) {
        fragment = MyReviewsFragment.newInstance(userId);
        replaceFragment(fragment);
    }

    private void showAccount() {
        fragment = new AccountFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.commit();
    }

    public void hideBottomNavigation() {
        bottomNavigation.setVisibility(View.GONE);

    }

    public void showBottomNavigation() {
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_companies:
                showCompanies();
                break;
            case R.id.action_ratings:
                mainPresenter.onClickMyReviews();
                break;
            case R.id.action_setting:
                showAccount();
                break;
            default:
                break;
        }
        return true;
    }
}
