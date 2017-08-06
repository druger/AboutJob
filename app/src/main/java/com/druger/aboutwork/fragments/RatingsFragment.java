package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.squareup.leakcanary.RefWatcher;


public class RatingsFragment extends Fragment {


    public RatingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(R.string.app_name);
        ((MainActivity) getActivity()).resetBackArrowActionBar();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
