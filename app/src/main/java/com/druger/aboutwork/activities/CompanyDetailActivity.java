package com.druger.aboutwork.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.druger.aboutwork.R;
import com.druger.aboutwork.fragments.CompanyDetailFragment;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;

// TODO добавить MVP
public class CompanyDetailActivity extends AppCompatActivity {

    private CompanyDetailFragment companyDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        showCompanyDetailFragment();
    }

    private void showCompanyDetailFragment() {
        String companyID = getIntent().getStringExtra(COMPANY_ID);
        FragmentManager fm = getSupportFragmentManager();
        companyDetail = (CompanyDetailFragment) fm.findFragmentByTag(CompanyDetailFragment.FRAGMENT_TAG);
        if (companyDetail == null) {
            companyDetail = CompanyDetailFragment.getInstance(companyID);
            fm.beginTransaction()   .add(R.id.company_container, companyDetail, CompanyDetailFragment.FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
//        refWatcher.watch(this);
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
