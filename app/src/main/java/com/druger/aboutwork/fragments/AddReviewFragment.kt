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
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.COMPANY_DETAIL
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.AddReviewView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.presenters.AddReviewPresenter
import kotlinx.android.synthetic.main.content_review.*
import kotlinx.android.synthetic.main.toolbar_review.*


class AddReviewFragment : MvpAppCompatFragment(), AdapterView.OnItemSelectedListener, AddReviewView {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    private lateinit var datePicker: DatePickerFragment
    private lateinit var rootView: View

    @ProvidePresenter
    fun provideAddReviewPresenter(): AddReviewPresenter {
        return App.appComponent.addReviewPresenter
    }

    companion object{
        fun newInstance(companyDetail: CompanyDetail): AddReviewFragment {

            val args = Bundle()
            args.putParcelable(COMPANY_DETAIL, companyDetail)

            val fragment = AddReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_review, container, false)
        getBundles()
        setupToolbar()
        datePicker = DatePickerFragment()
        setDateVisibility()
        setupWorkStatus()
        setupListeners()
        setupCompanyRating()
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
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

    private fun setupListeners() {
        etEmploymentDate.setOnClickListener{ employmentDateClick() }
        etDismissalDate.setOnClickListener{ dismissalDateClick() }
        etInterviewDate.setOnClickListener{ interviewDateClick() }
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
        datePicker.setData(etInterviewDate, getReview())
    }

    private fun dismissalDateClick() {
        datePicker.flag = DatePickerFragment.DISMISSAL_DATE
        datePicker.show(fragmentManager, DatePickerFragment.TAG)
        datePicker.setData(etDismissalDate, getReview())
    }

    private fun employmentDateClick() {
        datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE
        datePicker.show(fragmentManager, DatePickerFragment.TAG)
        datePicker.setData(etEmploymentDate, getReview())
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

    private fun getBundles() {
        val bundle = arguments
        if (bundle != null) {
            presenter.companyDetail = bundle.get(COMPANY_DETAIL) as CompanyDetail
        }
    }

    private fun setupToolbar() {
        tvTitle.setText(R.string.add_review)

        ivDone.setOnClickListener{ doneClick() }
        ivClose.setOnClickListener{ closeClick() }
    }

    private fun closeClick() {
        fragmentManager?.popBackStackImmediate()
    }

    private fun doneClick() {
        presenter.review.pluses = etPluses.text.toString().trim()
        presenter.review.minuses = etMinuses.text.toString().trim()
        presenter.review.position = etPosition.text.toString().trim()
        presenter.review.city = etCity.text.toString()

        presenter.doneClick()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (position) {
            0 -> presenter.onSelectedWorkingStatus(position)
            1 -> presenter.onSelectedWorkedStatus(position)
            2 -> presenter.onSelectedInterviewStatus(position)
            else -> { }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        setIsIndicator(true)
    }

    private fun setIsIndicator(indicator: Boolean) {
        ratingbar_salary.setIsIndicator(indicator)
        ratingbar_chief.setIsIndicator(indicator)
        ratingbar_workplace.setIsIndicator(indicator)
        ratingbar_career.setIsIndicator(indicator)
        ratingbar_collective.setIsIndicator(indicator)
        ratingbar_social_package.setIsIndicator(indicator)
    }

    private fun setupCompanyRating() = presenter.setupReview()

    fun    getReview(): Review  = presenter.review

    override fun showVacancies(vacancies: List<Vacancy>) {
        showSuggestions(vacancies, etPosition)
    }

    private fun showSuggestions(items: List<Any>, view: AutoCompleteTextView) {
        val arrayAdapter = ArrayAdapter<Any>(
                activity, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter<ArrayAdapter<*>>(arrayAdapter)
    }

    override fun showCities(cities: List<City>) {
        showSuggestions(cities, etCity)
    }

    override fun successfulAddition() {
        Toast.makeText(activity?.applicationContext, R.string.review_added,
                Toast.LENGTH_SHORT).show()
        fragmentManager?.popBackStackImmediate()
    }

    override fun showErrorAdding() {
        Toast.makeText(activity?.applicationContext, R.string.error_review_add,
                Toast.LENGTH_SHORT).show()
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
}
