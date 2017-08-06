package com.druger.aboutwork.di.components;

import com.druger.aboutwork.di.modules.AppModule;
import com.druger.aboutwork.di.modules.NetworkModule;
import com.druger.aboutwork.fragments.AccountFragment;
import com.druger.aboutwork.presenters.CommentsPresenter;
import com.druger.aboutwork.presenters.CompaniesPresenter;
import com.druger.aboutwork.presenters.SignupPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by druger on 30.07.2017.
 */

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(AccountFragment fragment);
    void inject(CommentsPresenter presenter);
    void inject(SignupPresenter presenter);

    CompaniesPresenter getCompaniesPresenter();
}
