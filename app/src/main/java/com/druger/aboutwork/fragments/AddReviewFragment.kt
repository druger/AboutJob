package com.druger.aboutwork.fragments


import android.app.Activity.RESULT_OK
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
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.interfaces.view.AddReviewView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.presenters.AddReviewPresenter
import com.druger.aboutwork.utils.Utils
import kotlinx.android.synthetic.main.content_review.*
import kotlinx.android.synthetic.main.toolbar_review.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class AddReviewFragment : ReviewFragment(), AdapterView.OnItemSelectedListener, AddReviewView {

    @InjectPresenter
    lateinit var presenter: AddReviewPresenter

    private lateinit var datePicker: DatePickerFragment

    @ProvidePresenter
    fun provideAddReviewPresenter() = AddReviewPresenter()

    companion object{

        fun newInstance(companyId: String, companyName: String): AddReviewFragment {

            val args = Bundle()
            args.putString(COMPANY_ID, companyId)
            args.putString(COMPANY_NAME, companyName)

            val fragment = AddReviewFragment()
            fragment.arguments = args
            return fragment
        }

        private const val COMPANY_ID = "companyId"
        private const val COMPANY_NAME = "companyName"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_review, container, false)
        getData(savedInstanceState)
        datePicker = DatePickerFragment()
        (activity as MainActivity).hideBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setDateVisibility()
        setupWorkStatus()
        setupCompanyRating()
        setupListeners()
        setupRecycler()
    }

    private fun setupRecycler() {
        rvPhotos.apply {
            adapter = uriPhotoAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView?.let { unbindDrawables(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(COMPANY_ID, presenter.companyId)
        outState.putString(COMPANY_NAME, presenter.companyName)
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
        etEmploymentDate.setOnClickListener { employmentDateClick() }
        etDismissalDate.setOnClickListener { dismissalDateClick() }
        cityChanges()
        positionChanges()
        spinnerStatus.onItemSelectedListener = this
        setupRatingChanges()
        radioGroupRecommendedListener()
        ivAddPhoto.setOnClickListener {
            presenter.sendAnalytics()
            checkPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == RC_PICK_IMAGE) {
            presenter.getUriImages(data)
        }
    }

    private fun radioGroupRecommendedListener() {
        rgRecommended.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rbRecommended -> presenter.setRecommendedReview()
                R.id.rbNotRecommended -> presenter.setNotRecommendedReview()
                -1 -> presenter.clearRecommended()
            }
        }
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

    private fun dismissalDateClick() {
        presenter.dismissalDateClick()
        datePicker.flag = DatePickerFragment.DISMISSAL_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(etDismissalDate, getReview())
    }

    private fun employmentDateClick() {
        presenter.employmentDateClick()
        datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE
        fragmentManager?.let { datePicker.show(it, DatePickerFragment.TAG) }
        datePicker.setData(etEmploymentDate, getReview())
    }

    private fun setupWorkStatus() {
        context?.let {
            val adapter = ArrayAdapter.createFromResource(it,
                    R.array.work_status, R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter
        }
    }

    private fun setDateVisibility() {
        ltEmploymentDate.visibility = View.GONE
        ltDismissalDate.visibility = View.GONE
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        presenter.companyId = bundle?.getString(COMPANY_ID)
        presenter.companyName = bundle?.getString(COMPANY_NAME)
    }

    private fun setupToolbar() {
        tvTitle.setText(R.string.add_review)

        ivDone.setOnClickListener{ doneClick() }
        ivClose.setOnClickListener{ closeClick() }
    }

    private fun closeClick() {
        presenter.closeClick()
        fragmentManager?.popBackStackImmediate()
    }

    private fun doneClick() {
        presenter.review.pluses = etPluses.text.toString().trim()
        presenter.review.minuses = etMinuses.text.toString().trim()
        presenter.review.position = etPosition.text.toString().trim()
        presenter.review.city = etCity.text.toString().trim()

        presenter.doneClick(uriPhotoAdapter.itemCount)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> presenter.onSelectedWorkingStatus(position)
            1 -> presenter.onSelectedWorkedStatus(position)
            2 -> presenter.onSelectedInterviewStatus(position)
            else -> { presenter.onSelectedWorkingStatus(position) }
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
        Utils.showSuggestions(requireContext(), vacancies, etPosition)
    }

    override fun showCities(cities: List<City>) {
        Utils.showSuggestions(requireContext(), cities, etCity)
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
        groupInterview.visibility = View.VISIBLE
    }

    override fun setIsIndicatorRatingBar(indicator: Boolean) = setIsIndicator(indicator)

    override fun showWorkedDate() {
        ltEmploymentDate.visibility = View.VISIBLE
        ltDismissalDate.visibility = View.VISIBLE
        groupInterview.visibility = View.VISIBLE
    }

    override fun showInterviewDate() {
        ltEmploymentDate.visibility = View.GONE
        ltDismissalDate.visibility = View.GONE
        groupInterview.visibility = View.GONE
    }

    override fun showPhotos(uri: List<Uri?>) {
        scrollContent.post { scrollContent.fullScroll(ScrollView.FOCUS_DOWN) }
        rvPhotos.visibility = View.VISIBLE
        uriPhotoAdapter?.addPhotos(uri)
    }
}
