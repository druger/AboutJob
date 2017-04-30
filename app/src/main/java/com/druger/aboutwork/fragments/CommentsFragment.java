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

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.recyclerview_helper.OnItemClickListener;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment implements ValueEventListener {
    public static final int NEW = 0;
    public static final int UPDATE = 1;
    private int type = NEW;

    private EditText etMessage;
    private ImageView ivSend;

    private RecyclerView recyclerView;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private Comment comment;

    private String reviewId;

    private FirebaseUser user;
    private DatabaseReference dbReference;

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

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.comments);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        ivSend = (ImageView) view.findViewById(R.id.ivSend);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = getArguments();
        reviewId = bundle.getString("reviewId");

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

        setComments();

        return view;
    }

    private void setComments() {
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        dbReference = FirebaseDatabase.getInstance().getReference();
        Query commentsQuery = dbReference.child("comments").orderByChild("reviewId").equalTo(reviewId);
        commentsQuery.addValueEventListener(this);

        changeComment();
    }

    private void changeComment() {
        commentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, final int position) {
                comment = comments.get(position);
                if (comment.getUserId().equals(user.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.comments_del, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    deleteComment(comment, position);
                                    break;
                                case 1:
                                    etMessage.setText(comment.getMessage());
                                    Utils.showKeyboard(getActivity());
                                    etMessage.setFocusableInTouchMode(true);
                                    etMessage.setSelection(comment.getMessage().length());
                                    type = UPDATE;
                                    break;
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
                return false;
            }
        });
    }

    private void deleteComment(Comment comment, int position) {
        FirebaseHelper.deleteComment(comment.getId());
        comments.remove(position);
        commentAdapter.notifyItemRemoved(position);
        commentAdapter.notifyItemRangeChanged(position, comments.size());
    }

    private void sendMessage(String message, int type) {
        if (message.length() > 0) {
            if (type == NEW) {
                Calendar calendar = Calendar.getInstance();
                Comment comment = new Comment(message, calendar.getTimeInMillis());
                comment.setUserName(SharedPreferencesHelper.getUserName(getActivity()));
                if (user != null) {
                    comment.setUserId(user.getUid());
                }
                comment.setReviewId(reviewId);
                FirebaseHelper.addComment(comment);
            } else if (type == UPDATE) {
                FirebaseHelper.updateComment(comment.getId(), message);
                Utils.hideKeyboard(getActivity(), this.etMessage);
                this.type = NEW;
            }
            this.etMessage.setText(null);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        comments.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Comment comment = snapshot.getValue(Comment.class);
            comment.setId(snapshot.getKey());
            comments.add(comment);
            commentAdapter.notifyItemChanged(comments.size() - 1);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
        dbReference.removeEventListener(this);
    }
}
