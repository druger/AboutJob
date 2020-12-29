package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.druger.aboutwork.Const.Bundles.DEBOUNCE_SEARCH
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.CompanyAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.SearchView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.presenters.SearchPresenter
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener
import com.druger.aboutwork.utils.rx.RxSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.network_error.*
import kotlinx.android.synthetic.main.toolbar_search.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.concurrent.TimeUnit

class SearchFragment : BaseSupportFragment(), SearchView {

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    private lateinit var adapter: CompanyAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    private var query: String? = null
    private var page = 0

    private var inputMode: Int = 0

    @ProvidePresenter
    internal fun provideSearchPresenter() = SearchPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search, container, false)
        setInputMode()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setActionBar(toolbar)
        setupRecycler()
        setupListeners()
        setupSearch()
    }

    private fun setupUI() {
        mProgressBar = progressBar
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

    private fun setupRecycler() {
        adapter = CompanyAdapter()
        rvCompanies?.adapter = adapter
    }

    private fun setupListeners() {
        scrollListener = object : EndlessRecyclerViewScrollListener(
            rvCompanies.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int) {
                this@SearchFragment.page = page
                if (page > 0) adapter.addLoading()
                getCompanies(page)
            }
        }
        rvCompanies.addOnScrollListener(scrollListener)

        adapter.setOnItemClickListener(object : OnItemClickListener<Company> {
            override fun onClick(item: Company, position: Int) {
                showCompanyDetail(item.id)
            }

            override fun onLongClick(item: Company, position: Int): Boolean {
                return false
            }
        })
        btnRetry.setOnClickListener { getCompanies(page) }
    }

    private fun getCompanies(page: Int) {
        query?.let { presenter.getCompanies(it, page, !cbMoreCompanies.isChecked) }
    }

    private fun setupSearch() {
        searchView.apply {
            queryHint = resources.getString(R.string.query_hint)
            isIconified = false
            setOnCloseListener(object : androidx.appcompat.widget.SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    if (searchView.query.isBlank()) {
                        parentFragmentManager.popBackStackImmediate()
                        return false
                    }
                    return true
                }
            })
            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    Utils.showKeyboard(v.context)
                    (activity as MainActivity).hideBottomNavigation()
                } else {
                    Utils.hideKeyboard(v.context, v)
                    (activity as MainActivity).showBottomNavigation()
                }
            }
        }

        RxSearch.fromSearchView(searchView)
            .debounce(DEBOUNCE_SEARCH.toLong(), TimeUnit.MILLISECONDS)
            .filter { item -> item.length >= 2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newText ->
                query = newText.trim()
                adapter.clear()
                scrollListener.resetPageCount()
            }

        cbMoreCompanies.setOnCheckedChangeListener { _, _ ->
            adapter.clear()
            scrollListener.resetPageCount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(inputMode)
        searchView.setOnQueryTextListener(null)
        rvCompanies.removeOnScrollListener(scrollListener)
        adapter.setOnItemClickListener(null)
    }

    override fun showCompanies(companies: List<Company>, pages: Int) {
        cbMoreCompanies.visibility = View.VISIBLE
        adapter.removeLoading()
        scrollListener.setLoaded()
        scrollListener.setPages(pages)
        if (companies.isNotEmpty()) {
            rvCompanies.visibility = View.VISIBLE
            adapter.addItems(companies)
        }
    }

    private fun showCompanyDetail(id: String) {
        Utils.hideKeyboard(requireContext(), searchView)
        replaceFragment(CompanyDetailFragment.newInstance(id), R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            rvCompanies.visibility = View.INVISIBLE
        } else {
            adapter.removeLoading()
            rvCompanies.visibility = View.VISIBLE
        }
    }
}