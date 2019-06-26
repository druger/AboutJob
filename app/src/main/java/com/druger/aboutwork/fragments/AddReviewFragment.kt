package com.druger.aboutwork.fragments


import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class AddReviewFragment : BaseSupportFragment(), AdapterView.OnItemSelectedListener, AddReviewView {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    private lateinit var datePicker: DatePickerFragment

    private lateinit var groupRating: Group
    private lateinit var etPluses: EditText
    private lateinit var etMinuses: EditText
    private lateinit var ivClose: ImageView
    private lateinit var ivDone: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var ltEmploymentDate: TextInputLayout
    private lateinit var ltDismissalDate: TextInputLayout
    private lateinit var ltInterviewDate: TextInputLayout
    private lateinit var rbSalary: RatingBar
    private lateinit var rbChief: RatingBar
    private lateinit var rbCareer: RatingBar
    private lateinit var rbCollective: RatingBar
    private lateinit var rbSocialPackage: RatingBar
    private lateinit var rbWorkplace: RatingBar
    private lateinit var etCity: AutoCompleteTextView
    private lateinit var etPosition: AutoCompleteTextView
    private lateinit var spinnerStatus: Spinner
    private lateinit var etInterviewDate: EditText
    private lateinit var etDismissalDate: EditText
    private lateinit var etEmploymentDate: EditText

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
        bindViews()
        datePicker = DatePickerFragment()
        return rootView
    }

    private fun bindViews() {
        groupRating = bindView(R.id.group_rating)
        etPluses = bindView(R.id.etPluses)
        etMinuses = bindView(R.id.etMinuses)
        ivClose = bindView(R.id.ivClose)
        ivDone = bindView(R.id.ivDone)
        tvTitle = bindView(R.id.tvTitle)
        ltEmploymentDate = bindView(R.id.ltEmploymentDate)
        ltDismissalDate = bindView(R.id.ltDismissalDate)
        ltInterviewDate = bindView(R.id.ltInterviewDate)
        rbSalary = bindView(R.id.rbSalary)
        rbChief = bindView(R.id.rbChief)
        rbCareer = bindView(R.id.rbCareer)
        rbCollective = bindView(R.id.rbCollective)
        rbSocialPackage = bindView(R.id.rbSocialPackage)
        rbWorkplace = bindView(R.id.rbWorkplace)
        etCity = bindView(R.id.etCity)
        etPosition = bindView(R.id.etPosition)
        spinnerStatus = bindView(R.id.spinnerStatus)
        etInterviewDate = bindView(R.id.etInterviewDate)
        etDismissalDate = bindView(R.id.etDismissalDate)
        etEmploymentDate = bindView(R.id.etEmploymentDate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setDateVisibility()
        setupWorkStatus()
        setupListeners()
        setupCompanyRating()
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
        rbSalary.setOnRatingBarChangeListener { _, rating, _ -> presenter.setSalary(rating) }
        rbChief.setOnRatingBarChangeListener { _, rating, _ -> presenter.setChief(rating) }
        rbWorkplace.setOnRatingBarChangeListener { _, rating, _ -> presenter.setWorkplace(rating) }
        rbCareer.setOnRatingBarChangeListener { _, rating, _ -> presenter.setCareer(rating) }
        rbCollective.setOnRatingBarChangeListener { _, rating, _ -> presenter.setCollective(rating) }
        rbSocialPackage.setOnRatingBarChangeListener { _, rating, _ -> presenter.setSocialPackage(rating) }
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
        rbSalary.setIsIndicator(indicator)
        rbChief.setIsIndicator(indicator)
        rbWorkplace.setIsIndicator(indicator)
        rbCareer.setIsIndicator(indicator)
        rbCollective.setIsIndicator(indicator)
        rbSocialPackage.setIsIndicator(indicator)
    }

    private fun setupCompanyRating() = presenter.setupReview()

    fun getReview(): Review = presenter.review

    override fun showVacancies(vacancies: List<Vacancy>) {
        showSuggestions(vacancies, etPosition)
    }

    private fun showSuggestions(items: List<Any>, view: AutoCompleteTextView) {
        val arrayAdapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter<ArrayAdapter<*>>(arrayAdapter)
    }

    override fun showCities(cities: List<City>) {
        showSuggestions(cities, etCity)
    }

    override fun successfulAddition() {
        Toast.makeText(context, R.string.review_added, Toast.LENGTH_SHORT).show()
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
        groupRating.visibility = View.VISIBLE
    }

    override fun setIsIndicatorRatingBar(indicator: Boolean) = setIsIndicator(indicator)

    override fun showWorkedDate() {
        ltEmploymentDate.visibility = View.VISIBLE
        ltDismissalDate.visibility = View.VISIBLE
        ltInterviewDate.visibility = View.GONE
        groupRating.visibility = View.VISIBLE
    }

    override fun showInterviewDate() {
        ltInterviewDate.visibility = View.VISIBLE
        ltEmploymentDate.visibility = View.GONE
        ltDismissalDate.visibility = View.GONE
        groupRating.visibility = View.GONE
    }
}
