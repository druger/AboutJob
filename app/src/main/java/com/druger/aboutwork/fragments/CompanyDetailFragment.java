package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.CompanyDetailPresenter;

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

    private TextView tvDescription;
    private ImageView ivDownDrop;
    private ImageView ivUpDrop;
    private TextView tvRating;
    private TextView tvCountReviews;
    private TextView site;
    private RatingBar ratingCompany;
    private ImageView ivToolbar;
    private FloatingActionButton fabAddReview;
    private CoordinatorLayout ltContent;
    private RelativeLayout ltInfo;

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
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
        return App.getAppComponent().getCompanyDetailPresenter();
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
        setupAppBarChanges();

        companyDetailPresenter.getCompanyDetail(getArguments().getString(COMPANY_ID, ""));
        return rootView;
    }

    private void setupAppBarChanges() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                ltInfo.setVisibility(View.GONE);
            } else if (verticalOffset == 0) {
                ltInfo.setVisibility(View.VISIBLE);
            }
        });
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
        ivDownDrop.setOnClickListener(this);
        ivUpDrop.setOnClickListener(this);
        fabAddReview.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
    }

    private void setupUI() {
        site = bindView(R.id.tvSite);
        tvDescription = bindView(R.id.tvContentDescription);
        ivDownDrop = bindView(R.id.ivDownDrop);
        ivUpDrop = bindView(R.id.ivUpDrop);
        ivToolbar = bindView(R.id.ivToolbar);
        tvCountReviews = bindView(R.id.tvCountReviews);
        tvRating = bindView(R.id.tvRating);
        ratingCompany = bindView(R.id.rating_company);
        fabAddReview = bindView(R.id.fabAddReview);
        ltContent = bindView(R.id.ltContent);
        ltError = bindView(R.id.ltError);
        progressBar = bindView(R.id.progressBar);
        btnRetry = bindView(R.id.btnRetry);
        ltInfo = bindView(R.id.ltInfo);
        appBarLayout = bindView(R.id.appBarLayout);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = bindView(R.id.collapsingToolbar);
    }

    private void setupRecycler(final List<Review> reviews) {
        rvReviews = bindView(R.id.rvReviews);
        reviewAdapter = new ReviewAdapter(getActivity(), reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    }

    private void addReview() {
        ReviewFragment review = ReviewFragment.newInstance(companyDetail);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, review);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivDownDrop:
                companyDetailPresenter.downDropClick();
                break;
            case R.id.ivUpDrop:
                companyDetailPresenter.upDropClick();
                break;
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
    public void showDescription() {
        ivDownDrop.setVisibility(View.INVISIBLE);
        ivUpDrop.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDescription() {
        ivUpDrop.setVisibility(View.INVISIBLE);
        ivDownDrop.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.GONE);
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
    public void showRating(float rating) {
        tvRating.setText(String.valueOf(rating));
        ratingCompany.setRating(rating);
    }

    @Override
    public void showCountReviews(int count) {
        tvCountReviews.setText(String.valueOf(count));
    }

    @Override
    public void showCompanyDetail(CompanyDetail company) {
        companyDetail = company;
        companyDetailPresenter.setReviews(company.getId());

        setToolbarName(company.getName());
        setDescription(company);
        loadImage(company);
    }

    private void setToolbarName(String name) {
        collapsingToolbar.setTitle(name);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
    }

    private void setDescription(CompanyDetail company) {
        tvDescription.setVisibility(View.GONE);

        String iSite = company.getSite();
        String iDescription = company.getDescription();
        if (iSite != null) {
            site.setText(iSite);
        }
        if (iDescription != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(iDescription, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvDescription.setText(Html.fromHtml(iDescription));
            }
        }
    }

    private void loadImage(CompanyDetail company) {
        CompanyDetail.Logo logo = company.getLogo();
        Glide.with(this)
                .load(logo != null ? logo.getOriginal() : "")
                .placeholder(R.drawable.default_company)
                .error(R.drawable.default_company)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivToolbar);
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
