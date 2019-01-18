package com.druger.aboutwork.fragments;


import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.databinding.FragmentSelectedReviewBinding;
import com.druger.aboutwork.databinding.SelectedReviewNoActionbarBinding;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.SelectedReview;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.SelectedReviewPresenter;
import com.druger.aboutwork.utils.Utils;

import java.util.List;

import static com.druger.aboutwork.Const.Bundles.EDIT_MODE;
import static com.druger.aboutwork.Const.Bundles.NAME;
import static com.druger.aboutwork.Const.Bundles.REVIEW;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.GREEN_500;
import static com.druger.aboutwork.Const.Colors.RED_200;
import static com.druger.aboutwork.Const.Colors.RED_500;

public class SelectedReviewFragment extends BaseFragment implements View.OnClickListener, SelectedReview {
    private static final int NEW = 0;
    private static final int UPDATE = 1;
    private int type = NEW;

    @InjectPresenter
    SelectedReviewPresenter presenter;

    private TextView tvDescriptionStatus;
    private ImageView ivLike;
    private ImageView ivDislike;
    private Review review;
    private FloatingActionButton fabEdit;
    private EditText etMessage;
    private ImageView ivSend;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    private Bundle bundle;
    private boolean editMode;

    public SelectedReviewFragment() {
        // Required empty public constructor
    }

    public static SelectedReviewFragment newInstance(Review review, boolean fromAccount) {

        Bundle args = new Bundle();
        args.putParcelable(REVIEW, review);
        args.putBoolean(EDIT_MODE, fromAccount);

        SelectedReviewFragment fragment = new SelectedReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getBundles();
        setView(inflater, container);
        setUI();
        setUX();
        setReview();
        setupComments();
        retrieveComments();
        setupListeners();
        return rootView;
    }

    private void getBundles() {
        bundle = getArguments();
        review = bundle.getParcelable(REVIEW);
        editMode = getArguments().getBoolean(EDIT_MODE);
    }

    private void retrieveComments() {
        presenter.retrieveComments(review.getFirebaseKey());
    }

    private void setupListeners() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    ivSend.setClickable(true);
                    ivSend.setColorFilter(Color.parseColor(RED_500));
                } else {
                    ivSend.setClickable(false);
                    ivSend.setColorFilter(Color.parseColor(RED_200));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSend.setOnClickListener(v -> {
            switch (type) {
                case NEW:
                    sendMessage(etMessage.getText().toString().trim(), NEW);
                    break;
                case UPDATE:
                    sendMessage(etMessage.getText().toString().trim(), UPDATE);
                    break;
            }
        });

        commentAdapter.setOnItemClickListener(new OnItemClickListener<Comment>() {
            @Override
            public void onClick(Comment item, int position) {

            }

            @Override
            public boolean onLongClick(int position) {
                return presenter.onLongClick(position);
            }
        });

    }

    private void sendMessage(String message, int type) {
        if (message.length() > 0) {
            if (type == NEW) {
                presenter.addComment(message, review.getFirebaseKey());
            } else if (type == UPDATE) {
                presenter.updateComment(message);
                Utils.INSTANCE.hideKeyboard(getActivity(), this.etMessage);
                this.type = NEW;
            }
            this.etMessage.setText(null);
        }
    }

    private void setView(LayoutInflater inflater, ViewGroup container) {
        if (!editMode) {
            FragmentSelectedReviewBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.fragment_selected_review, container, false);
            binding.setReview(review);
            rootView = binding.getRoot();
            setupToolbar();
        } else {
            SelectedReviewNoActionbarBinding bindingNoBar = DataBindingUtil
                    .inflate(inflater, R.layout.selected_review_no_actionbar, container, false);
            bindingNoBar.setReview(review);
            rootView = bindingNoBar.getRoot();
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    private void setUX() {
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        if (editMode) {
            fabEdit.setOnClickListener(this);
        }
    }

    private void setUI() {
        tvDescriptionStatus = bindView(R.id.tvDescriptionStatus);
        ivLike = bindView(R.id.ivLike);
        ivDislike = bindView(R.id.ivDislike);
        fabEdit = bindView(R.id.fabEdit);
        etMessage = bindView(R.id.etMessage);
        ivSend = bindView(R.id.ivSend);
        rvComments = bindView(R.id.rvComments);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String companyName = getActivity().getIntent().getStringExtra(NAME);
        if (companyName != null) {
            getActionBar().setTitle(companyName);
        }
    }

    private void setReview() {
        if (review != null) {
            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                ivLike.setTag(getActivity().getString(R.string.like_inactive));
            } else {
                ivLike.setTag(getActivity().getString(R.string.like_active));
                ivLike.setColorFilter(Color.parseColor(GREEN_500));
            }
            if (!myDislike) {
                ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
            } else {
                ivDislike.setTag(getActivity().getString(R.string.dislike_active));
                ivDislike.setColorFilter(Color.parseColor(RED_500));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLike:
                clickLike();
                break;
            case R.id.ivDislike:
                clickDislike();
                break;
            case R.id.fabEdit:
                showEditReview();
                break;
            default:
                break;
        }
    }

    private void showEditReview() {
        ReviewFragment reviewFragment = ReviewFragment.newInstance(review, true);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, reviewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clickDislike() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.isMyDislike()) {
            ivDislike.setColorFilter(Color.parseColor(RED_500));
            review.setDislike(++dislike);
            review.setMyDislike(true);

            if (review.isMyLike()) {
                ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--like);
                review.setMyLike(false);
                FirebaseHelper.likeReview(review);
            }
        } else {
            ivDislike.setColorFilter(Color.parseColor(GRAY_500));
            review.setDislike(--dislike);
            review.setMyDislike(false);
        }
        FirebaseHelper.dislikeReview(review);
    }

    private void clickLike() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.isMyLike()) {
            ivLike.setColorFilter(Color.parseColor(GREEN_500));
            review.setLike(++like);
            review.setMyLike(true);

            if (review.isMyDislike()) {
                ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislike);
                review.setMyDislike(false);
                FirebaseHelper.dislikeReview(review);
            }
        } else {
            ivLike.setColorFilter(Color.parseColor(GRAY_500));
            review.setLike(--like);
            review.setMyLike(false);
        }
        FirebaseHelper.likeReview(review);
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvComments.setItemAnimator(new DefaultItemAnimator());
        rvComments.setAdapter(commentAdapter);
        commentAdapter.setOnNameClickListener(comment -> showReviews(comment.getUserId()));
    }

    private void showReviews(String userId) {
        UserReviewsFragment reviews = UserReviewsFragment.newInstance(userId);

        FragmentTransaction transaction =getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, reviews);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (editMode) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.removeListeners();
    }

    @Override
    public void showChangeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.comments_change, (dialog, which) -> {
            switch (which) {
                case 0:
                    presenter.deleteComment(position);
                    break;
                case 1:
                    etMessage.setText(presenter.getComment().getMessage());
                    Utils.INSTANCE.showKeyboard(getActivity());
                    etMessage.setFocusableInTouchMode(true);
                    etMessage.setSelection(presenter.getComment().getMessage().length());
                    type = UPDATE;
                    break;
            }
        });
        builder.show();
    }

    @Override
    public void notifyItemRemoved(int position, int size) {
        commentAdapter.notifyItemRemoved(position);
        commentAdapter.notifyItemRangeChanged(position, size);
    }

    @Override
    public void showComments(List<Comment> comments) {
        commentAdapter.clear();
        commentAdapter.addItems(comments);
    }
}
