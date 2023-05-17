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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.databinding.FragmentReviewBinding
import com.druger.aboutwork.interfaces.view.EditReviewView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.presenters.EditReviewPresenter
import com.druger.aboutwork.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.StorageReference
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class EditReviewFragment : ReviewFragment(), EditReviewView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: EditReviewPresenter

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var mergeAdapter: ConcatAdapter

    @ProvidePresenter
    fun provideEditReviewPresenter() = EditReviewPresenter()

    private lateinit var review: Review
    private lateinit var reviewKey: String
    private lateinit var datePicker: DatePickerFragment

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
            presenter.getUriImages(data)
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
                presenter.sendAnalytics()
                checkPermission()
            }
        }
    }

    private fun setupRatingChanges() {
        with(binding.contentDetail) {
            rbSalary.setOnRatingBarChangeListener { _, rating, _ -> presenter.setSalary(rating) }
            rbChief.setOnRatingBarChangeListener { _, rating, _ -> presenter.setChief(rating) }
            rbWorkplace.setOnRatingBarChangeListener { _, rating, _ -> presenter.setWorkplace(rating) }
            rbCareer.setOnRatingBarChangeListener { _, rating, _ -> presenter.setCareer(rating) }
            rbCollective.setOnRatingBarChangeListener { _, rating, _ ->
                presenter.setCollective(
                    rating
                )
            }
            rbSocialPackage.setOnRatingBarChangeListener { _, rating, _ ->
                presenter.setSocialPackage(
                    rating
                )
            }
        }
    }

    private fun positionChanges() {
        binding.contentDetail.etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getVacancies(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun cityChanges() {
        binding.contentDetail.etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getCities(s.toString())
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
        presenter.getReview(reviewKey)
    }

    override fun setReview(review: Review) {
        this.review = review
        setUI()
        setupWorkStatus()
        presenter.setupRating(review)
        setupRecommendation(review)
        review.firebaseKey?.let { presenter.getPhotos(it) }
    }

    private fun setupRecommendation(review: Review) {
        with(binding.contentDetail) {
            review.recommended?.let { recommendation ->
                if (recommendation) rbRecommended.isChecked = true
                else rbNotRecommended.isChecked = true
            }
            rgRecommended.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbRecommended -> presenter.setRecommendedReview()
                    R.id.rbNotRecommended -> presenter.setNotRecommendedReview()
                    -1 -> presenter.clearRecommended()
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

            presenter.doneClick()
        }
    }

    private fun setStatus() =
        when (review.status) {
            Review.WORKING -> binding.contentDetail.spinnerStatus.setSelection(0)
            Review.WORKED -> binding.contentDetail.spinnerStatus.setSelection(1)
            Review.INTERVIEW -> binding.contentDetail.spinnerStatus.setSelection(2)
            else -> null
        }

    override fun setupCompanyRating(mark: MarkCompany) {
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
            0 -> presenter.onSelectedWorkingStatus(position)
            1 -> presenter.onSelectedWorkedStatus(position)
            2 -> presenter.onSelectedInterviewStatus(position)
            else -> {
            }
        }
    }

    override fun showVacancies(vacancies: List<Vacancy>) {
        Utils.showSuggestions(requireContext(), vacancies, binding.contentDetail.etPosition)
    }

    override fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, binding.contentDetail.etCity)
    }

    override fun showWorkingDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = true
            ltDismissalDate.isVisible = false
            groupInterview.isVisible = true
        }
    }

    override fun setIsIndicatorRatingBar(indicator: Boolean) = setIsIndicator(indicator)

    override fun showWorkedDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = true
            ltDismissalDate.isVisible = true
            groupInterview.isVisible = true
        }
    }

    override fun showInterviewDate() {
        with(binding.contentDetail) {
            ltEmploymentDate.isVisible = false
            ltDismissalDate.isVisible = false
            groupInterview.isVisible = false
        }
    }

    override fun clearRatingBar() {
        with(binding.contentDetail) {
            rbSalary.rating = 0f
            rbChief.rating = 0f
            rbWorkplace.rating = 0f
            rbCareer.rating = 0f
            rbCollective.rating = 0f
            rbSocialPackage.rating = 0f
        }
    }

    override fun successfulEditing() {
        Toast.makeText(
            activity?.applicationContext, R.string.review_edited,
            Toast.LENGTH_SHORT
        ).show()
        fragmentManager?.popBackStackImmediate()
    }

    override fun showErrorEditing() {
        Toast.makeText(
            activity?.applicationContext, R.string.error_review_edit,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showPhotos(uri: List<Uri?>) {
        with(binding.contentDetail) {
            scrollContent.post { scrollContent.fullScroll(ScrollView.FOCUS_DOWN) }
            rvPhotos.isVisible = true
            uriPhotoAdapter.addPhotos(uri)
        }
    }

    override fun showDownloadedPhotos(photos: List<StorageReference>) {
        binding.contentDetail.rvPhotos.isVisible = true
        storageRefPhotoAdapter.addPhotos(photos)
    }
}