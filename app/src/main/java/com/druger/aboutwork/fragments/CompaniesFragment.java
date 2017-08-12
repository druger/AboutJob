package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.CompanyDetailActivity;
import com.druger.aboutwork.adapters.CompanyAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.presenters.CompaniesPresenter;
import com.druger.aboutwork.utils.rx.RxSearch;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends BaseFragment implements CompaniesView {

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

    @ProvidePresenter
    CompaniesPresenter provideCompaniesPresenter() {
        return App.getAppComponent().getCompaniesPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_companies, container, false);

        setupToolbar();
        setupUI();
        setupRecycler();
        setupListeners();
        setupSearch();
        return rootView;
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(R.string.companies);
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
                showCompanyDetail(company.getId());
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
    }

    private void setupSearch() {
        searchView.setQueryHint(getResources().getString(R.string.query_hint));

        RxSearch.fromSearchView(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(item -> item.length() >= 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newText -> {
                    query = newText;
                    mCompanies.clear();
                    adapter.notifyDataSetChanged();
                    scrollListener.resetPageCount();
                });
    }

    private void setupUI() {
        recyclerView = bindView(R.id.recycler_view);
        searchView = bindView(R.id.search_view);
        progressBar = bindView(R.id.progressBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchView.setOnQueryTextListener(null);
        recyclerView.removeOnScrollListener(scrollListener);
        adapter.setOnItemClickListener(null);
    }

    @Override
    public void showCompanies(List<Company> companies) {
        mCompanies.addAll(companies);
        adapter.notifyDataSetChanged();
    }

    public void showCompanyDetail(String id) {
        Intent intent = new Intent(getActivity(), CompanyDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(COMPANY_ID, id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show) {
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}