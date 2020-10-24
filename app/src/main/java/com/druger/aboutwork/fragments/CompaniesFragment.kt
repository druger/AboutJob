package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.CompaniesPresenter
import com.druger.aboutwork.utils.ImagePreviewUtils
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.network_error.*
import kotlinx.android.synthetic.main.no_reviews.*
import kotlinx.android.synthetic.main.shimmer_content_companies.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class CompaniesFragment : BaseSupportFragment(), CompaniesView {

    @InjectPresenter
    lateinit var companiesPresenter: CompaniesPresenter

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var itemClickListener: OnItemClickListener<Review>

    private var inputMode: Int = 0

    @ProvidePresenter
    internal fun provideCompaniesPresenter() = CompaniesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
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
        fetchReviews()
    }

    private fun fetchReviews() {
        if (isInternetAvailable(requireContext())) companiesPresenter.fetchReviews()
        else showErrorScreen(true)
    }

    private fun setupUI() {
        mLtError = ltError
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
        actionBar?.setDisplayShowTitleEnabled(true)
        (activity as MainActivity).showSearchIcon()
        actionBar?.setTitle(R.string.search)
        (activity as MainActivity).getSearchView().apply {
            setOnSearchClickListener {
                replaceFragment(SearchFragment(), R.id.main_container, true)
                queryHint = resources.getString(R.string.query_hint)
            }
        }
    }

    private fun setupRecycler() {
        reviewAdapter = ReviewAdapter()
        rvLastReviews.adapter = reviewAdapter
        reviewAdapter.setOnClickListener(itemClickListener)
    }

    private fun setupListeners() {
        itemClickListener = object : OnItemClickListener<Review> {
            override fun onClick(item: Review, position: Int) {
                item.firebaseKey?.let { showSelectedReview(it) }
            }

            override fun onLongClick(item: Review, position: Int): Boolean {
                blurScreen()
                showDetailMarkCompany(item)
                return true
            }
        }
        btnRetry.setOnClickListener {
            showErrorScreen(false)
            fetchReviews()
        }
    }

    private fun blurScreen() {
        val content = requireActivity().findViewById<View>(android.R.id.content).rootView
        val blurImage = ImagePreviewUtils.getBlurredScreenDrawable(content)
        requireActivity().window.setBackgroundDrawable(blurImage)
    }

    private fun showDetailMarkCompany(review: Review) {
        parentFragmentManager.beginTransaction().apply {
            val prevFragment = parentFragmentManager.findFragmentByTag(DETAIL_MARK_DIALOG_TAG)
            if (prevFragment != null) remove(prevFragment)
            addToBackStack(null)
            val markCompany = review.markCompany
            markCompany?.let { mark ->
                DetailMarkCompanyDialog.newInstance(
                    mark.salary,
                    mark.chief,
                    mark.workplace,
                    mark.career,
                    mark.collective,
                    mark.socialPackage
                ).show(this, DETAIL_MARK_DIALOG_TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(inputMode)
    }

    override fun showReviews(reviews: List<Review>) {
        groupReviews.visibility = View.VISIBLE
        reviewAdapter.addReviews(reviews)
    }

    override fun showEmptyReviews() {
        groupReviews.visibility = View.GONE
        ltNoReviews.visibility = View.VISIBLE
        tvNoReviews.text = getString(R.string.no_recent_reviews)
    }

    override fun updateAdapter() {
        reviewAdapter.notifyDataSetChanged()
    }

    private fun showSelectedReview(id: String) {
        val fragment = SelectedReviewFragment.newInstance(id, false)
        replaceFragment(fragment, R.id.main_container, true, rvLastReviews, "detail_transform")
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            reviewPlaceholder.visibility = View.VISIBLE
            shimmerText.visibility = View.VISIBLE
            reviewPlaceholder.startShimmer()
        } else {
            reviewPlaceholder.stopShimmer()
            reviewPlaceholder.visibility = View.GONE
        }
    }

    companion object {
        private const val DETAIL_MARK_DIALOG_TAG = "mark_company"
    }
}