package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.DEBOUNCE_SEARCH
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.CompanyAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.SearchView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.realm.CompanyRealm
import com.druger.aboutwork.presenters.SearchPresenter
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener
import com.druger.aboutwork.utils.rx.RxSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_search.*
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

    private var fragment: Fragment? = null
    private var inputMode: Int = 0

    @ProvidePresenter
    internal fun provideSearchPresenter(): SearchPresenter {
        return App.appComponent.searchPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search, container, false)
        setInputMode()
        (activity as MainActivity).showBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupToolbar()
        setupRecycler()
        setupListeners()
        setupSearch()
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
    }

    private fun setupRecycler() {
        adapter = CompanyAdapter()
        rvCompanies?.adapter = adapter
    }

    private fun setupListeners() {
        scrollListener = object : EndlessRecyclerViewScrollListener(
            rvCompanies.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int) {
                if (page > 0) adapter.addLoading()
                query?.let { presenter.getCompanies(it, page, !cbMoreCompanies.isChecked) }
            }
        }
        rvCompanies.addOnScrollListener(scrollListener)

        adapter.setOnItemClickListener(object : OnItemClickListener<Company> {
            override fun onClick(company: Company, position: Int) {
                saveCompanyToDb(setupCompanyRealm(company))
                showCompanyDetail(company.id)
            }

            override fun onLongClick(position: Int): Boolean {
                return false
            }
        })
    }

    private fun setupSearch() {
        searchView.queryHint = resources.getString(R.string.query_hint)
        searchView.isIconified = false

        RxSearch.fromSearchView(searchView)
            .debounce(DEBOUNCE_SEARCH.toLong(), TimeUnit.MILLISECONDS)
            .filter { item -> item.length >= 2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newText ->
                cbMoreCompanies.visibility = View.VISIBLE
                query = newText.trim()
                adapter.clear()
                scrollListener.resetPageCount()
                showProgress(true)
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
        adapter.addItems(companies)
        adapter.removeLoading()
        scrollListener.setLoaded()
        scrollListener.setPages(pages)
        rvCompanies.visibility = View.VISIBLE
        if (companies.isNotEmpty()) {
            cbMoreCompanies.visibility = View.VISIBLE
        }
    }

    private fun saveCompanyToDb(company: CompanyRealm) {
        presenter.saveCompanyToDb(company)
    }

    private fun setupCompanyRealm(company: Company): CompanyRealm {
        val id = company.id
        val name = company.name
        val logo = company.logo
        val sLogo = logo?.logo90 ?: ""

        val companyRealm = CompanyRealm(id, name, sLogo)
        companyRealm.city = company.city
        return companyRealm
    }

    private fun showCompanyDetail(id: String) {
        fragment = CompanyDetailFragment.newInstance(id)
        replaceFragment(fragment as CompanyDetailFragment, R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            rvCompanies.visibility = View.INVISIBLE
        } else {
            adapter.removeLoading()
            rvCompanies.visibility = View.VISIBLE
        }
    }
}