package com.druger.aboutwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationViewPager viewPager;

    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        initUI();
    }

    private void initUI() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.companies, R.drawable.ic_company_white_24dp, R.color.tab1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.ratings, R.drawable.ic_star_white_24dp, R.color.tab2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.account, R.drawable.ic_account_white_24dp, R.color.tab3);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setColored(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position, false);
                return true;
            }
        });
    }
}
