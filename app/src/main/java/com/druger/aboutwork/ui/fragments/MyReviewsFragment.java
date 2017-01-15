package com.druger.aboutwork.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.ui.activities.MainActivity;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyReviewsFragment extends Fragment {


    public MyReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_reviews, container, false);

        ((MainActivity) getActivity()).setActionBarTitle(R.string.my_reviews);
        ((MainActivity) getActivity()).setBackArrowActionBar();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
