package com.druger.aboutwork.fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.ReviewAdapter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.MyReviewsView;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.MyReviewsPresenter;

import java.util.List;

import static com.druger.aboutwork.Const.Bundles.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyReviewsFragment extends BaseFragment implements MyReviewsView {

    @InjectPresenter
    MyReviewsPresenter myReviewsPresenter;

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private ItemTouchHelper touchHelper;
    @SuppressWarnings("FieldCanBeLocal")
    private ItemTouchHelper.SimpleCallback simpleCallback;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private boolean itemSwipe = true;

    private TextView tvCountReviews;
    private BottomNavigationView bottomNavigation;

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
        initSwipe();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString(USER_ID, userId);
        }

        myReviewsPresenter.fetchReviews(userId);
        return rootView;
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(R.string.my_reviews);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecycler(final List<Review> reviews) {
        reviewAdapter = new ReviewAdapter(getActivity(), reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setNestedScrollingEnabled(false);

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
                    itemSwipe = false;
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
        tvCountReviews = bindView(R.id.tvCountReviews);
        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
        recyclerView = bindView(R.id.recycler_view);
    }

    private void initSwipe() {
        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Review review = myReviewsPresenter.getReview(position);
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.coordinator), R.string.review_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> {
                            myReviewsPresenter.addReview(position, review);
                            reviewAdapter.notifyItemInserted(position);
                            recyclerView.scrollToPosition(position);
                        });
                showSnackbar(snackbar);
                myReviewsPresenter.removeReview(position);
                reviewAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return itemSwipe && super.isItemViewSwipeEnabled();
            }
        };
        touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(recyclerView);
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
        tvCountReviews.setText(String.valueOf(reviews.size()));
        setupRecycler(reviews);
    }

    @Override
    public void notifyDataSetChanged() {
        reviewAdapter.notifyDataSetChanged();
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
                    final List<Review> deletedReviews = getDeletedReviews();
                    Snackbar snackbar = makeSnackbar(deletedReviews);
                    showSnackbar(snackbar);
                    mode.finish();
                    return true;
                default:
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
            itemSwipe = true;
        }
    }

    private void showSnackbar(Snackbar snackbar) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, bottomNavigation.getHeight());
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
