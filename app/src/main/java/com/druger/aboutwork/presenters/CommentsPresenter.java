package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.interfaces.view.CommentsView;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.PreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by druger on 08.05.2017.
 */

@InjectViewState
public class CommentsPresenter extends MvpPresenter<CommentsView>  {

    private FirebaseUser user;
    private DatabaseReference dbReference;

    private List<Comment> comments;
    private Comment comment;

    @Inject
    PreferencesHelper preferencesHelper;

    public CommentsPresenter() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        comments = new ArrayList<>();
        App.Companion.getAppComponent().inject(this);
    }


    public Comment getComment() {
        return comment;
    }

}
