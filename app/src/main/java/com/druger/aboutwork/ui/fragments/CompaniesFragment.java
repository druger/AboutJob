package com.druger.aboutwork.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyResponse;
import com.druger.aboutwork.rest.ApiClient;
import com.druger.aboutwork.rest.ApiService;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends Fragment {
    private static final String TAG = CompaniesFragment.class.getSimpleName();

    private FastItemAdapter<Company> adapter;
    private FooterAdapter<ProgressItem> footerAdapter;

    private RecyclerView recyclerView;

    private String query;

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

        adapter = new FastItemAdapter<>();
        footerAdapter = new FooterAdapter<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

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
}