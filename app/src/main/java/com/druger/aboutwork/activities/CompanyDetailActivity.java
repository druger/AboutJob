package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.fragments.CompanyDetailFragment;
import com.squareup.leakcanary.RefWatcher;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;

// TODO добавить MVP
public class CompanyDetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        if (findViewById(R.id.company_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
            showCompanyDetailFragment();
        }
    }

    private void showCompanyDetailFragment() {
        String companyID = getIntent().getStringExtra(COMPANY_ID);
        CompanyDetailFragment company = CompanyDetailFragment.getInstance(companyID);
        getFragmentManager().beginTransaction()
                .add(R.id.company_container, company).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
