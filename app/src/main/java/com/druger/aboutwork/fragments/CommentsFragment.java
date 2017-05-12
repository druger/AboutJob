package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.CommentsView;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.presenters.CommentsPresenter;
import com.druger.aboutwork.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends MvpFragment implements CommentsView{
    public static final int NEW = 0;
    public static final int UPDATE = 1;
    private int type = NEW;

    @InjectPresenter
    CommentsPresenter commentsPresenter;

    private EditText etMessage;
    private ImageView ivSend;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    private String reviewId;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(String reviewId) {

        Bundle args = new Bundle();
        args.putString("reviewId", reviewId);

        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        Bundle bundle = getArguments();
        reviewId = bundle.getString("reviewId");

        setupToolbar(view);
        setupUI(view);
        setupListeners();
        retrieveComments();

        return view;
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
                    ivSend.setColorFilter(Color.parseColor("#F44336"));
                } else {
                    ivSend.setClickable(false);
                    ivSend.setColorFilter(Color.parseColor("#EF9A9A"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        sendMessage(etMessage.getText().toString().trim(), NEW);
                        break;
                    case 1:
                        sendMessage(etMessage.getText().toString().trim(), UPDATE);
                        break;
                }
            }
        });
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.comments);
    }

    private void setupUI(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        ivSend = (ImageView) view.findViewById(R.id.ivSend);
    }

    private void retrieveComments() {
        commentsPresenter.retrieveComments(reviewId);
    }

    private void setupRecycler(List<Comment> comments) {
        commentAdapter = new CommentAdapter(comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        changeComment();
    }

    private void changeComment() {
        commentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, final int position) {
                return commentsPresenter.onLongClick(position);
            }
        });
    }

    private void sendMessage(String message, int type) {
        if (message.length() > 0) {
            if (type == NEW) {
                commentsPresenter.addComment(getActivity(), message, reviewId);
            } else if (type == UPDATE) {
                commentsPresenter.updateComment(message);
                Utils.hideKeyboard(getActivity(), this.etMessage);
                this.type = NEW;
            }
            this.etMessage.setText(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
        commentsPresenter.removeListeners();
    }

    @Override
    public void showComments(List<Comment> comments) {
        if (commentAdapter == null) {
            setupRecycler(comments);
        } else {
            commentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showChangeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.comments_change, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        commentsPresenter.deleteComment(position);
                        break;
                    case 1:
                        etMessage.setText(commentsPresenter.getComment().getMessage());
                        Utils.showKeyboard(getActivity());
                        etMessage.setFocusableInTouchMode(true);
                        etMessage.setSelection(commentsPresenter.getComment().getMessage().length());
                        type = UPDATE;
                        break;
                }
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
