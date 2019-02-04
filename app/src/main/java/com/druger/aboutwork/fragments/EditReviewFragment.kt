package com.druger.aboutwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.REVIEW
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.EditReviewPresenter
import com.druger.aboutwork.utils.Utils
import kotlinx.android.synthetic.main.content_review.*

class EditReviewFragment: ReviewFragment() {

    @InjectPresenter
    lateinit var presenter: EditReviewPresenter

    @ProvidePresenter
    fun provideEditReviewPresenter(): EditReviewPresenter {
        return App.appComponent.editReviewPresenter
    }

    private lateinit var review: Review

    companion object{
        fun newInstance(review: Review): EditReviewFragment {
            val args = Bundle()

            val fragment = EditReviewFragment()
            args.putParcelable(REVIEW, review)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).hideBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setStatus()
    }

    private fun setUI() {
        etPosition.setText(review.position)
        etPluses.setText(review.pluses)
        etMinuses.setText(review.minuses)
        etCity.setText(review.city)
        etEmploymentDate.setText(review?.employmentDate?.let { Utils.getDate(it) })
        etDismissalDate.setText(review?.dismissalDate?.let { Utils.getDate(it) })
        etInterviewDate.setText(review?.interviewDate?.let { Utils.getDate(it) })
    }

    override fun getBundles() {
        val bundle = arguments
        if (bundle != null) {
            review = bundle.get(REVIEW) as Review
        }
    }

    override fun setupToolbar() {
        super.setupToolbar()
        tvTitle.setText(R.string.edit_review)
    }

    private fun setStatus() =
        when (review?.status) {
            0 -> spinnerWorkStatus.setPromptId(R.string.working)
            1 -> spinnerWorkStatus.setPromptId(R.string.worked)
            2 -> spinnerWorkStatus.setPromptId(R.string.interview)
            else -> null
        }

    override fun setupCompanyRating() {
        presenter.setupRating(review)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomNavigation()
    }

    override fun getReview(): Review = review

    override fun setSocialPackage(rating: Float) = presenter.setSocialPackage(rating)

    override fun setCollective(rating: Float) = presenter.setCollective(rating)

    override fun setCareer(rating: Float) = presenter.setCareer(rating)

    override fun setWorkplace(rating: Float) = presenter.setWorkplace(rating)

    override fun setChief(rating: Float) = presenter.setChief(rating)

    override fun setSalary(rating: Float) = presenter.setSalary(rating)
}