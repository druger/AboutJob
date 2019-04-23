package com.druger.aboutwork.di.components

import com.druger.aboutwork.di.modules.AppModule
import com.druger.aboutwork.di.modules.NetworkModule
import com.druger.aboutwork.fragments.AccountFragment
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

    fun inject(fragment: AccountFragment)
}
