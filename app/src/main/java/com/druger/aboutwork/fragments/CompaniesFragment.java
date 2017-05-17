package com.druger.aboutwork.fragments;


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

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.CompanyDetailActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.CompanyAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.presenters.CompaniesPresenter;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends MvpFragment implements CompaniesView {
    private static final String TAG = CompaniesFragment.class.getSimpleName();

    @InjectPresenter
    CompaniesPresenter companiesPresenter;

    private List<Company> mCompanies;
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private EndlessRecyclerOnScrollListener scrollListener;
    private LinearLayoutManager layoutManager;

    private SearchView searchView;
    private String query;

    public CompaniesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_companies, container, false);

        ((MainActivity) getActivity()).setActionBarTitle(R.string.app_name);
        ((MainActivity) getActivity()).resetBackArrowActionBar();

        companiesPresenter.onCreate();

        setupUI(view);
        setupRecycler();
        setupListeners();

        return view;
    }

    private void setupRecycler() {
        mCompanies = new ArrayList<>();
        adapter = new CompanyAdapter(mCompanies);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        scrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                companiesPresenter.getCompanies(query, currentPage);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Company company = mCompanies.get(position);
                companiesPresenter.getCompanyDetail(company);
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
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
                    mCompanies.clear();
                    adapter.notifyDataSetChanged();
                    scrollListener.resetPageCount();
                }
                return true;
            }
        });
    }

    private void setupUI(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchView = (SearchView) getActivity().findViewById(R.id.search_view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchView.setOnQueryTextListener(null);
        recyclerView.removeOnScrollListener(scrollListener);
        adapter.setOnItemClickListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void showCompanies(List<Company> companies) {
        mCompanies.addAll(companies);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showCompanyDetail(CompanyDetail companyDetail) {
        Intent intent = new Intent(getActivity(), CompanyDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("companyDetail", companyDetail);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}