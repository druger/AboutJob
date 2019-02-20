package com.druger.aboutwork.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.CommentAdapter;
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
import static com.druger.aboutwork.Const.Colors.DISLIKE;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.LIKE;
import static com.druger.aboutwork.Const.Colors.RED_200;
import static com.druger.aboutwork.Const.Colors.RED_500;

public class SelectedReviewFragment extends BaseSupportFragment implements View.OnClickListener, SelectedReview {
    private static final int NEW = 0;
    private static final int UPDATE = 1;
    private int type = NEW;

    @InjectPresenter
    SelectedReviewPresenter presenter;

    private TextView tvDescriptionStatus;
    private ImageView ivLike;
    private ImageView ivDislike;
    private Review review;
    private ImageView ivEdit;
    private EditText etMessage;
    private ImageView ivSend;
    private TextView tvPluses;
    private TextView tvMinuses;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvPosition;
    private RatingBar rbSalary;
    private RatingBar rbCareer;
    private RatingBar rbCollective;
    private RatingBar rbSocialPackage;
    private RatingBar rbChief;
    private RatingBar rbWorkplace;
    private TextView tvDislike;
    private TextView tvLike;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    private Bundle bundle;
    private boolean editMode;

    public SelectedReviewFragment() {
        // Required empty public constructor
    }

    public static SelectedReviewFragment newInstance(Review review, boolean editMode) {

        Bundle args = new Bundle();
        args.putParcelable(REVIEW, review);
        args.putBoolean(EDIT_MODE, editMode);

        SelectedReviewFragment fragment = new SelectedReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getBundles();
        setView(inflater, container);
        setupToolbar();
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
        rootView = inflater.inflate(R.layout.fragment_selected_review, container, false);
        if (editMode) {
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    private void setUX() {
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        if (editMode) {
            ivEdit.setOnClickListener(this);
        }
    }

    private void setUI() {
        tvDescriptionStatus = bindView(R.id.tvDescriptionStatus);
        ivLike = bindView(R.id.ivLike);
        ivDislike = bindView(R.id.ivDislike);
        etMessage = bindView(R.id.etMessage);
        ivSend = bindView(R.id.ivSend);
        rvComments = bindView(R.id.rvComments);
        tvPluses = bindView(R.id.tvPluses);
        tvMinuses = bindView(R.id.tvMinuses);
        tvName = bindView(R.id.tvName);
        tvDate = bindView(R.id.tvDate);
        tvPosition = bindView(R.id.tvPosition);
        rbSalary = bindView(R.id.ratingbar_salary);
        rbCareer = bindView(R.id.ratingbar_career);
        rbCollective = bindView(R.id.ratingbar_collective);
        rbSocialPackage = bindView(R.id.ratingbar_social_package);
        rbChief = bindView(R.id.ratingbar_chief);
        rbWorkplace = bindView(R.id.ratingbar_workplace);
        tvDislike = bindView(R.id.tvDislike);
        tvLike = bindView(R.id.tvLike);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        ivEdit = bindView(R.id.ivEdit);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String companyName = getActivity().getIntent().getStringExtra(NAME);
        if (companyName != null) {
            getActionBar().setTitle(companyName);
        }
        if (editMode) ivEdit.setVisibility(View.VISIBLE);
         else ivEdit.setVisibility(View.GONE);
    }

    private void setReview() {
        if (review != null) {
            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                ivLike.setTag(getActivity().getString(R.string.like_inactive));
            } else {
                ivLike.setTag(getActivity().getString(R.string.like_active));
                ivLike.setColorFilter(Color.parseColor(LIKE));
            }
            if (!myDislike) {
                ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
            } else {
                ivDislike.setTag(getActivity().getString(R.string.dislike_active));
                ivDislike.setColorFilter(Color.parseColor(DISLIKE));
            }
        }
        tvPluses.setText(Utils.getQuoteSpan(getActivity(), review.getPluses(), R.color.review_positive));
        tvMinuses.setText(Utils.getQuoteSpan(getActivity(), review.getMinuses(), R.color.review_negative));
        tvName.setText(review.getName());
        tvDate.setText(Utils.getDate(review.getDate()));
        tvPosition.setText(review.getPosition());
        rbSalary.setRating(review.getMarkCompany().getSalary());
        rbCareer.setRating(review.getMarkCompany().getCareer());
        rbCollective.setRating(review.getMarkCompany().getCollective());
        rbSocialPackage.setRating(review.getMarkCompany().getSocialPackage());
        rbChief.setRating(review.getMarkCompany().getChief());
        rbWorkplace.setRating(review.getMarkCompany().getWorkplace());
        tvDislike.setText(String.valueOf(review.getDislike()));
        tvLike.setText(String.valueOf(review.getLike()));
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
            case R.id.ivEdit:
                showEditReview();
                break;
            default:
                break;
        }
    }

    private void showEditReview() {
        EditReviewFragment reviewFragment = EditReviewFragment.Companion.newInstance(review);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, reviewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clickDislike() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.isMyDislike()) {
            ivDislike.setColorFilter(Color.parseColor(DISLIKE));
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
            ivLike.setColorFilter(Color.parseColor(LIKE));
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

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
