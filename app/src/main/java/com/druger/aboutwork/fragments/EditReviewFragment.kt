package com.druger.aboutwork.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.REVIEW
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.interfaces.view.EditReviewView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.presenters.EditReviewPresenter
import com.druger.aboutwork.utils.Utils
import kotlinx.android.synthetic.main.content_review.*
import kotlinx.android.synthetic.main.toolbar_review.*

class EditReviewFragment: BaseSupportFragment(), EditReviewView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: EditReviewPresenter

    @ProvidePresenter
    fun provideEditReviewPresenter(): EditReviewPresenter {
        return App.appComponent.editReviewPresenter
    }

    private lateinit var review: Review
    private lateinit var datePicker: DatePickerFragment

    companion object{
        fun newInstance(review: Review): EditReviewFragment {
            val args = Bundle()

            val fragment = EditReviewFragment()
            args.putParcelable(REVIEW, review)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_review, container, false)
        getBundles()
        datePicker = DatePickerFragment()
        (activity as MainActivity).hideBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setStatus()
        setupToolbar()
        setDateVisibility()
        setupWorkStatus()
        setupListeners()
        setupCompanyRating()
    }

    private fun setupListeners() {
        etEmploymentDate.setOnClickListener{employmentDateClick()}
        etDismissalDate.setOnClickListener{dismissalDateClick()}
        etInterviewDate.setOnClickListener{interviewDateClick()}
        cityChanges()
        positionChanges()
        spinnerStatus.onItemSelectedListener = this
        setupRatingChanges()
    }

    private fun setupRatingChanges() {
        ratingbar_salary.setOnRatingBarChangeListener { _, rating, _ -> presenter.setSalary(rating) }
        ratingbar_chief.setOnRatingBarChangeListener { _, rating, _ -> presenter.setChief(rating) }
        ratingbar_workplace.setOnRatingBarChangeListener { _, rating, _ -> presenter.setWorkplace(rating) }
        ratingbar_career.setOnRatingBarChangeListener { _, rating, _ -> presenter.setCareer(rating) }
        ratingbar_collective.setOnRatingBarChangeListener { _, rating, _ -> presenter.setCollective(rating) }
        ratingbar_social_package.setOnRatingBarChangeListener { _, rating, _ -> presenter.setSocialPackage(rating) }
    }

    private fun positionChanges() {
        etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getVacancies(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun cityChanges() {
        etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.getCities(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun interviewDateClick() {
        datePicker.flag = DatePickerFragment.INTERVIEW_DATE
        datePicker.show(fragmentManager, DatePickerFragment.TAG)
        datePicker.setData(etInterviewDate, review)
    }

    private fun dismissalDateClick() {
        datePicker.flag = DatePickerFragment.DISMISSAL_DATE
        datePicker.show(fragmentManager, DatePickerFragment.TAG)
        datePicker.setData(etDismissalDate, review)
    }

    private fun employmentDateClick() {
        datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE
        datePicker.show(fragmentManager, DatePickerFragment.TAG)
        datePicker.setData(etEmploymentDate, review)
    }

    private fun setupWorkStatus() {
        val adapter = ArrayAdapter.createFromResource(activity,
                R.array.work_status, R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
    }

    private fun setDateVisibility() {
        ltEmploymentDate.visibility = View.GONE
        ltDismissalDate.visibility = View.GONE
        ltInterviewDate.visibility = View.GONE
    }

    private fun setUI() {
        etPosition.setText(review.position)
        etPluses.setText(review.pluses)
        etMinuses.setText(review.minuses)
        etCity.setText(review.city)
        etEmploymentDate.setText(review.employmentDate.let { Utils.getDate(it) })
        etDismissalDate.setText(review.dismissalDate.let { Utils.getDate(it) })
        etInterviewDate.setText(review.interviewDate.let { Utils.getDate(it) })
    }

    private fun getBundles() {
        val bundle = arguments
        if (bundle != null) {
            review = bundle.get(REVIEW) as Review
        }
    }

    private fun setupToolbar() {
        ivDone.setOnClickListener{doneClick()}
        ivClose.setOnClickListener{closeClick()}
        tvTitle.setText(R.string.edit_review)
    }

    private fun closeClick() = fragmentManager?.popBackStackImmediate()

    private fun doneClick() {
        review.pluses = etPluses.text.toString().trim()
        review.minuses = etMinuses.text.toString().trim()
        review.position = etPosition.text.toString().trim()
        review.city = etCity.text.toString()

        presenter.doneClick()
    }

    private fun setStatus() =
        when (review.status) {
            0 -> spinnerStatus.setPromptId(R.string.working)
            1 -> spinnerStatus.setPromptId(R.string.worked)
            2 -> spinnerStatus.setPromptId(R.string.interview)
            else -> null
        }

    private fun setupCompanyRating() {
        presenter.setupRating(review)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomNavigation()
        unbindDrawables(rootView)
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
        ratingbar_salary.setIsIndicator(indicator)
        ratingbar_chief.setIsIndicator(indicator)
        ratingbar_workplace.setIsIndicator(indicator)
        ratingbar_career.setIsIndicator(indicator)
        ratingbar_collective.setIsIndicator(indicator)
        ratingbar_social_package.setIsIndicator(indicator)
    }

    private fun showSuggestions(items: List<Any>, view: AutoCompleteTextView) {
        val arrayAdapter = ArrayAdapter<Any>(
                activity, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter<ArrayAdapter<*>>(arrayAdapter)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setIsIndicator(true)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> presenter.onSelectedWorkingStatus(position)
            1 -> presenter.onSelectedWorkedStatus(position)
            2 -> presenter.onSelectedInterviewStatus(position)
            else -> { }
        }
    }

    override fun showVacancies(vacancies: List<Vacancy>) {
        showSuggestions(vacancies, etPosition)
    }

    override fun showCities(cities: List<City>) {
        showSuggestions(cities, etCity)
    }

    override fun showWorkingDate() {
        ltEmploymentDate.visibility = View.VISIBLE
        ltDismissalDate.visibility = View.GONE
        ltInterviewDate.visibility = View.GONE
    }

    override fun setIsIndicatorRatingBar(indicator: Boolean) = setIsIndicator(indicator)

    override fun showWorkedDate() {
        ltEmploymentDate.visibility = View.VISIBLE
        ltDismissalDate.visibility = View.VISIBLE
        ltInterviewDate.visibility = View.GONE
    }

    override fun showInterviewDate() {
        ltInterviewDate.visibility = View.VISIBLE
        ltEmploymentDate.visibility = View.GONE
        ltDismissalDate.visibility = View.GONE
    }

    override fun clearRatingBar() {
        ratingbar_salary.rating = 0f
        ratingbar_chief.rating = 0f
        ratingbar_workplace.rating = 0f
        ratingbar_career.rating = 0f
        ratingbar_collective.rating = 0f
        ratingbar_social_package.rating = 0f
    }

    override fun successfulEditing() {
        Toast.makeText(activity?.applicationContext, R.string.review_edited,
                Toast.LENGTH_SHORT).show()
        fragmentManager?.popBackStackImmediate()
    }

    override fun showErrorEditing() {
        Toast.makeText(activity?.applicationContext, R.string.error_review_edit,
                Toast.LENGTH_SHORT).show()
    }
}