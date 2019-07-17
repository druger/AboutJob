package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Logo;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.CompanyDetailPresenter;
import com.druger.aboutwork.utils.Utils;
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

import static com.druger.aboutwork.Const.Bundles.COMPANY_ID;

public class CompanyDetailFragment extends BaseSupportFragment implements View.OnClickListener,
        CompanyDetailView {
    public static final int REVIEW_REQUEST = 0;
    public static final String FRAGMENT_TAG = "companyDetail";

    @InjectPresenter
    CompanyDetailPresenter presenter;

    private FloatingActionButton fabAddReview;
    private CoordinatorLayout ltContent;
    TextView tvCompanyName;
    TextView tvRating;
    TextView tvSite;
    TextView tvCity;
    RatingBar ratingCompany;
    ImageView ivRatingSalary;
    ImageView ivRatingChief;
    ImageView ivRatingWorkPlace;
    ImageView ivRatingCareer;
    ImageView ivRatingCollective;
    ImageView ivRatingSocialPackage;
    ImageView ivLogo;
    ImageView ivInfo;
    private NestedScrollView scrollView;
    private LinearLayout ltNoReviews;
    private ProgressBar progressReview;
    private RelativeLayout ltAuthCompany;
    private Button btnLogin;
    private TextView tvAuth;

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView rvReviews;
    private List<Review> reviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private CompanyDetail companyDetail;

    public static CompanyDetailFragment newInstance(String companyID) {
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

        presenter.getCompanyDetail(getArguments().getString(COMPANY_ID, ""));
        setupToolbar();
        setupUI();
        setupUX();
        setupRecycler(reviews);
        setupFabBehavior();
        return rootView;
    }

    private void setupFabBehavior() {
        scrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) fabAddReview.hide();
            else fabAddReview.show();

        });
    }

        private void setupUX() {
            fabAddReview.setOnClickListener(this);
            btnRetry.setOnClickListener(this);
            btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        }

    private void setupUI() {
        fabAddReview = bindView(R.id.fabAddReview);
        ltContent = bindView(R.id.ltContent);
        ltError = bindView(R.id.ltError);
        mProgressBar = bindView(R.id.progressBar);
        btnRetry = bindView(R.id.btnRetry);
        tvCompanyName = bindView(R.id.tvCompanyName);
        tvSite = bindView(R.id.tvSite);
        tvRating = bindView(R.id.tvRating);
        ratingCompany = bindView(R.id.ratingBarCompany);
        ivRatingSalary = bindView(R.id.ivRatingSalary);
        ivRatingChief = bindView(R.id.ivRatingChief);
        ivRatingWorkPlace = bindView(R.id.ivRatingWorkPlace);
        ivRatingCareer = bindView(R.id.ivRatingCareer);
        ivRatingCollective = bindView(R.id.ivRatingCollective);
        ivRatingSocialPackage = bindView(R.id.ivRatingSocialPackage);
        ivLogo = bindView(R.id.ivLogo);
        tvCity = bindView(R.id.tvCity);
        ivInfo = bindView(R.id.ivInfo);
        scrollView = bindView(R.id.scrollView);
        ltNoReviews = bindView(R.id.ltNoReviews);
        progressReview = bindView(R.id.progressReview);
        ltAuthCompany = bindView(R.id.ltAuthCompany);
        btnLogin = bindView(R.id.btnLogin);
        tvAuth = bindView(R.id.tvAuth);
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
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
                replaceFragment(reviewFragment, R.id.company_container, true);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        rvReviews.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                presenter.getReviews(companyDetail.getId(), ++page);
            }
        });
    }

    private void showDescription(String description) {
        CompanyDescriptionFragment fragment = CompanyDescriptionFragment.Companion.newInstance(description);
        replaceFragment(fragment, R.id.company_container, true);
    }

    private void showWebView(String site) {
        new FinestWebView.Builder(getActivity())
                .setCustomAnimations(R.anim.activity_open_enter,
                        R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                .webViewSupportZoom(true)
                .webViewBuiltInZoomControls(true)
                .theme(R.style.WebViewRedTheme)
                .swipeRefreshColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .show(site);
    }

    @Override
    public void addReview() {
        AddReviewFragment review = AddReviewFragment.Companion.newInstance(companyDetail);
        replaceFragment(review, R.id.company_container, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddReview:
                presenter.checkAuthUser();
                break;
            case R.id.btnRetry:
                presenter.getCompanyDetail(getArguments().getString(COMPANY_ID, ""));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.removeListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reviewAdapter.setOnClickListener(null);
        presenter.removeAuthListener();
    }

    @Override
    public void updateAdapter() {
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showReviews(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        if (reviews.isEmpty()) {
            rvReviews.setVisibility(View.GONE);
            ltNoReviews.setVisibility(View.VISIBLE);
        }  else {
            rvReviews.setVisibility(View.VISIBLE);
            ltNoReviews.setVisibility(View.GONE);
        }
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCompanyDetail(CompanyDetail company) {
        companyDetail = company;
        presenter.getReviews(company.getId(), 1);
        setSite();
        tvCity.setText(companyDetail.getArea().getName());
        setSalaryRating(5);
        setChiefRating(3);
        setWorkplaceRating(2);
        setCarrierRating(1);
        setCollectiveRating(4);
        setSocialPackageRating(5);
        setCompanyName(companyDetail.getName());
        loadImage(companyDetail);
        ivInfo.setOnClickListener(v -> showDescription(companyDetail.getDescription()));
    }

    private void setSite() {
        String site = companyDetail.getSite();
        if (site.equals("http://") || site.equals("https://")) {
            tvSite.setVisibility(View.GONE);
        } else {
            tvSite.setOnClickListener(v -> showWebView(companyDetail.getSite()));
            tvSite.setText(companyDetail.getSite());
        }
    }

    void loadImage(CompanyDetail company) {
        Logo logo = company.getLogo();
        Glide.with(getContext())
                .load(logo != null ? logo.getOriginal() : "")
                .placeholder(R.drawable.ic_default_company)
                .error(R.drawable.ic_default_company)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivLogo);
    }

    void setCompanyName(String name) {
        tvCompanyName.setText(name);
    }

    void setSalaryRating(int percent) {
        ivRatingSalary.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
    }

    void setChiefRating(int percent) {
        ivRatingChief.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
    }

    void setWorkplaceRating(int percent) {
        ivRatingWorkPlace.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
    }

    void setCarrierRating(int percent) {
        ivRatingCareer.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
    }

    void setCollectiveRating(int percent) {
        ivRatingCollective.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
    }

    void setSocialPackageRating(int percent) {
        ivRatingSocialPackage.setImageBitmap(Utils.INSTANCE.crateArcBitmap(getContext(), percent));
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

    @Override
    public void showProgressReview() {
        progressReview.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressReview() {
        progressReview.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showAuth() {
        scrollView.setVisibility(View.GONE);
        fabAddReview.hide();
        ltAuthCompany.setVisibility(View.VISIBLE);
        tvAuth.setText(R.string.company_login);
    }
}
