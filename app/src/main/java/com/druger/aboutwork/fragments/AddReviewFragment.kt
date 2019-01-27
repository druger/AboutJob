package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.druger.aboutwork.Const.Bundles.COMPANY_DETAIL
import com.druger.aboutwork.R
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.presenters.AddReviewPresenter


class AddReviewFragment : ReviewFragment() {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    private var companyDetail: CompanyDetail? = null

    companion object{
        fun newInstance(companyDetail: CompanyDetail): AddReviewFragment {

            val args = Bundle()
            args.putParcelable(COMPANY_DETAIL, companyDetail)

            val fragment = AddReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        presenter.companyId = companyDetail?.id
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getBundles() {
        val bundle = arguments
        if (bundle != null) {
            companyDetail = bundle.get(COMPANY_DETAIL) as CompanyDetail
        }
    }

    override fun setupToolbar() {
        super.setupToolbar()
        tvTitle.setText(R.string.add_review)
    }

    override fun setupCompanyRating() = presenter.setupReview()

}
