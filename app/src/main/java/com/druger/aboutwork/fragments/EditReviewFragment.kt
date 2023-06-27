package com.druger.aboutwork.fragments

import android.app.Activity
import android.content.Intent
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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.databinding.FragmentReviewBinding
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.utils.UploadPhotoHelper
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.viewmodels.EditReviewViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReviewFragment : ReviewFragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: EditReviewViewModel by viewModels()

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var mergeAdapter: ConcatAdapter

    private lateinit var review: Review
    private lateinit var reviewKey: String
    private lateinit var datePicker: DatePickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeStates()
    }

    private fun observeStates() {
        viewModel.companyRatingState.observe(this) { setupCompanyRating(it) }
        viewModel.successEditing.observe(this) { successfulEditing() }
        viewModel.errorEditing.observe(this) { showErrorEditing() }
        viewModel.vacanciesState.observe(this) { showVacancies(it) }
        viewModel.citiesState.observe(this) { showCities(it) }
        viewModel.workingDate.observe(this) { showWorkingDate() }
        viewModel.workedDate.observe(this) { showWorkedDate() }
        viewModel.interviewDate.observe(this) { showInterviewDate() }
        viewModel.reviewState.observe(this) { setReview(it) }
        viewModel.photosState.observe(this) { showDownloadedPhotos(it) }
        viewModel.indicatorRatingBar.observe(this) { setIsIndicator(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        datePicker = DatePickerFragment()
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundles()
        getReview()
        setDateVisibility()
        setupListeners()
        setupRecycler()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_PICK_IMAGE) {
            UploadPhotoHelper.getUriImages(data) { uri ->
                showPhotos(uri)
            }
        }
    }

    private fun setupRecycler() {
        mergeAdapter = ConcatAdapter(uriPhotoAdapter, storageRefPhotoAdapter)
        binding.contentDetail.rvPhotos.apply {
            adapter = mergeAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun setupListeners() {
        with(binding.contentDetail) {
            etEmploymentDate.setOnClickListener { employmentDateClick() }
            etDismissalDate.setOnClickListener { dismissalDateClick() }
            cityChanges()
            positionChanges()
            spinnerStatus.onItemSelectedListener = this@EditReviewFragment
            setupRatingChanges()
            ivAddPhoto.setOnClickListener {
                viewModel.sendAnalytics()
                checkPermission()
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
        datePicker.flag = DatePickerFragment.DISMISSAL_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(binding.contentDetail.etDismissalDate, review)
    }

    private fun employmentDateClick() {
        datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(binding.contentDetail.etEmploymentDate, review)
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
        setStatus()
    }

    private fun setDateVisibility() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = false
            ltDismissalDate.isVisible = false
        }
    }

    private fun setUI() {
        with(binding.contentDetail) {
            etPosition.setText(review.position)
            etPluses.setText(review.pluses)
            etMinuses.setText(review.minuses)
            etCity.setText(review.city)
            setDate(review.employmentDate, etEmploymentDate)
            setDate(review.dismissalDate, etDismissalDate)
        }
    }

    private fun setDate(date: Long, etDate: TextInputEditText) {
        if (date != 0L) {
            etDate.setText(date.let { Utils.getDate(it) })
            etDate.background = null
        }
    }

    private fun getBundles() {
        arguments?.let { reviewKey = it.getString(REVIEW_KEY, "") }
    }

    private fun getReview() {
        viewModel.getReview(reviewKey)
    }

    private fun setReview(review: Review) {
        this.review = review
        setUI()
        setupWorkStatus()
        viewModel.setupRating(review)
        setupRecommendation(review)
        review.firebaseKey?.let { viewModel.getPhotos(it) }
    }

    private fun setupRecommendation(review: Review) {
        with(binding.contentDetail) {
            review.recommended?.let { recommendation ->
                if (recommendation) rbRecommended.isChecked = true
                else rbNotRecommended.isChecked = true
            }
            rgRecommended.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbRecommended -> viewModel.setRecommendedReview()
                    R.id.rbNotRecommended -> viewModel.setNotRecommendedReview()
                    -1 -> viewModel.clearRecommended()
                }
            }
        }
    }

    override fun setToolbar() {
        with(binding.toolbar) {
            ivDone.setOnClickListener { doneClick() }
            ivClose.setOnClickListener { closeClick() }
            tvTitle.setText(R.string.edit_review)
        }
    }

    override fun closeClick() {
        fragmentManager?.popBackStackImmediate()
    }

    override fun doneClick() {
        with(binding.contentDetail) {
            review.pluses = etPluses.text.toString().trim()
            review.minuses = etMinuses.text.toString().trim()
            review.position = etPosition.text.toString().trim()
            review.city = etCity.text.toString().trim()

            viewModel.doneClick()
        }
    }

    private fun setStatus() =
        when (review.status) {
            Review.WORKING -> binding.contentDetail.spinnerStatus.setSelection(0)
            Review.WORKED -> binding.contentDetail.spinnerStatus.setSelection(1)
            Review.INTERVIEW -> binding.contentDetail.spinnerStatus.setSelection(2)
            else -> null
        }

    private fun setupCompanyRating(mark: MarkCompany) {
        with(binding.contentDetail) {
            rbSalary.rating = mark.salary
            rbChief.rating = mark.chief
            rbWorkplace.rating = mark.workplace
            rbCareer.rating = mark.career
            rbCollective.rating = mark.collective
            rbSocialPackage.rating = mark.socialPackage
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView?.let { unbindDrawables(it) }
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

    private fun setIsIndicator(indicator: Boolean) {
        with(binding.contentDetail) {
            rbSalary.setIsIndicator(indicator)
            rbChief.setIsIndicator(indicator)
            rbWorkplace.setIsIndicator(indicator)
            rbCareer.setIsIndicator(indicator)
            rbCollective.setIsIndicator(indicator)
            rbSocialPackage.setIsIndicator(indicator)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setIsIndicator(true)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> viewModel.onSelectedWorkingStatus(position)
            1 -> viewModel.onSelectedWorkedStatus(position)
            2 -> viewModel.onSelectedInterviewStatus(position)
            else -> {
            }
        }
    }

    private fun showVacancies(vacancies: List<Vacancy>) {
        Utils.showSuggestions(requireContext(), vacancies, binding.contentDetail.etPosition)
    }

    private fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, binding.contentDetail.etCity)
    }

    private fun showWorkingDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = true
            ltDismissalDate.isVisible = false
            groupInterview.isVisible = true
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

    private fun clearRatingBar() {
        with(binding.contentDetail) {
            rbSalary.rating = 0f
            rbChief.rating = 0f
            rbWorkplace.rating = 0f
            rbCareer.rating = 0f
            rbCollective.rating = 0f
            rbSocialPackage.rating = 0f
        }
    }

    private fun successfulEditing() {
        Toast.makeText(
            activity?.applicationContext, R.string.review_edited,
            Toast.LENGTH_SHORT
        ).show()
        fragmentManager?.popBackStackImmediate()
    }

    private fun showErrorEditing() {
        Toast.makeText(
            activity?.applicationContext, R.string.error_review_edit,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showPhotos(uri: List<Uri?>) {
        with(binding.contentDetail) {
            scrollContent.post { scrollContent.fullScroll(ScrollView.FOCUS_DOWN) }
            rvPhotos.isVisible = true
            uriPhotoAdapter.addPhotos(uri)
        }
    }

    private fun showDownloadedPhotos(photos: List<StorageReference>) {
        binding.contentDetail.rvPhotos.isVisible = true
        storageRefPhotoAdapter.addPhotos(photos)
    }

    companion object {
        private const val REVIEW_KEY = "review_key"

        fun newInstance(reviewKey: String): EditReviewFragment {
            val args = Bundle()

            val fragment = EditReviewFragment()
            args.putString(REVIEW_KEY, reviewKey)
            fragment.arguments = args
            return fragment
        }
    }
}