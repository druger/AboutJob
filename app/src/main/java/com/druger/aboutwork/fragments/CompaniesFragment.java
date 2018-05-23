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
import android.widget.ImageView;
import android.widget.TextView;

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
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.druger.aboutwork.utils.rx.RxSearch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;
import static com.druger.aboutwork.Const.Bundles.DEBOUNCE_SEARCH;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends BaseFragment implements CompaniesView {

    @InjectPresenter
    CompaniesPresenter companiesPresenter;

    private CompanyAdapter adapter;
    private RecyclerView rvCompanies;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;
    private ImageView ivEmptySearch;
    private TextView tvEmptySearch;

    private SearchView searchView;
    private String query;

    @ProvidePresenter
    CompaniesPresenter provideCompaniesPresenter() {
        return App.Companion.getAppComponent().getCompaniesPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_companies_new, container, false);

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
        getActionBar().setTitle(R.string.search);
    }

    private void setupRecycler() {
        adapter = new CompanyAdapter();
        layoutManager = new LinearLayoutManager(getActivity());
        rvCompanies.setLayoutManager(layoutManager);
        rvCompanies.setAdapter(adapter);
    }

    private void setupListeners() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                companiesPresenter.getCompanies(query, currentPage);
            }
        };
        rvCompanies.addOnScrollListener(scrollListener);

        adapter.setOnItemClickListener(new OnItemClickListener<Company>() {
            @Override
            public void onClick(Company company, int position) {
                showCompanyDetail(company.getId());
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });
    }

    private void setupSearch() {
        searchView.setQueryHint(getResources().getString(R.string.query_hint));

        RxSearch.fromSearchView(searchView)
                .debounce(DEBOUNCE_SEARCH, TimeUnit.MILLISECONDS)
                .filter(item -> item.length() >= 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newText -> {
                    query = newText;
                    adapter.clear();
                    scrollListener.resetPageCount();
                });
    }

    private void setupUI() {
        rvCompanies = bindView(R.id.rvCompanies);
        searchView = bindView(R.id.search_view);
        progressBar = bindView(R.id.progressBar);
        ivEmptySearch = bindView(R.id.ivEmptySearch);
        tvEmptySearch = bindView(R.id.tvEmptySearch);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchView.setOnQueryTextListener(null);
        rvCompanies.removeOnScrollListener(scrollListener);
        adapter.setOnItemClickListener(null);
    }

    @Override
    public void showCompanies(List<Company> companies) {
        adapter.addItems(companies);
        ivEmptySearch.setVisibility(View.INVISIBLE);
        tvEmptySearch.setVisibility(View.INVISIBLE);
        rvCompanies.setVisibility(View.VISIBLE);
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
            ivEmptySearch.setVisibility(View.INVISIBLE);
            tvEmptySearch.setVisibility(View.INVISIBLE);
            rvCompanies.setVisibility(View.INVISIBLE);
        } else {
            rvCompanies.setVisibility(View.VISIBLE);
        }
    }
}