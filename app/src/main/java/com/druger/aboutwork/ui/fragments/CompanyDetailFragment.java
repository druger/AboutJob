package com.druger.aboutwork.ui.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyDetailFragment extends Fragment implements View.OnClickListener, ValueEventListener {
    public static final int REVIEW_REQUEST = 0;

    private TextView description;
    private ImageView downDrop;
    private ImageView upDrop;
    private TextView rating;
    private TextView countReviews;
    private RatingBar ratingCompany;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Review> reviews = new ArrayList<>();

    private FastItemAdapter<Review> fastItemAdapter;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    private Intent companyIntent;

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
        View view = inflater.inflate(R.layout.fragment_company_detail, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);

        TextView site = (TextView) view.findViewById(R.id.site);
        description = (TextView) view.findViewById(R.id.content_description);
        downDrop = (ImageView) view.findViewById(R.id.down_drop);
        upDrop = (ImageView) view.findViewById(R.id.up_drop);
        ImageView imgToolbar = (ImageView) view.findViewById(R.id.img_toolbar);
        countReviews = (TextView) view.findViewById(R.id.count_reviews);
        rating = (TextView) view.findViewById(R.id.rating);
        ratingCompany = (RatingBar) view.findViewById(R.id.rating_company);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        description.setVisibility(View.GONE);
        downDrop.setOnClickListener(this);
        upDrop.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });

        companyIntent = getActivity().getIntent();

        collapsingToolbar.setTitle(companyIntent.getStringExtra("name"));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));

        String iSite = companyIntent.getStringExtra("site");
        String iDescription = companyIntent.getStringExtra("description");
        if (iSite != null) {
            site.setText(iSite);
        }
        if (iDescription != null) {
            description.setText(Html.fromHtml(iDescription));
        }

        Glide.with(this)
                .load(companyIntent.getStringExtra("logo"))
                .placeholder(R.drawable.default_company)
                .error(R.drawable.default_company)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgToolbar);

        setReviews();
        return view;
    }

    private void setRating() {
        float sum = 0;
        float mRating = 0;

        if (!reviews.isEmpty()) {
            for (Review review : reviews) {
                MarkCompany markCompany = review.getMarkCompany();
                sum += markCompany != null ? markCompany.getAverageMark() : 0;
            }
            mRating = MarkCompany.roundMark(sum / reviews.size(), 2);
        }

        rating.setText(String.valueOf(mRating));
        ratingCompany.setRating(mRating);
    }

    private void setReviews() {
        fastItemAdapter = new FastItemAdapter<>();
        dbReference = FirebaseDatabase.getInstance().getReference();
        Query reviewsQuery = dbReference.child("reviews").orderByChild("companyId").equalTo(companyIntent.getStringExtra("id"));
        reviewsQuery.addValueEventListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fastItemAdapter.withSelectable(true);
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<Review>() {
            @Override
            public boolean onClick(View v, IAdapter<Review> adapter, Review item, int position) {
                SelectedReviewFragment reviewFragment = new SelectedReviewFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("review", item);
                reviewFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.company_container, reviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });
    }


    private void addReview() {
        ReviewFragment review = new ReviewFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        review.setTargetFragment(CompanyDetailFragment.this, REVIEW_REQUEST);
        transaction.replace(R.id.company_container, review);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchReviews(DataSnapshot dataSnapshot) {
        reviews.clear();
        fastItemAdapter.clear();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            final Review review = snapshot.getValue(Review.class);
            Query queryUserId = dbReference.child("users").orderByChild("id").equalTo(review.getUserId());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            review.setUserName(user.getName());
                            fastItemAdapter.notifyAdapterDataSetChanged();
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryUserId.addValueEventListener(valueEventListener);
            review.setFirebaseHelper(firebaseHelper);
            review.setFirebaseKey(dataSnapshot.getKey());
            reviews.add(review);
        }
        countReviews.setText(String.valueOf(reviews.size()));
        setRating();
        fastItemAdapter.add(reviews);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_drop:
                downDrop.setVisibility(View.INVISIBLE);
                upDrop.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                break;
            case R.id.up_drop:
                upDrop.setVisibility(View.INVISIBLE);
                downDrop.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dbReference.removeEventListener(this);
        if (valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REVIEW_REQUEST) {
                firebaseHelper.addReview((Review) data.getParcelableExtra("addedReview"));
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        fetchReviews(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
