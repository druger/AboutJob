package com.druger.aboutwork.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.MyReviewAdapter;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.MyReviewsView;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.MyReviewsPresenter;
import com.druger.aboutwork.utils.recycler.RecyclerItemTouchHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.druger.aboutwork.Const.Bundles.USER_ID;

public class MyReviewsFragment extends BaseSupportFragment implements MyReviewsView,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @InjectPresenter
    MyReviewsPresenter myReviewsPresenter;

    private RecyclerView rvReviews;
    private MyReviewAdapter reviewAdapter;
    private List<Review> reviews = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private ItemTouchHelper touchHelper;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerItemTouchHelper simpleCallback;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    private BottomNavigationView bottomNavigation;
    private LinearLayout ltNoReviews;
    private RelativeLayout content;

    private String userId;

    public MyReviewsFragment() {
        // Required empty public constructor
    }

    public static MyReviewsFragment newInstance(String userId) {
        MyReviewsFragment myReviews = new MyReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        myReviews.setArguments(bundle);
        return myReviews;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_reviews, container, false);

        setupUI();
        setupToolbar();
        setupRecycler(reviews);
        initSwipe();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString(USER_ID, userId);
        }

        myReviewsPresenter.fetchReviews(userId);
        return rootView;
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setTitle(R.string.my_reviews);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecycler(final List<Review> reviews) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        reviewAdapter = new MyReviewAdapter(reviews);
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setItemAnimator(new DefaultItemAnimator());
        rvReviews.setAdapter(reviewAdapter);

        reviewAdapter.setOnClickListener(new OnItemClickListener<Review>() {
            @Override
            public void onClick(Review review, int position) {
                if (actionMode != null) {
                    toggleSelection(position);
                } else {
                    showSelectedReview(review);
                }
            }

            @Override
            public boolean onLongClick(int position) {
                if (actionMode == null) {
                    actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);
                    simpleCallback.setItemSwipe(false);
                }
                toggleSelection(position);
                return true;
            }
        });
    }

    private void showSelectedReview(Review review) {
        SelectedReviewFragment reviewFragment = SelectedReviewFragment.newInstance(review, true);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, reviewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupUI() {
        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
        rvReviews = bindView(R.id.recycler_view);
        mProgressBar = bindView(R.id.progressBar);
        ltNoReviews = bindView(R.id.ltNoReviews);
        content = bindView(R.id.content);
    }

    private void initSwipe() {
        simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(rvReviews);
    }

    @Override
    public void onStop() {
        super.onStop();
        myReviewsPresenter.removeListeners();
    }

    /**
     * Toggle the selection state of an item.
     * <p>
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

    @Override
    public void showReviews(List<Review> reviews) {
        if (reviews.isEmpty()) {
            ltNoReviews.setVisibility(View.VISIBLE);
            content.setVisibility(View.INVISIBLE);
        } else {
            ltNoReviews.setVisibility(View.INVISIBLE);
            content.setVisibility(View.VISIBLE);
        }
        this.reviews.clear();
        this.reviews.addAll(reviews);
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show) content.setVisibility(View.INVISIBLE);
        else content.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof ReviewAdapter.ReviewVH) {
            final int position = viewHolder.getAdapterPosition();
            final Review review = myReviewsPresenter.getReview(position);

            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.coordinator), R.string.review_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        reviews.add(position, review);
                        reviewAdapter.notifyItemInserted(position);
                        myReviewsPresenter.addReview(position, review);
                        rvReviews.scrollToPosition(position);
                    });
            showSnackbar(snackbar);
            reviews.remove(position);
            reviewAdapter.notifyItemRemoved(position);
            myReviewsPresenter.removeReview(position);
        }
    }

    // TODO сделать класс статическим
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
            int i = item.getItemId();
            if (i == R.id.menu_delete) {
                reviewAdapter.removeItems(reviewAdapter.getSelectedItems());
                final List<Review> deletedReviews = getDeletedReviews();
                Snackbar snackbar = makeSnackbar(deletedReviews);
                showSnackbar(snackbar);
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @NonNull
        private List<Review> getDeletedReviews() {
            final List<Review> deletedReviews = reviewAdapter.getDeletedReviews();
            for (Review review : deletedReviews) {
                FirebaseHelper.removeReview(review.getFirebaseKey());
            }
            return deletedReviews;
        }

        @NonNull
        private Snackbar makeSnackbar(List<Review> deletedReviews) {
            return Snackbar
                    .make(getActivity().findViewById(R.id.coordinator), R.string.review_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        myReviewsPresenter.addDeletedReviews(deletedReviews);
                        reviewAdapter.notifyDataSetChanged();
                        for (Review review : deletedReviews) {
                            myReviewsPresenter.addToFirebase(review);
                        }
                    });
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            reviewAdapter.clearSelection();
            actionMode = null;
            simpleCallback.setItemSwipe(true);
        }
    }

    private void showSnackbar(Snackbar snackbar) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, bottomNavigation.getHeight());
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
