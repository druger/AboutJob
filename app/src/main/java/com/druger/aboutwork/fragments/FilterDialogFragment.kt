package com.druger.aboutwork.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.FilterView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.presenters.FilterPresenter
import com.druger.aboutwork.utils.Utils
import kotlinx.android.synthetic.main.fragment_filter_review.*
import moxy.MvpBottomSheetDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class FilterDialogFragment : MvpBottomSheetDialogFragment(), FilterView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: FilterPresenter

    @ProvidePresenter
    fun provideFilterPresenter(): FilterPresenter {
        return App.appComponent.filterPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_filter_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinnerFilter()
        getPositions()
        getCities()
    }

    private fun getPositions() {
        etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getPositions(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getCities() {
        etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getCities(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

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
        when (position) {
            0 -> presenter.filterAtRating()
            1 -> presenter.filterAtSalary()
            2 -> presenter.filterAtChief()
            3 -> presenter.filterAtWorkplace()
            4 -> presenter.filterAtCareer()
            5 -> presenter.filterAtCollective()
            6 -> presenter.filterAtBenefits()
            7 -> presenter.filterAtPopularity()
        }
    }

    override fun showPositions(positions: List<Vacancy>) {
        Utils.showSuggestions(requireContext(), positions, etPosition)
    }

    override fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, etCity)
    }
}