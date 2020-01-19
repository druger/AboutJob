package com.druger.aboutwork.di.components

import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.di.modules.AppModule
import com.druger.aboutwork.di.modules.NetworkModule
import com.druger.aboutwork.fragments.AccountFragment
import com.druger.aboutwork.fragments.MyReviewsFragment
import com.druger.aboutwork.presenters.*
import dagger.Component
import javax.inject.Singleton

/**
 * Created by druger on 30.07.2017.
 */

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    val companiesPresenter: CompaniesPresenter

    val companyDetailPresenter: CompanyDetailPresenter

    val editReviewPresenter: EditReviewPresenter

    val addReviewPresenter: AddReviewPresenter

    val accountPresenter: AccountPresenter

    val searchPresenter: SearchPresenter

    fun inject(fragment: AccountFragment)

    fun inject(activity: LoginActivity)

    fun inject(presenter: AddReviewPresenter)

    fun inject(fragment: MyReviewsFragment)

    fun inject(presenter: SelectedReviewPresenter)

    fun inject(presenter: AccountPresenter)
}
