package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.fragments.CompanyDetailFragment;
import com.squareup.leakcanary.RefWatcher;

public class CompanyDetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        if (findViewById(R.id.company_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            CompanyDetailFragment company = new CompanyDetailFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.company_container, company).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
