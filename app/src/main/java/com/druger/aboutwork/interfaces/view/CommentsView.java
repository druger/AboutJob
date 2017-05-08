package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.Comment;

import java.util.List;

/**
 * Created by druger on 08.05.2017.
 */

public interface CommentsView extends MvpView {

    void showComments(List<Comment> comments);

    void showChangeDialog(int position);

    void notifyItemRemoved(int position, int size);
}
