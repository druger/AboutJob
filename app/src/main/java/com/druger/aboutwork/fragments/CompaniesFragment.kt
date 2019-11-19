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
import com.druger.aboutwork.adapters.CompanyRealmAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.realm.CompanyRealm
import com.druger.aboutwork.presenters.CompaniesPresenter
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener
import com.druger.aboutwork.utils.rx.RxSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.toolbar_searchview.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.concurrent.TimeUnit

class CompaniesFragment : BaseSupportFragment(), CompaniesView {

    @InjectPresenter
    lateinit var companiesPresenter: CompaniesPresenter

    private lateinit var adapter: CompanyAdapter
    private lateinit var realmAdapter: CompanyRealmAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var itemClickListener: OnItemClickListener<CompanyRealm>

    private var query: String? = null

    private var fragment: Fragment? = null
    private var inputMode: Int = 0

    private val companiesFromDb: RealmResults<CompanyRealm>
        get() = companiesPresenter.companiesFromDb

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
        setupRecycler()
        setupListeners()
        setupRecyclerRealm()
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
        setActionBar(mToolbar)
        actionBar.setTitle(R.string.search)
    }

    private fun setupRecycler() {
        adapter = CompanyAdapter()
        rvCompanies?.adapter = adapter
    }

    private fun setupRecyclerRealm() {
        realmAdapter = CompanyRealmAdapter(companiesFromDb, itemClickListener)
        rvCompaniesRealm.adapter = realmAdapter
    }

    private fun setupListeners() {
        scrollListener = object : EndlessRecyclerViewScrollListener(
            rvCompanies.layoutManager as LinearLayoutManager?) {
            override fun onLoadMore(currentPage: Int) {
                companiesPresenter.getCompanies(query, currentPage)
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

        itemClickListener = object : OnItemClickListener<CompanyRealm> {
            override fun onClick(company: CompanyRealm, position: Int) {
                showCompanyDetail(company.id)
            }

            override fun onLongClick(position: Int): Boolean {
                return false
            }
        }
    }

    private fun setupSearch() {
        searchView.queryHint = resources.getString(R.string.query_hint)

        RxSearch.fromSearchView(searchView)
            .debounce(DEBOUNCE_SEARCH.toLong(), TimeUnit.MILLISECONDS)
            .filter { item -> item.length >= 2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newText ->
                query = newText
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
        companiesPresenter.removeRealmListener()
    }

    override fun showCompanies(companies: List<Company>, pages: Int) {
        adapter.addItems(companies)
        scrollListener.setLoaded()
        scrollListener.setPages(pages)
        rvCompanies.visibility = View.VISIBLE
        rvCompaniesRealm.visibility = View.INVISIBLE
        tvWatched.visibility = View.GONE
        ivEmptySearch.visibility = View.INVISIBLE
        tvEmptySearch.visibility = View.INVISIBLE
    }

    override fun showWatchedRecently() {
        tvWatched.visibility = View.VISIBLE
    }

    override fun showCompaniesRealm() {
        rvCompaniesRealm.visibility = View.VISIBLE
        ivEmptySearch.visibility = View.INVISIBLE
        tvEmptySearch.visibility = View.INVISIBLE
    }

    private fun saveCompanyToDb(company: CompanyRealm) {
        companiesPresenter.saveCompanyToDb(company)
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
        replaceFragment(fragment, R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            ivEmptySearch.visibility = View.INVISIBLE
            tvEmptySearch.visibility = View.INVISIBLE
            rvCompanies.visibility = View.INVISIBLE
            rvCompaniesRealm.visibility = View.INVISIBLE
            tvWatched.visibility = View.GONE
        } else {
            rvCompanies.visibility = View.VISIBLE
        }
    }
}