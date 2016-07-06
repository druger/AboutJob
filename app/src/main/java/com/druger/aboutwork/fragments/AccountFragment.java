package com.druger.aboutwork.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    public static AccountFragment newInstance(int index) {
        AccountFragment account = new AccountFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        account.setArguments(bundle);
        return  account;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

}
