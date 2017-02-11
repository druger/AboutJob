package com.druger.aboutwork.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.recyclerview_helper.ItemClickListener;
import com.druger.aboutwork.ui.activities.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyReviewsFragment extends Fragment implements ValueEventListener {

    private List<Review> reviews;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    private TextView countReviews;

    private String userId;

    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    public MyReviewsFragment() {
        // Required empty public constructor
    }

    public static MyReviewsFragment newInstance(String userId) {
        MyReviewsFragment myReviews = new MyReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        myReviews.setArguments(bundle);
        return myReviews;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_reviews, container, false);

        ((MainActivity) getActivity()).setActionBarTitle(R.string.my_reviews);
        ((MainActivity) getActivity()).setBackArrowActionBar();

        dbReference = FirebaseDatabase.getInstance().getReference();

        countReviews = (TextView) view.findViewById(R.id.count_reviews);

        reviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviews);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        reviewAdapter.setOnClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (actionMode != null) {
                    toggleSelection(position);
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                if (actionMode == null) {
                    actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);
                }
                toggleSelection(position);
                return true;
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId", userId);
        }

        Query reviewsQuery = dbReference.child("reviews").orderByChild("userId").equalTo(userId);
        reviewsQuery.addValueEventListener(this);
        return view;
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        fetchReviews(dataSnapshot);
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void fetchReviews(DataSnapshot dataSnapshot) {
        reviews.clear();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            final Review review = snapshot.getValue(Review.class);
            Query queryByCompanyId = dbReference.child("companies").orderByChild("id").equalTo(review.getCompanyId());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Company company = data.getValue(Company.class);
                            review.setName(company.getName());
                            reviewAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryByCompanyId.addValueEventListener(valueEventListener);
            review.setFirebaseKey(snapshot.getKey());
            reviews.add(review);
        }
        countReviews.setText(String.valueOf(reviews.size()));
        reviewAdapter.notifyDataSetChanged();
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        reviewAdapter.toggleSelection(position);
        int count = reviewAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    reviewAdapter.removeItems(reviewAdapter.getSelectedItems());
                    List<Review> deletedReviews = reviewAdapter.getDeletedReviews();
                    for (Review review : deletedReviews) {
                        FirebaseHelper.removeReview(review.getFirebaseKey());
                    }
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            reviewAdapter.clearSelection();
            actionMode = null;
        }
    }
}
