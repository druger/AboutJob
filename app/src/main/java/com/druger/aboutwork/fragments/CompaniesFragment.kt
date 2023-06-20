package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.databinding.FragmentCompaniesBinding
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.viewmodels.CompaniesViewModel
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompaniesFragment : BaseSupportFragment(){

    private val viewModel: CompaniesViewModel by viewModels()

    private var _binding: FragmentCompaniesBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var itemClickListener: OnItemClickListener<Review>

    private var inputMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        observeProgress()
        observeReviews()
        observeEmptyReviews()
        observeAdapter()
    }

    private fun observeAdapter() {
        viewModel.updateAdapter.observe(this) { updateAdapter() }
    }

    private fun observeProgress() {
        viewModel.progress.observe(this) { showProgress(it) }
    }

    private fun observeEmptyReviews() {
        viewModel.emptyReviews.observe(this) { showEmptyReviews() }
    }

    private fun observeReviews() {
        viewModel.reviewsState.observe(this) { showReviews(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompaniesBinding.inflate(inflater, container, false)
        setInputMode()
        (activity as MainActivity).showBottomNavigation()
        return binding.root
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
        if (isInternetAvailable(requireContext())) viewModel.fetchReviews()
        else showErrorScreen(true)
    }

    private fun setupUI() {
        mLtError = binding.ltError.root
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
        binding.mToolbar.apply {
            setActionBar(toolbar)
            actionBar?.setTitle(R.string.search)
            ivSearch.isVisible = true
            ivSearch.setOnClickListener {
                replaceFragment(SearchFragment(), R.id.main_container, true)
            }
        }
    }

    private fun setupRecycler() {
        reviewAdapter = ReviewAdapter()
        binding.rvLastReviews.adapter = reviewAdapter
        reviewAdapter.setOnClickListener(itemClickListener)
    }

    private fun setupListeners() {
        itemClickListener = object : OnItemClickListener<Review> {
            override fun onClick(item: Review, position: Int) {
                item.firebaseKey?.let { showSelectedReview(it) }
            }

            override fun onLongClick(item: Review, position: Int): Boolean {
                if (item.status != Review.INTERVIEW) {
                    showDetailMarkCompany(item)
                }
                return true
            }
        }
        binding.ltError.btnRetry.setOnClickListener {
            showErrorScreen(false)
            fetchReviews()
        }
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
        _binding = null
    }

    private fun showReviews(reviews: List<Review>) {
        binding.groupReviews.isVisible = true
        reviewAdapter.addReviews(reviews)
    }

    private fun showEmptyReviews() {
        binding.apply {
            groupReviews.isVisible = false
            ltNoReviews.root.isVisible = true
            ltNoReviews.tvNoReviews.text = getString(R.string.no_recent_reviews)
        }
    }

    private fun updateAdapter() {
        reviewAdapter.notifyDataSetChanged()
    }

    private fun showSelectedReview(id: String) {
        val fragment = SelectedReviewFragment.newInstance(id, false)
        replaceFragment(
            fragment,
            R.id.main_container,
            true,
            binding.rvLastReviews,
            "detail_transform"
        )
    }

    override fun showProgress(show: Boolean) {
        binding.apply {
            if (show) {
                reviewPlaceholder.isVisible = true
                binding.shimmerContent.shimmerText.isVisible = true
                reviewPlaceholder.startShimmer()
            } else {
                reviewPlaceholder.stopShimmer()
                reviewPlaceholder.isVisible = false
            }
        }
    }

    companion object {
        private const val DETAIL_MARK_DIALOG_TAG = "mark_company"
    }
}