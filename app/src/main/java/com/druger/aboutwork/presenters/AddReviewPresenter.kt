package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.RestApi
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@InjectViewState
class AddReviewPresenter(restApi: RestApi): ReviewPresenter(restApi) {

    lateinit var companyDetail: CompanyDetail

    @ProvidePresenter
    internal fun provideAddReviewPresenter(): AddReviewPresenter {
        return App.appComponent.addReviewPresenter
    }

    fun setupReview() {
        val user = FirebaseAuth.getInstance().currentUser
        val companyId = companyDetail.id
        review = Review(companyId, user?.uid, Calendar.getInstance().timeInMillis)
        mark = MarkCompany(user?.uid, companyId)
        review.markCompany = mark
    }

    override fun doneClick() {
        val company = Company(companyDetail.id, companyDetail.name)
        addReview(company)
    }

    private fun addReview(company: Company) {
        if (isCorrectStatus && isCorrectReview(review)) {
            review.status = status
            FirebaseHelper.addReview(review)
            FirebaseHelper.addCompany(company)
            viewState.successfulAddition()
        } else {
            viewState.showErrorAdding()
        }
    }
}