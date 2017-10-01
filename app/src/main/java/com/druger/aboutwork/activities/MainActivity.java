package com.druger.aboutwork.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.fragments.AccountFragment;
import com.druger.aboutwork.fragments.CompaniesFragment;
import com.druger.aboutwork.fragments.RatingsFragment;
import com.druger.aboutwork.interfaces.view.MainView;
import com.druger.aboutwork.presenters.MainPresenter;
import com.squareup.leakcanary.RefWatcher;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    @InjectPresenter
    MainPresenter mainPresenter;

    private Fragment fragment;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        mainPresenter.checkAuthUser();
        setupUI();
        setupUX();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupUX() {
        bottomNavigation.setOnNavigationItemSelectedListener(mainPresenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private void setupUI() {
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        fragment = getFragmentManager().findFragmentById(R.id.main_container);
        if (fragment == null) {
            fragment = new CompaniesFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment).commit();
        }
    }

    @Override
    public void showCompanies() {
        bottomNavigation.setItemBackgroundResource(R.color.tab1);
        fragment = new CompaniesFragment();
        replaceFragment(fragment);
    }

    @Override
    public void showRatings() {
        bottomNavigation.setItemBackgroundResource(R.color.tab2);
        fragment = new RatingsFragment();
        replaceFragment(fragment);
    }

    @Override
    public void showAccount() {
        bottomNavigation.setItemBackgroundResource(R.color.tab3);
        fragment = new AccountFragment();
        replaceFragment(fragment);
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
}
