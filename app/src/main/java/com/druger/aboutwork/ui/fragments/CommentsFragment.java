package com.druger.aboutwork.ui.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
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
import com.druger.aboutwork.utils.SharedPreferencesHelper;
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

    private EditText message;
    private ImageView send;

    private RecyclerView recyclerView;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;

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
        message = (EditText) view.findViewById(R.id.et_message);
        send = (ImageView) view.findViewById(R.id.send_message);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = getArguments();
        reviewId = bundle.getString("reviewId");

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    send.setClickable(true);
                    send.setColorFilter(Color.parseColor("#F44336"));
                } else {
                    send.setClickable(false);
                    send.setColorFilter(Color.parseColor("#EF9A9A"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(message.getText().toString().trim());
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
    }

    private void sendMessage(String message) {
        if (message.length() > 0) {
            Calendar calendar = Calendar.getInstance();
            Comment comment = new Comment(message, calendar.getTimeInMillis());
            comment.setUserName(SharedPreferencesHelper.getUserName(getActivity()));
            if (user != null) {
                comment.setUserId(user.getUid());
            }
            comment.setReviewId(reviewId);
            FirebaseHelper.addComment(comment);
            this.message.setText(null);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        comments.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Comment comment = snapshot.getValue(Comment.class);
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
