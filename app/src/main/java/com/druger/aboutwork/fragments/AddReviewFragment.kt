package com.druger.aboutwork.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.Bundles.COMPANY_DETAIL
import com.druger.aboutwork.R
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.AddReviewPresenter
import kotlinx.android.synthetic.main.content_review.*
import kotlinx.android.synthetic.main.toolbar_review.*


class AddReviewFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    private lateinit var datePicker: DatePickerFragment

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.fragment_review, container, false)
        getBundles()
        setupToolbar()
        datePicker = DatePickerFragment()
        setDateVisibility()
        setupWorkStatus()
        setupListeners()
        setupCompanyRating()
        return rootView
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
        ratingbar_salary.setOnRatingBarChangeListener { _, rating, _ -> setSalary(rating) }
        ratingbar_chief.setOnRatingBarChangeListener { _, rating, _ -> setChief(rating) }
        ratingbar_workplace.setOnRatingBarChangeListener { _, rating, _ -> setWorkplace(rating) }
        ratingbar_career.setOnRatingBarChangeListener { _, rating, _ -> setCareer(rating) }
        ratingbar_collective.setOnRatingBarChangeListener { _, rating, _ -> setCollective(rating) }
        ratingbar_social_package.setOnRatingBarChangeListener { _, rating, _ -> setSocialPackage(rating) }
    }

    private fun positionChanges() {
        etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                reviewPresenter.getVacancies(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    private fun cityChanges() {
        etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                reviewPresenter.getCities(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
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
        fragmentManager.popBackStackImmediate()
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
            0 -> reviewPresenter.onSelectedWorkingStatus(position)
            1 -> reviewPresenter.onSelectedWorkedStatus(position)
            2 -> reviewPresenter.onSelectedInterviewStatus(position)
            else -> {
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        setIsIndicator(true)
    }

    private fun setupCompanyRating() = presenter.setupReview()

    override fun    getReview(): Review  = presenter.review

    override fun setSocialPackage(rating: Float) = presenter.setSocialPackage(rating)

    override fun setCollective(rating: Float) = presenter.setCollective(rating)

    override fun setCareer(rating: Float) = presenter.setCareer(rating)

    override fun setWorkplace(rating: Float) = presenter.setWorkplace(rating)

    override fun setChief(rating: Float) = presenter.setChief(rating)

    override fun setSalary(rating: Float) = presenter.setSalary(rating)
}
