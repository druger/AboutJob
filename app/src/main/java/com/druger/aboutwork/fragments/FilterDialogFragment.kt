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
import androidx.fragment.app.viewModels
import com.druger.aboutwork.R
import com.druger.aboutwork.databinding.FragmentFilterReviewBinding
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.viewmodels.FilterViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpBottomSheetDialogFragment

@AndroidEntryPoint
class FilterDialogFragment : MvpBottomSheetDialogFragment(),
    AdapterView.OnItemSelectedListener {

    private val viewModel: FilterViewModel by viewModels()

    private var _binding: FragmentFilterReviewBinding? = null
    private val binding get() = _binding!!

    private var filterListener: OnFilterListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        filterListener = parentFragment as? OnFilterListener
        observeCities()
        observePositions()
        observeFilter()
        observeClearClick()
    }

    private fun observeClearClick() {
        viewModel.clearState.observe(this) {
            clearClick()
        }
    }

    private fun observeFilter() {
        viewModel.filterState.observe(this) {
            applyFilter(it)
        }
    }

    private fun observePositions() {
        viewModel.positionsState.observe(this) {
            showPositions(it)
        }
    }

    private fun observeCities() {
        viewModel.citiesState.observe(this) {
            showCities(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFilterReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            showBottomSheetAboveKeyboard()
            setupSpinnerFilter()
            getPositions()
            getCities()
            btnApply.setOnClickListener {
                val position = etPosition.text.toString().trim()
                val city = etCity.text.toString().trim()
                viewModel.applyFilter(position, city)
            }
            tvClear.setOnClickListener { viewModel.clearFilter() }
            setupFilters()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupFilters() {
        binding.etPosition.setText(arguments?.getString(POSITION_KEY))
        binding.etCity.setText(arguments?.getString(CITY_KEY))
    }

    private fun showBottomSheetAboveKeyboard() {
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }
    }

    private fun getPositions() {
        binding.etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.getPositions(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getCities() {
        binding.etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.getCities(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun setupSpinnerFilter() {
        with(binding) {
            spinnerFilter.onItemSelectedListener = this@FilterDialogFragment
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_reviews,
                R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spinnerFilter.adapter = adapter
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> viewModel.setFilterType(FilterType.RATING)
            1 -> viewModel.setFilterType(FilterType.SALARY)
            2 -> viewModel.setFilterType(FilterType.CHIEF)
            3 -> viewModel.setFilterType(FilterType.WORKPLACE)
            4 -> viewModel.setFilterType(FilterType.CAREER)
            5 -> viewModel.setFilterType(FilterType.COLLECTIVE)
            6 -> viewModel.setFilterType(FilterType.BENEFITS)
            7 -> viewModel.setFilterType(FilterType.POPULARITY)
        }
    }

    private fun showPositions(positions: List<Vacancy>) {
        Utils.showSuggestions(requireContext(), positions, binding.etPosition)
    }

    private fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, binding.etCity)
    }

    private fun applyFilter(filter: FilterViewModel.Filter) {
        dismiss()
        filterListener?.onFilter(filter.filterType, filter.position, filter.city)
    }

    private fun clearClick() {
        binding.etPosition.setText("")
        binding.etCity.setText("")
    }

    interface OnFilterListener {
        fun onFilter(filterType: FilterType, position: String, city: String)
    }

    companion object {
        private const val POSITION_KEY = "position"
        private const val CITY_KEY = "city"

        fun newInstance(position: String, city: String): FilterDialogFragment {
            return FilterDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(POSITION_KEY, position)
                    putString(CITY_KEY, city)
                }
            }
        }
    }
}