package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.CompanyDetailPresenter;
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyDetailFragment extends BaseFragment implements View.OnClickListener,
        CompanyDetailView {
    public static final int REVIEW_REQUEST = 0;

    @InjectPresenter
    CompanyDetailPresenter companyDetailPresenter;

    private FloatingActionButton fabAddReview;
    private CoordinatorLayout ltContent;

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView rvReviews;
    private List<Review> reviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private CompanyDetail companyDetail;

    public static CompanyDetailFragment getInstance(String companyID) {
        CompanyDetailFragment companyDetail = new CompanyDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMPANY_ID, companyID);
        companyDetail.setArguments(bundle);
        return companyDetail;
    }

    @ProvidePresenter
    CompanyDetailPresenter provideCompanyDetailPresenter() {
        return App.Companion.getAppComponent().getCompanyDetailPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_company_detail, container, false);

        setupToolbar();
        setupUI();
        setupUX();
        setupRecycler(reviews);
        setupFabBehavior();

        companyDetailPresenter.getCompanyDetail(getArguments().getString(COMPANY_ID, ""));
        return rootView;
    }

    private void setupFabBehavior() {
        rvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAddReview.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fabAddReview.isShown()) {
                    fabAddReview.hide();
                }
            }
        });
    }

    private void setupUX() {
        fabAddReview.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
    }

    private void setupUI() {
        fabAddReview = bindView(R.id.fabAddReview);
        ltContent = bindView(R.id.ltContent);
        ltError = bindView(R.id.ltError);
        progressBar = bindView(R.id.progressBar);
        btnRetry = bindView(R.id.btnRetry);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecycler(final List<Review> reviews) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvReviews = bindView(R.id.rvReviews);
        reviewAdapter = new ReviewAdapter(reviews);
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setItemAnimator(new DefaultItemAnimator());
        rvReviews.setAdapter(reviewAdapter);

        reviewAdapter.setOnClickListener(new OnItemClickListener<Review>() {
            @Override
            public void onClick(Review review, int position) {
                SelectedReviewFragment reviewFragment = SelectedReviewFragment.newInstance(review, false);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.company_container, reviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        reviewAdapter.setUrlClickListener((String site) -> showWebview(site));

        rvReviews.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                companyDetailPresenter.getReviews(companyDetail.getId(), ++page);
            }
        });
    }

    private void showWebview(String site) {
        new FinestWebView.Builder(getActivity())
                .setCustomAnimations(R.anim.activity_open_enter,
                        R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                .webViewSupportZoom(true)
                .webViewBuiltInZoomControls(true)
                .theme(R.style.WebViewRedTheme)
                .swipeRefreshColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .show(site);
    }

    private void addReview() {
        AddReviewFragment review = AddReviewFragment.Companion.newInstance(companyDetail);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, review);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddReview:
                addReview();
                break;
            case R.id.btnRetry:
                companyDetailPresenter.getCompanyDetail(getArguments().getString(COMPANY_ID, ""));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        companyDetailPresenter.removeListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reviewAdapter.setOnClickListener(null);
    }

    @Override
    public void updateAdapter() {
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showReviews(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCompanyDetail(CompanyDetail company) {
        companyDetail = company;
        reviewAdapter.setCompanyDetail(company);
        companyDetailPresenter.getReviews(company.getId(), 1);
    }

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show) {
            ltContent.setVisibility(View.INVISIBLE);
        } else {
            ltContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showErrorScreen(boolean show) {
        super.showErrorScreen(show);
        if (show) {
            ltContent.setVisibility(View.INVISIBLE);
        } else {
            ltContent.setVisibility(View.VISIBLE);
        }
    }
}
