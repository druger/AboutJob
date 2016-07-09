package com.druger.aboutwork.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends Fragment {


    public static CompaniesFragment newInstance(int index) {
        CompaniesFragment companies = new CompaniesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        companies.setArguments(bundle);
        return companies;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_companies, container, false);
    }

}
