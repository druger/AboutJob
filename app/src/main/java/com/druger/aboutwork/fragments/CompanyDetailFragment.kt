package com.druger.aboutwork.fragments


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.databinding.FragmentCompanyDetailBinding
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.enums.Screen
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.viewmodels.CompanyDetailViewModel
import com.thefinestartist.finestwebview.FinestWebView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyDetailFragment : BaseSupportFragment(), FilterDialogFragment.OnFilterListener {

    private val viewModel: CompanyDetailViewModel by viewModels()

    private var descriptionShow: Boolean = false

    private lateinit var reviewAdapter: ReviewAdapter

    private var companyDetail: CompanyDetail? = null
    private var companyId: String? = null

    private var _binding: FragmentCompanyDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeProgress()
        observeError()
        observeAdapter()
        observeReview()
        observeEmptyReview()
        observeEmptyFilteredReview()
        observeFilterIcon()
        observeAuth()
        observeAddReview()
        observeCompanyDetail()
        observeFilter()
    }

    private fun observeFilter() {
        viewModel.filterState.observe(this) {
            showFilterDialog(it.position, it.city)
        }
    }

    private fun observeCompanyDetail() {
        viewModel.companyDetailState.observe(this) {
            showCompanyDetail(it)
        }
    }

    private fun observeAddReview() {
        viewModel.addReviewState.observe(this) {
            addReview()
        }
    }

    private fun observeAuth() {
        viewModel.authState.observe(this) {
            showAuth()
        }
    }

    private fun observeFilterIcon() {
        viewModel.filterIconState.observe(this) {
            setFilterIcon(it)
        }
    }

    private fun observeEmptyFilteredReview() {
        viewModel.emptyFilteredReviewsState.observe(this) {
            showEmptyReviews(true)
        }
    }

    private fun observeEmptyReview() {
        viewModel.emptyReviewsState.observe(this) {
            showEmptyReviews()
        }
    }

    private fun observeReview() {
        viewModel.reviewState.observe(this) {
            showReviews(it)
        }
    }

    private fun observeAdapter() {
        viewModel.adapterState.observe(this) { updateAdapter() }
    }

    private fun observeError() {
        viewModel.errorState.observe(this) { showErrorScreen(it) }
    }

    private fun observeProgress() {
        viewModel.progressState.observe(this) { showProgress(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        getData(savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        setupUX()
        setupRecycler()
    }

    private fun setupUI() {
        mLtError = binding.ltError.root
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        companyId = bundle?.getString(COMPANY_ID)
        companyId?.let { viewModel.getCompanyDetail(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(COMPANY_ID, companyId)
    }

    private fun setupUX() {
        binding.fabAddReview.setOnClickListener { viewModel.checkAuthUser() }
        binding.ltError.btnRetry.setOnClickListener { companyId?.let { viewModel.getCompanyDetail(it) } }
        binding.contentDetail.apply {
            tvShowDescription.setOnClickListener { showDescription() }
            ivFilter.setOnClickListener { viewModel.filterClick() }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setActionBar(toolbar)
            actionBar?.setDisplayHomeAsUpEnabled(true)
            ivSearch.isVisible = false
            actionBar?.setDisplayShowTitleEnabled(true)
        }
    }

    private fun setupRecycler() {
        reviewAdapter = ReviewAdapter()
        binding.contentDetail.apply {
            rvReviews.itemAnimator = DefaultItemAnimator()
            rvReviews.adapter = reviewAdapter
        }

        reviewAdapter.setOnClickListener(object : OnItemClickListener<Review> {
            override fun onClick(item: Review, position: Int) {
                item.firebaseKey?.let {
                    val reviewFragment = SelectedReviewFragment.newInstance(it)
                    replaceFragment(reviewFragment, R.id.main_container, true)
                }
            }

            override fun onLongClick(item: Review, position: Int): Boolean {
                return false
            }
        })
    }

    private fun showDescription() {
        binding.contentDetail.apply {
            if (descriptionShow) {
                descriptionShow = false
                tvShowDescription.setText(R.string.show_all)
                tvDescription.maxLines = 4
            } else {
                descriptionShow = true
                tvShowDescription.setText(R.string.hide)
                tvDescription.maxLines = Integer.MAX_VALUE
            }
        }
    }

    private fun showWebView(site: String) {
        FinestWebView.Builder(requireActivity())
            .setCustomAnimations(
                R.anim.activity_open_enter,
                R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit
            )
            .webViewSupportZoom(true)
            .webViewBuiltInZoomControls(true)
            .theme(R.style.WebViewRedTheme)
            .swipeRefreshColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            .show(site)
    }

    private fun addReview() {
        companyDetail?.id?.let { id ->
            companyDetail?.name?.let { name ->
                val review = AddReviewFragment.newInstance(id, name)
                replaceFragment(
                    review,
                    R.id.main_container,
                    true,
                    binding.fabAddReview,
                    "fab_transform"
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reviewAdapter.setOnClickListener(null)
        viewModel.removeAuthListener()
        _binding = null
    }

    private fun updateAdapter() {
        reviewAdapter.notifyDataSetChanged()
    }

    private fun showReviews(reviews: List<Review>) {
        reviewAdapter.addReviews(reviews)
        binding.contentDetail.apply {
            groupReviews.isVisible = true
            ltNoReviews.root.isVisible = false
            groupFilter.isVisible = true
            filterEmpty.root.isVisible = false
            tvCountReviews.text =
                resources.getQuantityString(R.plurals.reviews, reviews.size, reviews.size)
        }
    }

    private fun showEmptyReviews(isFilter: Boolean = false) {
        binding.contentDetail.apply {
            if (isFilter) {
                filterEmpty.root.isVisible = true
            } else {
                ltNoReviews.root.isVisible = true
                groupFilter.isVisible = false
            }
            groupReviews.isVisible = false
        }
    }

    private fun showCompanyDetail(company: CompanyDetail) {
        companyDetail = company
        company.id?.let { viewModel.getReviews(it) }
        setSite()
        binding.contentDetail.tvCity.text = companyDetail?.area?.name
        setCompanyName(companyDetail?.name)
        companyDetail?.let { loadImage(it) }
        setDescription()
    }

    private fun setDescription() {
        binding.contentDetail.apply {
            val description = companyDetail?.description
            if (!description.isNullOrEmpty()) {
                tvDescription.text = description
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    tvDescription.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                else
                    tvDescription.text = Html.fromHtml(description)
            } else {
                tvDescription.setText(R.string.no_description)
                tvShowDescription.visibility = View.GONE
            }
        }
    }

    private fun setSite() {
        binding.contentDetail.apply {
            val site = companyDetail?.site
            if (site == "http://" || site == "https://") {
                tvSite.visibility = View.GONE
            } else {
                site?.let { s ->
                    tvSite.setOnClickListener { showWebView(s) }
                    tvSite.text = s
                }
            }
        }
    }

    private fun loadImage(company: CompanyDetail) {
        val logo = company.logo
        Glide.with(requireContext())
            .load(logo?.original ?: "")
            .placeholder(R.drawable.ic_default_company)
            .error(R.drawable.ic_default_company)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.contentDetail.ivLogo)
    }

    private fun setCompanyName(name: String?) {
        binding.contentDetail.tvCompanyName.text = name
        actionBar?.title = name
    }

    override fun showProgress(show: Boolean) {
        binding.contentDetail.apply {
            if (show) {
                reviewPlaceholder.visibility = View.VISIBLE
                reviewPlaceholder.startShimmer()
            } else {
                reviewPlaceholder.stopShimmer()
                reviewPlaceholder.visibility = View.GONE
            }
        }
    }

    override fun showErrorScreen(show: Boolean) {
        super.showErrorScreen(show)
        if (show) {
            binding.ltContent.isInvisible = true
        } else {
            binding.ltContent.isVisible = true
        }
    }

    private fun showAuth() {
        replaceFragment(
            AuthFragment.newInstance(
                getString(R.string.company_login),
                Screen.COMPANY_DETAIL.name,
                companyId
            ),
            R.id.content_company
        )
    }

    private fun showFilterDialog(position: String, city: String) {
        val filter = FilterDialogFragment.newInstance(position, city)
        filter.show(childFragmentManager, null)
    }

    override fun onFilter(filterType: FilterType, position: String, city: String) {
        viewModel.filterReviews(filterType, position, city)
    }

    private fun setFilterIcon(icFilter: Int) {
        binding.contentDetail.ivFilter.setImageResource(icFilter)
    }

    companion object {
        private const val COMPANY_ID = "companyID"

        fun newInstance(companyID: String): CompanyDetailFragment {
            val companyDetail = CompanyDetailFragment()
            val bundle = Bundle()
            bundle.putString(COMPANY_ID, companyID)
            companyDetail.arguments = bundle
            return companyDetail
        }
    }
}
