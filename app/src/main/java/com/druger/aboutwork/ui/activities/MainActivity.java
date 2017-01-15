package com.druger.aboutwork.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.ui.fragments.AccountFragment;
import com.druger.aboutwork.ui.fragments.CompaniesFragment;
import com.druger.aboutwork.ui.fragments.RatingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.RefWatcher;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    private BottomNavigationView bottomNavigation;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private void initUI() {
        fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment == null) {
            fragment = new CompaniesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment).commit();
        }

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_companies:
                        bottomNavigation.setItemBackgroundResource(R.color.tab1);
                        fragment = new CompaniesFragment();
                        break;
                    case R.id.action_ratings:
                        bottomNavigation.setItemBackgroundResource(R.color.tab2);
                        fragment = new RatingsFragment();
                        break;
                    case R.id.action_account:
                        bottomNavigation.setItemBackgroundResource(R.color.tab3);
                        fragment = new AccountFragment();
                        break;
                }
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, fragment);
                transaction.commit();
                return true;
            }
        });
    }

    public void setActionBarTitle(int title) {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void setBackArrowActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void resetBackArrowActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
}
