package com.druger.aboutwork.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.druger.aboutwork.Const.Bundles.REVIEW
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.databinding.FragmentReviewBinding
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.EditReviewPresenter

class EditReviewFragment: ReviewFragment() {

    @InjectPresenter
    lateinit var presenter: EditReviewPresenter

    private var review: Review? = null

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
        val binding = DataBindingUtil
                .inflate<FragmentReviewBinding>(inflater, R.layout.fragment_review, container, false)
        binding.review = review
        rootView = binding.root
        (activity as MainActivity).hideBottomNavigation()
        setStatus()
        return super.onCreateView(inflater, container, savedInstanceState)
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
}