package com.druger.aboutwork.fragments


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.enums.Screen
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.CompanyDetailView
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.CompanyDetailPresenter
import com.thefinestartist.finestwebview.FinestWebView
import kotlinx.android.synthetic.main.content_company_detail.*
import kotlinx.android.synthetic.main.fragment_company_detail.*
import kotlinx.android.synthetic.main.network_error.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class CompanyDetailFragment : BaseSupportFragment(), CompanyDetailView,
    FilterDialogFragment.OnFilterListener {

    @InjectPresenter
    lateinit var presenter: CompanyDetailPresenter

    private var descriptionShow: Boolean = false

    private lateinit var reviewAdapter: ReviewAdapter

    private var companyDetail: CompanyDetail? = null
    private var companyId: String? = null

    @ProvidePresenter
    internal fun provideCompanyDetailPresenter() = CompanyDetailPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_company_detail, container, false)

        getData(savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        setupUX()
        setupRecycler()
    }

    private fun setupUI() {
        mLtError = ltError
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        companyId = bundle?.getString(COMPANY_ID)
        companyId?.let { presenter.getCompanyDetail(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(COMPANY_ID, companyId)
    }

    private fun setupUX() {
        fabAddReview.setOnClickListener { presenter.checkAuthUser() }
        btnRetry.setOnClickListener { companyId?.let { presenter.getCompanyDetail(it) } }
        tvShowDescription.setOnClickListener { showDescription() }
        ivFilter.setOnClickListener { presenter.filterClick() }
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        mToolbar?.let { setActionBar(it) }
        actionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).hideSearchIcon()
        actionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupRecycler() {
        reviewAdapter = ReviewAdapter()
        rvReviews.itemAnimator = DefaultItemAnimator()
        rvReviews.adapter = reviewAdapter

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

    private fun showWebView(site: String) {
        FinestWebView.Builder(requireActivity())
            .setCustomAnimations(R.anim.activity_open_enter,
                R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
            .webViewSupportZoom(true)
            .webViewBuiltInZoomControls(true)
            .theme(R.style.WebViewRedTheme)
            .swipeRefreshColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            .show(site)
    }

    override fun addReview() {
        companyDetail?.id?.let { id ->
            companyDetail?.name?.let { name ->
                val review = AddReviewFragment.newInstance(id, name)
                replaceFragment(review, R.id.main_container, true, fabAddReview, "fab_transform")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.removeListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reviewAdapter.setOnClickListener(null)
        presenter.removeAuthListener()
    }

    override fun updateAdapter() {
        reviewAdapter.notifyDataSetChanged()
    }

    override fun showReviews(reviews: List<Review>, isFilter: Boolean) {
        reviewAdapter.addReviews(reviews)
        groupReviews.visibility = View.VISIBLE
        ltNoReviews.visibility = View.GONE
        groupFilter.visibility = View.VISIBLE
        filterEmpty.visibility = View.GONE
        tvCountReviews.text = resources.getQuantityString(R.plurals.reviews, reviews.size, reviews.size)
    }

    override fun showEmptyReviews(isFilter: Boolean) {
        if (isFilter) {
            filterEmpty.visibility = View.VISIBLE
        } else {
            ltNoReviews.visibility = View.VISIBLE
            groupFilter.visibility = View.GONE
        }
        groupReviews.visibility = View.GONE
    }

    override fun showCompanyDetail(company: CompanyDetail) {
        companyDetail = company
        company.id?.let { presenter.getReviews(it) }
        setSite()
        tvCity.text = companyDetail?.area?.name
        setCompanyName(companyDetail?.name)
        companyDetail?.let { loadImage(it) }
        setDescription()
    }

    private fun setDescription() {
        val description = companyDetail?.description
        if (description != null && description.isNotEmpty()) {
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

    private fun setSite() {
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

    private fun loadImage(company: CompanyDetail) {
        val logo = company.logo
        Glide.with(requireContext())
            .load(logo?.original ?: "")
            .placeholder(R.drawable.ic_default_company)
            .error(R.drawable.ic_default_company)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(ivLogo)
    }

    private fun setCompanyName(name: String?) {
        tvCompanyName.text = name
        actionBar?.title = name
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            reviewPlaceholder.visibility = View.VISIBLE
            reviewPlaceholder.startShimmer()
        } else {
            reviewPlaceholder.stopShimmer()
            reviewPlaceholder.visibility = View.GONE
        }
    }

    override fun showErrorScreen(show: Boolean) {
        super.showErrorScreen(show)
        if (show) {
            ltContent.visibility = View.INVISIBLE
        } else {
            ltContent.visibility = View.VISIBLE
        }
    }

    override fun showAuth() {
        replaceFragment(
            AuthFragment.newInstance(
                getString(R.string.company_login),
                Screen.COMPANY_DETAIL.name,
                companyId),
            R.id.content_company)
    }

    override fun showFilterDialog(position: String, city: String) {
        val filter = FilterDialogFragment.newInstance(position, city)
        filter.show(childFragmentManager, null)
    }

    override fun onFilter(filterType: FilterType, position: String, city: String) {
        presenter.filterReviews(filterType, position, city)
    }

    override fun setFilterIcon(icFilter: Int) {
        ivFilter.setImageResource(icFilter)
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
