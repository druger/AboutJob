package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.CompanyRealmAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.realm.CompanyRealm
import com.druger.aboutwork.presenters.CompaniesPresenter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class CompaniesFragment : BaseSupportFragment(), CompaniesView {

    @InjectPresenter
    lateinit var companiesPresenter: CompaniesPresenter

    private lateinit var realmAdapter: CompanyRealmAdapter
    private lateinit var itemClickListener: OnItemClickListener<CompanyRealm>

    private var fragment: Fragment? = null
    private var inputMode: Int = 0

    private val companiesFromDb: RealmResults<CompanyRealm>
        get() = companiesPresenter.getCompaniesFromDb()

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
        setupRecyclerRealm()
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

    private fun setupRecyclerRealm() {
        realmAdapter = CompanyRealmAdapter(companiesFromDb, itemClickListener)
        rvCompaniesRealm.adapter = realmAdapter
    }

    private fun setupListeners() {
        itemClickListener = object : OnItemClickListener<CompanyRealm> {
            override fun onClick(company: CompanyRealm, position: Int) {
                showCompanyDetail(company.id)
            }

            override fun onLongClick(position: Int): Boolean {
                return false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(inputMode)
        companiesPresenter.removeRealmListener()
    }

    override fun showWatchedRecently() {
        tvWatched.visibility = View.VISIBLE
    }

    override fun showCompaniesRealm() {
        rvCompaniesRealm.visibility = View.VISIBLE
        ivEmptySearch.visibility = View.INVISIBLE
        tvEmptySearch.visibility = View.INVISIBLE
    }

    private fun showCompanyDetail(id: String) {
        fragment = CompanyDetailFragment.newInstance(id)
        replaceFragment(fragment as CompanyDetailFragment, R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            ivEmptySearch.visibility = View.INVISIBLE
            tvEmptySearch.visibility = View.INVISIBLE
            rvCompaniesRealm.visibility = View.INVISIBLE
            tvWatched.visibility = View.GONE
        } else {
            rvCompaniesRealm.visibility = View.VISIBLE
        }
    }
}