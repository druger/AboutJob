package com.druger.aboutwork.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.databinding.FragmentReviewBinding
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.utils.UploadPhotoHelper
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.viewmodels.AddReviewVieModel
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReviewFragment : ReviewFragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: AddReviewVieModel by viewModels()

    private lateinit var datePicker: DatePickerFragment

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMotion()
        observeSuccessState()
        observeErrorState()
        observeVacancies()
        observeCities()
        observeWorkingDate()
        observeWorkedDate()
        observeInterviewDate()
    }

    private fun observeInterviewDate() {
        viewModel.interviewDate.observe(this) {
            showInterviewDate()
        }
    }

    private fun observeWorkedDate() {
        viewModel.workedDate.observe(this) {
            showWorkedDate()
            setIsIndicatorRatingBar(false)
        }
    }

    private fun observeWorkingDate() {
        viewModel.workingDate.observe(this) {
            showWorkingDate()
            setIsIndicatorRatingBar(false)
        }
    }

    private fun observeCities() {
        viewModel.cities.observe(this) {
            showCities(it)
        }
    }

    private fun observeVacancies() {
        viewModel.vacancies.observe(this) {
            showVacancies(it)
        }
    }

    private fun observeErrorState() {
        viewModel.errorState.observe(this) {
            showErrorAdding()
        }
    }

    private fun observeSuccessState() {
        viewModel.successState.observe(this) {
            successfulAddition()
        }
    }

    private fun setupMotion() {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            startContainerColor = ContextCompat.getColor(requireContext(), R.color.colorSurface)
            endContainerColor = ContextCompat.getColor(requireContext(), R.color.colorSurface)
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        getData(savedInstanceState)
        datePicker = DatePickerFragment()
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDateVisibility()
        setupWorkStatus()
        setupCompanyRating()
        setupListeners()
        setupRecycler()
    }

    private fun setupRecycler() {
        binding.contentDetail.rvPhotos.apply {
            adapter = uriPhotoAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onDestroy() {
        _binding?.let { unbindDrawables(binding.root) }
        _binding = null
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(COMPANY_ID, viewModel.companyId)
        outState.putString(COMPANY_NAME, viewModel.companyName)
    }

    private fun unbindDrawables(view: View) {
        if (view.background != null) {
            view.background.callback = null
        }
        if (view is ViewGroup && view !is AdapterView<*>) {
            for (i in 0 until view.childCount) {
                unbindDrawables(view.getChildAt(i))
            }
            view.removeAllViews()
        }
    }

    private fun setupListeners() {
        with(binding.contentDetail) {
            etEmploymentDate.setOnClickListener { employmentDateClick() }
            etDismissalDate.setOnClickListener { dismissalDateClick() }
            cityChanges()
            positionChanges()
            spinnerStatus.onItemSelectedListener = this@AddReviewFragment
            setupRatingChanges()
            radioGroupRecommendedListener()
            ivAddPhoto.setOnClickListener {
                viewModel.sendAnalytics()
                checkPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == RC_PICK_IMAGE) {
            UploadPhotoHelper.getUriImages(data) { uri ->
                showPhotos(uri)
            }
        }
    }

    private fun radioGroupRecommendedListener() {
        binding.contentDetail.rgRecommended.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbRecommended -> viewModel.setRecommendedReview()
                R.id.rbNotRecommended -> viewModel.setNotRecommendedReview()
                -1 -> viewModel.clearRecommended()
            }
        }
    }

    private fun setupRatingChanges() {
        with(binding.contentDetail) {
            rbSalary.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setSalary(rating) }
            rbChief.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setChief(rating) }
            rbWorkplace.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setWorkplace(rating) }
            rbCareer.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setCareer(rating) }
            rbCollective.setOnRatingBarChangeListener { _, rating, _ ->
                viewModel.setCollective(
                    rating
                )
            }
            rbSocialPackage.setOnRatingBarChangeListener { _, rating, _ ->
                viewModel.setSocialPackage(
                    rating
                )
            }
        }
    }

    private fun positionChanges() {
        binding.contentDetail.etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.getVacancies(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun cityChanges() {
        binding.contentDetail.etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.getCities(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun dismissalDateClick() {
        viewModel.dismissalDateClick()
        datePicker.flag = DatePickerFragment.DISMISSAL_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(binding.contentDetail.etDismissalDate, getReview())
    }

    private fun employmentDateClick() {
        viewModel.employmentDateClick()
        datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(binding.contentDetail.etEmploymentDate, getReview())
    }

    private fun setupWorkStatus() {
        context?.let {
            val adapter = ArrayAdapter.createFromResource(
                it,
                R.array.work_status, R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.contentDetail.spinnerStatus.adapter = adapter
        }
    }

    private fun setDateVisibility() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = false
            ltDismissalDate.isVisible = false
        }
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        viewModel.companyId = bundle?.getString(COMPANY_ID)
        viewModel.companyName = bundle?.getString(COMPANY_NAME)
    }

    override fun setToolbar() {
        binding.toolbar.apply {
            tvTitle.setText(R.string.add_review)
            ivDone.setOnClickListener { doneClick() }
            ivClose.setOnClickListener { closeClick() }
        }
    }

    override fun closeClick() {
        viewModel.closeClick()
        fragmentManager?.popBackStackImmediate()
    }

    override fun doneClick() {
        with(binding.contentDetail) {
            viewModel.review.pluses = etPluses.text.toString().trim()
            viewModel.review.minuses = etMinuses.text.toString().trim()
            viewModel.review.position = etPosition.text.toString().trim()
            viewModel.review.city = etCity.text.toString().trim()
        }

        viewModel.doneClick(uriPhotoAdapter.getItems(), uriPhotoAdapter.wasPhotoRemoved)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> viewModel.onSelectedWorkingStatus(position)
            1 -> viewModel.onSelectedWorkedStatus(position)
            2 -> viewModel.onSelectedInterviewStatus(position)
            else -> {
                viewModel.onSelectedWorkingStatus(position)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        setIsIndicatorRatingBar(true)
    }

    private fun setIsIndicatorRatingBar(indicator: Boolean) {
        with(binding.contentDetail) {
            rbSalary.setIsIndicator(indicator)
            rbChief.setIsIndicator(indicator)
            rbWorkplace.setIsIndicator(indicator)
            rbCareer.setIsIndicator(indicator)
            rbCollective.setIsIndicator(indicator)
            rbSocialPackage.setIsIndicator(indicator)
        }
    }

    private fun setupCompanyRating() = viewModel.setupReview()

    fun getReview(): Review = viewModel.review

    private fun showVacancies(vacancies: List<Vacancy>) {
        Utils.showSuggestions(requireContext(), vacancies, binding.contentDetail.etPosition)
    }

    private fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, binding.contentDetail.etCity)
    }

    private fun successfulAddition() {
        Toast.makeText(
            activity?.applicationContext, R.string.review_added,
            Toast.LENGTH_SHORT
        ).show()
        fragmentManager?.popBackStackImmediate()
    }

    private fun showErrorAdding() {
        Toast.makeText(
            activity?.applicationContext, R.string.error_review_add,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showWorkingDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.visibility = View.VISIBLE
            ltDismissalDate.visibility = View.GONE
            groupInterview.visibility = View.VISIBLE
        }
    }

    private fun showWorkedDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = true
            ltDismissalDate.isVisible = true
            groupInterview.isVisible = true
        }
    }

    private fun showInterviewDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = false
            ltDismissalDate.isVisible = false
            groupInterview.isVisible = false
        }
    }

    private fun showPhotos(uri: List<Uri?>) {
        with(binding.contentDetail) {
            scrollContent.post { scrollContent.fullScroll(ScrollView.FOCUS_DOWN) }
            rvPhotos.visibility = View.VISIBLE
        }
        uriPhotoAdapter.isFullScreen = isFullScreenShown
        uriPhotoAdapter.addPhotos(uri)
    }

    companion object {
        private const val COMPANY_ID = "companyId"
        private const val COMPANY_NAME = "companyName"

        fun newInstance(companyId: String, companyName: String): AddReviewFragment {

            val args = Bundle()
            args.putString(COMPANY_ID, companyId)
            args.putString(COMPANY_NAME, companyName)

            val fragment = AddReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
