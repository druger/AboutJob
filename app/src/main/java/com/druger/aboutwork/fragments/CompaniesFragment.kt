package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.CompaniesPresenter
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class CompaniesFragment : BaseSupportFragment(), CompaniesView {

    @InjectPresenter
    lateinit var companiesPresenter: CompaniesPresenter

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var itemClickListener: OnItemClickListener<Review>

    private var inputMode: Int = 0

    @ProvidePresenter
    internal fun provideCompaniesPresenter(): CompaniesPresenter {
        return App.appComponent.companiesPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_companies, container, false)
        setInputMode()
        (activity as MainActivity).showBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupToolbar()
        setupListeners()
        setupRecycler()
        reviewAdapter.removeReviews()
        companiesPresenter.fetchReviews()
    }

    private fun setupUI() {
        mProgressBar = progressBar
    }

    private fun setInputMode() {
        activity?.window?.let { window ->
            window.attributes?.softInputMode?.let { mode ->
                inputMode = mode
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            }
        }
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        mToolbar?.let { setActionBar(it) }
        actionBar?.setTitle(R.string.search)
        ivSearch.visibility = View.VISIBLE
        ivSearch.setOnClickListener {
            replaceFragment(SearchFragment(), R.id.main_container, true)
        }
    }

    private fun setupRecycler() {
        reviewAdapter = ReviewAdapter()
        rvLastReviews.adapter = reviewAdapter
        reviewAdapter.setOnClickListener(itemClickListener)
    }

    private fun setupListeners() {
        itemClickListener = object : OnItemClickListener<Review> {
            override fun onClick(review: Review, position: Int) {
                review.firebaseKey?.let { showSelectedReview(it) }
            }

            override fun onLongClick(position: Int): Boolean {
                return false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(inputMode)
    }

    override fun showReview(review: Review) {
        reviewAdapter.addReview(review)
    }

    override fun showEmptyReviews() {
        groupReviews.visibility = View.GONE
        ltNoReviews.visibility = View.VISIBLE
    }

    private fun showSelectedReview(id: String) {
        val fragment = SelectedReviewFragment.newInstance(id, false)
        replaceFragment(fragment, R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            rvLastReviews.visibility = View.INVISIBLE
            tvLastReviews.visibility = View.GONE
        } else {
            rvLastReviews.visibility = View.VISIBLE
        }
    }
}