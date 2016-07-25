package com.druger.aboutwork.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.CatalogAdapter;
import com.druger.aboutwork.model.CatalogCompanies;
import com.druger.aboutwork.rest.ApiClient;
import com.druger.aboutwork.rest.ApiService;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends Fragment {
    private static final String TAG = CompaniesFragment.class.getSimpleName();

    private CatalogAdapter adapter;
    private RecyclerView recyclerView;

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
        View view = inflater.inflate(R.layout.fragment_companies, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        getCatalogCompanies();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    /**
     * Get list catalog companies from hh.ru
     */
    private void getCatalogCompanies() {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ArrayList<CatalogCompanies>> call = apiService.getCatalogCompanies();
        call.enqueue(new Callback<ArrayList<CatalogCompanies>>() {
            @Override
            public void onResponse(Call<ArrayList<CatalogCompanies>> call, Response<ArrayList<CatalogCompanies>> response) {
                List<CatalogCompanies> companies = response.body();
                Collections.sort(companies);
                adapter = new CatalogAdapter(companies);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<CatalogCompanies>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

}
