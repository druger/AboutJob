package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.druger.aboutwork.Const.Bundles.DEBOUNCE_SEARCH
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.CompanyAdapter
import com.druger.aboutwork.databinding.FragmentSearchBinding
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener
import com.druger.aboutwork.utils.rx.RxSearch
import com.druger.aboutwork.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SearchFragment : BaseSupportFragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CompanyAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    private var query: String? = null
    private var page = 0

    private var inputMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeProgress()
        observeError()
        observeCompanies()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setInputMode()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setActionBar(binding.tbSearch.toolbar)
        setupRecycler()
        setupListeners()
        setupSearch()
    }

    private fun observeProgress() {
        viewModel.progressState.observe(this) { showProgress(it) }
    }

    private fun observeError() {
        viewModel.errorState.observe(this) { showErrorScreen(it) }
    }

    private fun observeCompanies() {
        viewModel.companiesState.observe(this) { showCompanies(it.companies, it.pages) }
    }

    private fun setupUI() {
        mProgressBar = binding.progressBar
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

    private fun setupRecycler() {
        adapter = CompanyAdapter()
        binding.rvCompanies.adapter = adapter
    }

    private fun setupListeners() {
        scrollListener = object : EndlessRecyclerViewScrollListener(
            binding.rvCompanies.layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(page: Int) {
                this@SearchFragment.page = page
                if (page > 0) adapter.addLoading()
                getCompanies(page)
            }
        }
        binding.rvCompanies.addOnScrollListener(scrollListener)

        adapter.setOnItemClickListener(object : OnItemClickListener<Company> {
            override fun onClick(item: Company, position: Int) {
                showCompanyDetail(item.id)
            }

            override fun onLongClick(item: Company, position: Int): Boolean {
                return false
            }
        })
        binding.ltError.btnRetry.setOnClickListener { getCompanies(page) }
    }

    private fun getCompanies(page: Int) {
        query?.let { viewModel.getCompanies(it, page, !binding.cbMoreCompanies.isChecked) }
    }

    private fun setupSearch() {
        binding.tbSearch.searchView.apply {
            queryHint = resources.getString(R.string.query_hint)
            isIconified = false
            setOnCloseListener(object : androidx.appcompat.widget.SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    if (binding.tbSearch.searchView.query.isBlank()) {
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

        RxSearch.fromSearchView(binding.tbSearch.searchView)
            .debounce(DEBOUNCE_SEARCH.toLong(), TimeUnit.MILLISECONDS)
            .filter { item -> item.length >= 2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newText ->
                query = newText.trim()
                adapter.clear()
                scrollListener.resetPageCount()
            }

        binding.cbMoreCompanies.setOnCheckedChangeListener { _, _ ->
            adapter.clear()
            scrollListener.resetPageCount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(inputMode)
        binding.tbSearch.searchView.setOnQueryTextListener(null)
        binding.rvCompanies.removeOnScrollListener(scrollListener)
        adapter.setOnItemClickListener(null)
        _binding = null
    }

    private fun showCompanies(companies: List<Company>, pages: Int) {
        binding.cbMoreCompanies.isVisible = true
        adapter.removeLoading()
        scrollListener.setLoaded()
        scrollListener.setPages(pages)
        if (companies.isNotEmpty()) {
            binding.rvCompanies.isVisible = true
            adapter.addItems(companies)
        }
    }

    private fun showCompanyDetail(id: String) {
        Utils.hideKeyboard(requireContext(), binding.tbSearch.searchView)
        binding.tbSearch.searchView.setOnQueryTextFocusChangeListener(null)
        replaceFragment(CompanyDetailFragment.newInstance(id), R.id.main_container, true)
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            binding.rvCompanies.isInvisible = true
        } else {
            adapter.removeLoading()
            binding.rvCompanies.isVisible = true
        }
    }
}