package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.CompanyDetailPresenter;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyDetailFragment extends MvpFragment implements View.OnClickListener,
        CompanyDetailView {
    public static final int REVIEW_REQUEST = 0;

    @InjectPresenter
    CompanyDetailPresenter companyDetailPresenter;

    private View view;
    private TextView tvDescription;
    private ImageView ivDownDrop;
    private ImageView ivUpDrop;
    private TextView tvRating;
    private TextView tvCountReviews;
    private TextView site;
    private RatingBar ratingCompany;
    private ImageView ivToolbar;
    private FloatingActionButton fab;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView recyclerView;
    private List<Review> reviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private CompanyDetail detail;

    public CompanyDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company_detail, container, false);
        detail = getActivity().getIntent().getExtras().getParcelable("companyDetail");

        setupUI(view);
        setupUX();
        setupRecycler(view, reviews);

        companyDetailPresenter.setReviews(detail);
        return view;
    }

    private void setupUX() {
        ivDownDrop.setOnClickListener(this);
        ivUpDrop.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void setupUI(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);

        site = (TextView) view.findViewById(R.id.tvSite);
        tvDescription = (TextView) view.findViewById(R.id.tvContentDescription);
        ivDownDrop = (ImageView) view.findViewById(R.id.ivDownDrop);
        ivUpDrop = (ImageView) view.findViewById(R.id.ivUpDrop);
        ivToolbar = (ImageView) view.findViewById(R.id.ivToolbar);
        tvCountReviews = (TextView) view.findViewById(R.id.tvCountReviews);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        ratingCompany = (RatingBar) view.findViewById(R.id.rating_company);
        fab = (FloatingActionButton) view.findViewById(R.id.fabAddReview);

        tvDescription.setVisibility(View.GONE);

        collapsingToolbar.setTitle(detail.getName());
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));

        String iSite = detail.getSite();
        String iDescription = detail.getDescription();
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

        CompanyDetail.Logo logo = detail.getLogo();
        Glide.with(this)
                .load(logo != null ? logo.getOriginal() : "")
                .placeholder(R.drawable.default_company)
                .error(R.drawable.default_company)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivToolbar);
    }

    private void setupRecycler(View view, final List<Review> reviews) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        reviewAdapter = new ReviewAdapter(reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        reviewAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Review review = reviews.get(position);
                SelectedReviewFragment reviewFragment = SelectedReviewFragment.newInstance(review, false);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.company_container, reviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
    }

    private void addReview() {
        ReviewFragment review = new ReviewFragment();

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
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
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
}
