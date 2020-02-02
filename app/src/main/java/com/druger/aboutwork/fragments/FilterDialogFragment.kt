package com.druger.aboutwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.FilterView
import com.druger.aboutwork.presenters.FilterPresenter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_filter_review.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class FilterDialogFragment : BottomSheetDialogFragment(), FilterView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: FilterPresenter

    @ProvidePresenter
    internal fun provideFilterPresenter(): FilterPresenter {
        return App.appComponent.filterPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_filter_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinnerFilter()
    }

    private fun setupSpinnerFilter() {
        spinnerFilter.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_reviews,
            R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerFilter.adapter = adapter
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position) {
//            0 -> presenter.filterAtRating()
//            1 -> presenter.filterAtSalary()
//            2 -> presenter.filterAtChief()
//            3 -> presenter.filterAtWorkplace()
//            4 -> presenter.filterAtCareer()
//            5 -> presenter.filterAtCollective()
//            6 -> presenter.filterAtBenefits()
//            7 -> presenter.filterAtPopularity()
        }
    }
}