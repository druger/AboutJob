package com.druger.aboutwork.presenters;

import android.app.Activity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.CommentsView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by druger on 08.05.2017.
 */

@InjectViewState
public class CommentsPresenter extends MvpPresenter<CommentsView> implements ValueEventListener {

    private FirebaseUser user;
    private DatabaseReference dbReference;

    private List<Comment> comments;
    private Comment comment;

    public CommentsPresenter() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        comments = new ArrayList<>();
    }


    public void retrieveComments(String reviewId) {
        dbReference = FirebaseDatabase.getInstance().getReference();
        Query commentsQuery = dbReference.child("comments").orderByChild("reviewId").equalTo(reviewId);
        commentsQuery.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        comments.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Comment comment = snapshot.getValue(Comment.class);
            comment.setId(snapshot.getKey());
            comments.add(comment);
        }
        getViewState().showComments(comments);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void removeListeners() {
        dbReference.removeEventListener(this);
    }

    public boolean onLongClick(int position) {
        comment = comments.get(position);
        if (comment.getUserId().equals(user.getUid())) {
            getViewState().showChangeDialog(position);
            return true;
        }
        return false;
    }

    public void deleteComment(int position) {
        FirebaseHelper.deleteComment(comment.getId());
        comments.remove(position);
        getViewState().notifyItemRemoved(position, comments.size());
    }

    public Comment getComment() {
        return comment;
    }

    public void addComment(Activity activity, String message, String reviewId) {
        Calendar calendar = Calendar.getInstance();
        Comment comment = new Comment(message, calendar.getTimeInMillis());
        comment.setUserName(SharedPreferencesHelper.getUserName(activity));
        if (user != null) {
            comment.setUserId(user.getUid());
        }
        comment.setReviewId(reviewId);
        FirebaseHelper.addComment(comment);
    }

    public void updateComment(String message) {
        FirebaseHelper.updateComment(comment.getId(), message);
    }
}
