package com.druger.aboutwork.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.CompanyResponse;
import com.druger.aboutwork.rest.ApiClient;
import com.druger.aboutwork.rest.ApiService;
import com.druger.aboutwork.ui.activities.CompanyDetailActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends Fragment {
    private static final String TAG = CompaniesFragment.class.getSimpleName();

    private ApiService apiService;

    private FastItemAdapter<Company> adapter;
    private FooterAdapter<ProgressItem> footerAdapter;

    private String query;

    public CompaniesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_companies, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new FastItemAdapter<>();
        footerAdapter = new FooterAdapter<>();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(footerAdapter.wrap(adapter));

        final EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                getCompanies(query, currentPage);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        adapter.withSelectable(true);
        adapter.withOnClickListener(new FastAdapter.OnClickListener<Company>() {
            @Override
            public boolean onClick(View v, IAdapter<Company> adapter, Company item, int position) {
                Call<CompanyDetail> call = apiService.getCompanyDetail(item.getId());
                call.enqueue(new Callback<CompanyDetail>() {
                    @Override
                    public void onResponse(Call<CompanyDetail> call, Response<CompanyDetail> response) {
                        CompanyDetail detail = response.body();

                        Intent intent = new Intent(getActivity(), CompanyDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("companyDetail", detail);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<CompanyDetail> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
                return true;
            }
        });

        searchView.setQueryHint(getResources().getString(R.string.query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, newText);
                if (!newText.isEmpty()) {
                    query = newText;
                    adapter.clear();
                    scrollListener.resetPageCount();
                }
                return true;
            }
        });

        return view;
    }

    /**
     * Get list companies from search on hh.ru
     */
    private void getCompanies(String query, int page) {

        Call<CompanyResponse> call = apiService.getCompanies(query, page);
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                List<Company> companies = response.body().getItems();
                footerAdapter.clear();
                adapter.add(companies);
                Log.d(TAG, "Companies size = " + companies.size());
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                footerAdapter.clear();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}