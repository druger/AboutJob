package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CommentsView;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.presenters.CommentsPresenter;
import com.druger.aboutwork.utils.Utils;

import java.util.List;

import static com.druger.aboutwork.Const.Bundles.FROM_ACCOUNT;
import static com.druger.aboutwork.Const.Bundles.REVIEW_ID;
import static com.druger.aboutwork.Const.Colors.RED_200;
import static com.druger.aboutwork.Const.Colors.RED_500;
import static com.druger.aboutwork.R.string.comments;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends BaseFragment implements CommentsView {
    public static final int NEW = 0;
    public static final int UPDATE = 1;
    private int type = NEW;

    @InjectPresenter
    CommentsPresenter commentsPresenter;

    private EditText etMessage;
    private ImageView ivSend;
    private BottomNavigationView bottomNavigation;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    private String reviewId;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(String reviewId, boolean fromAccount) {

        Bundle args = new Bundle();
        args.putString(REVIEW_ID, reviewId);
        args.putBoolean(FROM_ACCOUNT, fromAccount);

        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        Bundle bundle = getArguments();
        reviewId = bundle.getString(REVIEW_ID);

        setupToolbar();
        setupUI();
        setupListeners();
        setupRecycler();
        retrieveComments();
        hideBottomNavigation();
        return rootView;
    }

    private void hideBottomNavigation() {
        if (getArguments().getBoolean(FROM_ACCOUNT)) {
            bottomNavigation.setVisibility(View.INVISIBLE);
        }
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
                case 0:
                    sendMessage(etMessage.getText().toString().trim(), NEW);
                    break;
                case 1:
                    sendMessage(etMessage.getText().toString().trim(), UPDATE);
                    break;
            }
        });
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(comments);
    }

    private void setupUI() {
        recyclerView = bindView(R.id.recycler_view);
        etMessage = bindView(R.id.etMessage);
        ivSend = bindView(R.id.ivSend);
        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
    }

    private void retrieveComments() {
        commentsPresenter.retrieveComments(reviewId);
    }

    private void setupRecycler() {
        commentAdapter = new CommentAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);

        changeComment();
        avatarClick();
    }

    private void avatarClick() {
        commentAdapter.setOnAvatarClickListener(comment -> showReviews(comment.getUserId()));
    }

    private void showReviews(String userId) {
        UserReviewsFragment reviews = UserReviewsFragment.newInstance(userId);

        FragmentTransaction transaction =getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, reviews);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void changeComment() {
        commentAdapter.setOnItemClickListener(new OnItemClickListener<Comment>() {
            @Override
            public void onClick(Comment item, int position) {

            }

            @Override
            public boolean onLongClick(int position) {
                return commentsPresenter.onLongClick(position);
            }
        });
    }

    private void sendMessage(String message, int type) {
        if (message.length() > 0) {
            if (type == NEW) {
                commentsPresenter.addComment(message, reviewId);
            } else if (type == UPDATE) {
                commentsPresenter.updateComment(message);
                Utils.INSTANCE.hideKeyboard(getActivity(), this.etMessage);
                this.type = NEW;
            }
            this.etMessage.setText(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showBottomNavigation();
    }

    private void showBottomNavigation() {
        if (getArguments().getBoolean(FROM_ACCOUNT)) {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        commentsPresenter.removeListeners();
    }

    @Override
    public void showComments(List<Comment> comments) {
        commentAdapter.clear();
        commentAdapter.addItems(comments);
    }

    @Override
    public void showChangeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.comments_change, (dialog, which) -> {
            switch (which) {
                case 0:
                    commentsPresenter.deleteComment(position);
                    break;
                case 1:
                    etMessage.setText(commentsPresenter.getComment().getMessage());
                    Utils.INSTANCE.showKeyboard(getActivity());
                    etMessage.setFocusableInTouchMode(true);
                    etMessage.setSelection(commentsPresenter.getComment().getMessage().length());
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
}
