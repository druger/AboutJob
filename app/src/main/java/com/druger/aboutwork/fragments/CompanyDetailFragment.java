package com.druger.aboutwork.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Logo;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.CompanyDetailPresenter;
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

public class CompanyDetailFragment extends BaseSupportFragment implements View.OnClickListener,
        CompanyDetailView {
    public static final int REVIEW_REQUEST = 0;
    public static final String FRAGMENT_TAG = "companyDetail";
    private static final String COMPANY_ID = "companyID";

    @InjectPresenter
    CompanyDetailPresenter presenter;

    private FloatingActionButton fabAddReview;
    private CoordinatorLayout ltContent;
    TextView tvCompanyName;
    TextView tvSite;
    TextView tvCity;
    ImageView ivLogo;
    private NestedScrollView scrollView;
    private LinearLayout ltNoReviews;
    private ProgressBar progressReview;
    private RelativeLayout ltAuthCompany;
    private Button btnLogin;
    private TextView tvAuth;
    private TextView tvShowDescription;
    private TextView tvDescription;
    private boolean descriptionShow;

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView rvReviews;
    private List<Review> reviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private CompanyDetail companyDetail;
    private String companyId;

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

        setupToolbar();
        detData(savedInstanceState);
        setupUI();
        setupUX();
        setupRecycler(reviews);
        setupFabBehavior();
        ((MainActivity) getActivity()).hideBottomNavigation();
        return rootView;
    }

    private void detData(Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        companyId = bundle.getString(COMPANY_ID);
        presenter.getCompanyDetail(companyId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COMPANY_ID, companyId);
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
            tvShowDescription.setOnClickListener(v -> showDescription());
        }

    private void setupUI() {
        fabAddReview = bindView(R.id.fabAddReview);
        ltContent = bindView(R.id.ltContent);
        ltError = bindView(R.id.ltError);
        mProgressBar = bindView(R.id.progressBar);
        btnRetry = bindView(R.id.btnRetry);
        tvCompanyName = bindView(R.id.tvCompanyName);
        tvSite = bindView(R.id.tvSite);
        ivLogo = bindView(R.id.ivLogo);
        tvCity = bindView(R.id.tvCity);
        scrollView = bindView(R.id.scrollView);
        ltNoReviews = bindView(R.id.ltNoReviews);
        progressReview = bindView(R.id.progressReview);
        ltAuthCompany = bindView(R.id.ltAuthCompany);
        btnLogin = bindView(R.id.btnLogin);
        tvAuth = bindView(R.id.tvAuth);
        tvShowDescription = bindView(R.id.tvShowDescription);
        tvDescription = bindView(R.id.tvDescription);
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
                SelectedReviewFragment reviewFragment = SelectedReviewFragment.newInstance(review.getFirebaseKey(), false);
                replaceFragment(reviewFragment, R.id.main_container, true);
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

    private void showDescription() {
        if (descriptionShow) {
            descriptionShow = false;
            tvShowDescription.setText(R.string.show_all);
            tvDescription.setMaxLines(4);
        } else {
            descriptionShow = true;
            tvShowDescription.setText(R.string.hide);
            tvDescription.setMaxLines(Integer.MAX_VALUE);
        }
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
        AddReviewFragment review =
                AddReviewFragment.Companion.newInstance(companyDetail.getId(), companyDetail.getName());
        replaceFragment(review, R.id.main_container, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddReview:
                presenter.checkAuthUser();
                break;
            case R.id.btnRetry:
                presenter.getCompanyDetail(companyId);
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
        setCompanyName(companyDetail.getName());
        loadImage(companyDetail);
        setDescription();
    }

    private void setDescription() {
        String description = companyDetail.getDescription();
        tvDescription.setText(description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            tvDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
        else tvDescription.setText(Html.fromHtml(description));
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

    private void setCompanyName(String name) {
        tvCompanyName.setText(name);
        getActionBar().setTitle(name);
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
