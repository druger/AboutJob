package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.COMPANY_DETAIL
import com.druger.aboutwork.R
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.AddReviewPresenter


class AddReviewFragment : ReviewFragment() {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    @ProvidePresenter
    fun provideAddReviewPresenter(): AddReviewPresenter {
        return App.appComponent.addReviewPresenter
    }

    companion object{
        fun newInstance(companyDetail: CompanyDetail): AddReviewFragment {

            val args = Bundle()
            args.putParcelable(COMPANY_DETAIL, companyDetail)

            val fragment = AddReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBundles() {
        val bundle = arguments
        if (bundle != null) {
            presenter.companyDetail = bundle.get(COMPANY_DETAIL) as CompanyDetail
        }
    }

    override fun setupToolbar() {
        super.setupToolbar()
        tvTitle.setText(R.string.add_review)
    }

    override fun setupCompanyRating() = presenter.setupReview()

    override fun getReview(): Review  = presenter.review

    override fun setSocialPackage(rating: Float) = presenter.setSocialPackage(rating)

    override fun setCollective(rating: Float) = presenter.setCollective(rating)

    override fun setCareer(rating: Float) = presenter.setCareer(rating)

    override fun setWorkplace(rating: Float) = presenter.setWorkplace(rating)

    override fun setChief(rating: Float) = presenter.setChief(rating)

    override fun setSalary(rating: Float) = presenter.setSalary(rating)
}
