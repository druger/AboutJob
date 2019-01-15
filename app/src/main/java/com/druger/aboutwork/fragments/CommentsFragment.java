package com.druger.aboutwork.fragments;


import android.app.Fragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.presenters.CommentsPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends BaseFragment {

    @InjectPresenter
    CommentsPresenter commentsPresenter;

    private CommentAdapter commentAdapter;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
