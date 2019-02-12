package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
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
import com.druger.aboutwork.adapters.CompanyRealmAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.realm.CompanyRealm;
import com.druger.aboutwork.presenters.CompaniesPresenter;
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.druger.aboutwork.utils.rx.RxSearch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.RealmResults;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;
import static com.druger.aboutwork.Const.Bundles.DEBOUNCE_SEARCH;

public class CompaniesFragment extends BaseSupportFragment implements CompaniesView {

    @InjectPresenter
    CompaniesPresenter companiesPresenter;

    private CompanyAdapter adapter;
    private CompanyRealmAdapter realmAdapter;
    private RecyclerView rvCompanies;
    private RecyclerView rvCompaniesRealm;
    private EndlessRecyclerViewScrollListener scrollListener;
    private OnItemClickListener<CompanyRealm> itemClickListener;
    private ImageView ivEmptySearch;
    private TextView tvEmptySearch;
    private TextView tvWatched;

    private SearchView searchView;
    private String query;

    @ProvidePresenter
    CompaniesPresenter provideCompaniesPresenter() {
        return App.Companion.getAppComponent().getCompaniesPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_companies, container, false);

        setupToolbar();
        setupUI();
        setupRecycler();
        setupListeners();
        setupRecyclerRealm();
        setupSearch();
        return rootView;
    }

    private RealmResults<CompanyRealm> getCompaniesFromDb() {
        return companiesPresenter.getCompaniesFromDb();
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(R.string.search);
    }

    private void setupRecycler() {
        adapter = new CompanyAdapter();
        rvCompanies.setHasFixedSize(true);
        rvCompanies.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCompanies.setAdapter(adapter);
    }

    private void setupRecyclerRealm() {
        realmAdapter = new CompanyRealmAdapter(getCompaniesFromDb(), itemClickListener);
        rvCompaniesRealm.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCompaniesRealm.setAdapter(realmAdapter);
    }

    private void setupListeners() {
        scrollListener = new EndlessRecyclerViewScrollListener(
                (LinearLayoutManager) rvCompanies.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
                companiesPresenter.getCompanies(query, currentPage);
            }
        };
        rvCompanies.addOnScrollListener(scrollListener);

        adapter.setOnItemClickListener(new OnItemClickListener<Company>() {
            @Override
            public void onClick(Company company, int position) {
                saveCompanyToDb(setupCompanyRealm(company));
                showCompanyDetail(company.getId());
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        itemClickListener = new OnItemClickListener<CompanyRealm>() {
            @Override
            public void onClick(CompanyRealm company, int position) {
                saveCompanyToDb(company);
                showCompanyDetail(company.getId());
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        };
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
        rvCompaniesRealm = bindView(R.id.rvCompaniesRealm);
        searchView = bindView(R.id.search_view);
        progressBar = bindView(R.id.progressBar);
        ivEmptySearch = bindView(R.id.ivEmptySearch);
        tvEmptySearch = bindView(R.id.tvEmptySearch);
        tvWatched = bindView(R.id.tvWatched);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchView.setOnQueryTextListener(null);
        rvCompanies.removeOnScrollListener(scrollListener);
        adapter.setOnItemClickListener(null);
        companiesPresenter.removeRealmListener();
    }

    @Override
    public void showCompanies(List<Company> companies) {
        adapter.clear();
        adapter.addItems(companies);
        scrollListener.setLoaded();
        rvCompanies.setVisibility(View.VISIBLE);
        rvCompaniesRealm.setVisibility(View.INVISIBLE);
        tvWatched.setVisibility(View.GONE);
        ivEmptySearch.setVisibility(View.INVISIBLE);
        tvEmptySearch.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showWatchedRecently() {
        tvWatched.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCompaniesRealm() {
        rvCompaniesRealm.setVisibility(View.VISIBLE);
        ivEmptySearch.setVisibility(View.INVISIBLE);
        tvEmptySearch.setVisibility(View.INVISIBLE);
    }

    private void saveCompanyToDb(CompanyRealm company) {
        companiesPresenter.saveCompanyToDb(company);
    }

    private CompanyRealm setupCompanyRealm(Company company) {
        String id = company.getId();
        String name = company.getName();
        Company.Logo logo = company.getLogo();
        String sLogo = logo != null ? logo.getLogo90() : "";

        CompanyRealm companyRealm = new CompanyRealm(id, name, sLogo);
        companyRealm.setCity(company.getCity());
        return companyRealm;
    }

    private void showCompanyDetail(String id) {
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
            rvCompaniesRealm.setVisibility(View.INVISIBLE);
            tvWatched.setVisibility(View.GONE);
        } else {
            rvCompanies.setVisibility(View.VISIBLE);
        }
    }
}