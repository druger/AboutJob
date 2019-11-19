package com.druger.aboutwork.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseUser;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import moxy.presenter.InjectPresenter;

import static com.druger.aboutwork.Const.Bundles.EDIT_MODE;
import static com.druger.aboutwork.Const.Colors.DISLIKE;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.LIKE;
import static com.druger.aboutwork.Const.Colors.PURPLE_100;
import static com.druger.aboutwork.Const.Colors.PURPLE_500;

public class SelectedReviewFragment extends BaseSupportFragment implements View.OnClickListener, SelectedReview {
    private static final int NEW = 0;
    private static final int UPDATE = 1;
    private static final String REVIEW_KEY = "review_key";

    private int type = NEW;

    @InjectPresenter
    SelectedReviewPresenter presenter;

    private TextView tvDescriptionStatus;
    private TextView tvStatus;
    private ImageView ivLike;
    private ImageView ivDislike;
    private Review review;
    private String reviewKey;
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
    private Group groupRating;

    private Bundle bundle;
    private boolean editMode;

    public static SelectedReviewFragment newInstance(String reviewKey, boolean editMode) {

        Bundle args = new Bundle();
        args.putString(REVIEW_KEY, reviewKey);
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
        getReview();
        return rootView;
    }

    private void getBundles() {
        bundle = getArguments();
        reviewKey = bundle.getString(REVIEW_KEY);
        editMode = getArguments().getBoolean(EDIT_MODE);
    }

    @Override
    public void setupComments(@Nullable FirebaseUser user) {
        commentAdapter = new CommentAdapter(presenter.getUser());
        rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvComments.setItemAnimator(new DefaultItemAnimator());
        rvComments.setAdapter(commentAdapter);
        commentAdapter.setOnNameClickListener(comment -> showReviews(comment.getUserId()));

        retrieveComments();
        setupListeners();
    }

    private void retrieveComments() {
        presenter.retrieveComments(reviewKey);
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
                    ivSend.setColorFilter(Color.parseColor(PURPLE_500));
                } else {
                    ivSend.setClickable(false);
                    ivSend.setColorFilter(Color.parseColor(PURPLE_100));
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
        tvStatus = bindView(R.id.tvStatus);
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
        groupRating = bindView(R.id.group_rating);
    }

    private void setupToolbar() {
        mToolbar = bindView(R.id.toolbar);
        ivEdit = bindView(R.id.ivEdit);
        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.review);
        if (editMode) ivEdit.setVisibility(View.VISIBLE);
         else ivEdit.setVisibility(View.GONE);
    }

    private void getReview() {
        presenter.getReview(reviewKey);
    }

    @Override
    public void setReview(Review review) {
        if (review != null) {
            this.review = review;
            boolean myLike = review.getMyLike();
            boolean myDislike = review.getMyDislike();
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
            setExperience(review);
        }
    }

    private void setExperience(Review review) {
        switch (review.getStatus()) {
            case Review.WORKING:
                tvStatus.setText(R.string.working);
                setWorkingDays(review.getEmploymentDate(), Calendar.getInstance().getTimeInMillis());
                break;
            case Review.WORKED:
                tvStatus.setText(R.string.worked);
                setWorkingDays(review.getEmploymentDate(), review.getDismissalDate());
                break;
            case Review.INTERVIEW:
                tvStatus.setText(R.string.interview);
                long interviewDate = review.getInterviewDate();
                if (interviewDate != 0) tvDescriptionStatus.setText(Utils.getDate(interviewDate));
                groupRating.setVisibility(View.GONE);
                break;
        }
    }

    private void setWorkingDays(long first, long last) {
        if (first != 0) {
            Date f = new Date(first);
            Date l = new Date(last);
            LocalDate firstDate = Instant.ofEpochMilli(f.getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate lastDate = Instant.ofEpochMilli(l.getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            Period period = Period.between(firstDate, lastDate);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();
            Resources res = getResources();
            if (years > 0 && months > 0 && days > 0) {
                tvDescriptionStatus.setText(res.getQuantityString(R.plurals.year, years, years));
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.month, months,months));
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.day, days, days));
            } else if (years >= 0 && months <= 0 && days <= 0) {
                tvDescriptionStatus.setText(res.getQuantityString(R.plurals.year, years, years));
            } else if (years <= 0 && months > 0 && days >= 0) {
                tvDescriptionStatus.setText(res.getQuantityString(R.plurals.month, months, months));
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.day, days, days));
            } else if (years <= 0 && months <= 0) {
                tvDescriptionStatus.setText(res.getQuantityString(R.plurals.day, days, days));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLike:
                presenter.clickLike();
                break;
            case R.id.ivDislike:
                presenter.clickDislike();
                break;
            case R.id.ivEdit:
                showEditReview();
                break;
            default:
                break;
        }
    }

    private void showEditReview() {
        EditReviewFragment reviewFragment = EditReviewFragment.Companion.newInstance(reviewKey);
        replaceFragment(reviewFragment, R.id.main_container, true);
    }

    @Override
    public void onLikeClicked() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.getMyLike()) {
            ivLike.setColorFilter(Color.parseColor(LIKE));
            review.setLike(++like);
            tvLike.setText(String.valueOf(like));
            review.setMyLike(true);

            if (review.getMyDislike()) {
                ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislike);
                tvDislike.setText(String.valueOf(dislike));
                review.setMyDislike(false);
                FirebaseHelper.INSTANCE.dislikeReview(review);
            }
        } else {
            ivLike.setColorFilter(Color.parseColor(GRAY_500));
            review.setLike(--like);
            tvLike.setText(String.valueOf(like));
            review.setMyLike(false);
        }
        FirebaseHelper.INSTANCE.likeReview(review);
    }

    @Override
    public void onDislikeClicked() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.getMyDislike()) {
            ivDislike.setColorFilter(Color.parseColor(DISLIKE));
            review.setDislike(++dislike);
            tvDislike.setText(String.valueOf(dislike));
            review.setMyDislike(true);

            if (review.getMyLike()) {
                ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--like);
                tvLike.setText(String.valueOf(like));
                review.setMyLike(false);
                FirebaseHelper.INSTANCE.likeReview(review);
            }
        } else {
            ivDislike.setColorFilter(Color.parseColor(GRAY_500));
            review.setDislike(--dislike);
            tvDislike.setText(String.valueOf(dislike));
            review.setMyDislike(false);
        }
        FirebaseHelper.INSTANCE.dislikeReview(review);
    }

    private void showReviews(String userId) {
        UserReviewsFragment reviews = UserReviewsFragment.Companion.newInstance(userId);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, reviews);
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

    @Override
    public void showAuthDialog(int title) {
        Utils.INSTANCE.showAuthDialog(getActivity(), title);
    }
}
