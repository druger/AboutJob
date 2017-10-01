package com.druger.aboutwork.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.ReviewView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.ReviewPresenter;
import com.druger.aboutwork.utils.Utils;

import java.util.Calendar;

import static com.druger.aboutwork.Const.Bundles.COMPANY_DETAIL;
import static com.druger.aboutwork.Const.Bundles.FROM_ACCOUNT;
import static com.druger.aboutwork.Const.Bundles.REVIEW;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends BaseFragment implements ReviewView, View.OnClickListener {

    @InjectPresenter
    ReviewPresenter reviewPresenter;

    private RatingBar salary;
    private RatingBar chief;
    private RatingBar workplace;
    private RatingBar career;
    private RatingBar collective;
    private RatingBar socialPackage;

    private TextInputEditText etPluses;
    private TextInputEditText etMinuses;
    private TextInputEditText etPosition;

    private TextInputLayout ltEmploymentDate;
    private TextInputLayout ltDismissalDate;
    private TextInputLayout ltInterviewDate;
    private EditText etEmploymentDate;
    private EditText etDismissalDate;
    private EditText etInterviewDate;

    private RadioGroup radioGroup;
    private Button btnAdd;
    private Button btnEdit;

    private DatePickerFragment datePicker;

    private CompanyDetail companyDetail;
    private Review review;
    private boolean fromAccount;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(Review review, boolean fromAccount) {
        Bundle args = new Bundle();

        ReviewFragment fragment = new ReviewFragment();
        args.putParcelable(REVIEW, review);
        args.putBoolean(FROM_ACCOUNT, fromAccount);
        fragment.setArguments(args);
        return fragment;
    }

    public static ReviewFragment newInstance(CompanyDetail companyDetail) {

        Bundle args = new Bundle();
        args.putParcelable(COMPANY_DETAIL, companyDetail);

        ReviewFragment fragment = new ReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            review = (Review) bundle.get(REVIEW);
            fromAccount = bundle.getBoolean(FROM_ACCOUNT);
            companyDetail = (CompanyDetail) bundle.get(COMPANY_DETAIL);
        }

        if (!fromAccount) {
            rootView = inflater.inflate(R.layout.fragment_review, container, false);

            String companyId = companyDetail.getId();
            reviewPresenter.setCompanyId(companyId);
            setupToolbar();
        } else {
            rootView = inflater.inflate(R.layout.fragment_review_no_actionbar, container, false);
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
        setupUI();
        setupListeners();
        checkSelectedStatus();

        reviewPresenter.setCompanyRating(salary, chief, workplace, career, collective, socialPackage,
                review, fromAccount);
        if (fromAccount) {
            fillData();
        }
        return rootView;
    }

    private void checkSelectedStatus() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            setIsIndicator(true);
        }
    }

    private void fillData() {
        etPluses.setText(review.getPluses());
        etMinuses.setText(review.getMinuses());
        etPosition.setText(review.getPosition());
        etEmploymentDate.setText(Utils.getDate(review.getEmploymentDate()));
        etDismissalDate.setText(Utils.getDate(review.getDismissalDate()));
        etInterviewDate.setText(Utils.getDate(review.getInterviewDate()));

        MarkCompany markCompany = review.getMarkCompany();
        salary.setRating(markCompany.getSalary());
        chief.setRating(markCompany.getChief());
        workplace.setRating(markCompany.getWorkplace());
        career.setRating(markCompany.getCareer());
        collective.setRating(markCompany.getCollective());
        socialPackage.setRating(markCompany.getSocialPackage());

        switch (review.getStatus()) {
            case 0:
                radioGroup.check(R.id.radio_working);
                break;
            case 1:
                radioGroup.check(R.id.radio_worked);
                break;
            case 2:
                radioGroup.check(R.id.radio_interview);
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (companyDetail != null) {
            getActionBar().setTitle(companyDetail.getName());
        }
    }

    private void setupListeners() {
        radioGroup.setOnCheckedChangeListener(reviewPresenter);
        btnAdd.setOnClickListener(this);
        etEmploymentDate.setOnClickListener(this);
        etDismissalDate.setOnClickListener(this);
        etInterviewDate.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void setupUI() {
        datePicker = new DatePickerFragment();

        etPluses = bindView(R.id.etPluses);
        etMinuses = bindView(R.id.etMinuses);
        etPosition = bindView(R.id.et_position);

        salary = bindView(R.id.ratingbar_salary);
        chief = bindView(R.id.ratingbar_chief);
        workplace = bindView(R.id.ratingbar_workplace);
        career = bindView(R.id.ratingbar_career);
        collective = bindView(R.id.ratingbar_collective);
        socialPackage = bindView(R.id.ratingbar_social_package);

        radioGroup = bindView(R.id.radio_group);

        ltEmploymentDate = bindView(R.id.ltEmploymentDate);
        ltDismissalDate = bindView(R.id.ltDismissalDate);
        ltInterviewDate = bindView(R.id.ltInterviewDate);
        etEmploymentDate = bindView(R.id.etEmploymentDate);
        etDismissalDate = bindView(R.id.etDismissalDate);
        etInterviewDate = bindView(R.id.etInterviewDate);

        btnAdd = bindView(R.id.btnAdd);
        btnEdit = bindView(R.id.btnEdit);

        ltEmploymentDate.setVisibility(View.GONE);
        ltDismissalDate.setVisibility(View.GONE);
        ltInterviewDate.setVisibility(View.GONE);

        if (fromAccount) {
            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(rootView);
        if (fromAccount) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                checkReview(false);
                break;
            case R.id.etEmploymentDate:
                datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etEmploymentDate, reviewPresenter.getReview());
                break;
            case R.id.etDismissalDate:
                datePicker.flag = DatePickerFragment.DISMISSAL_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etDismissalDate, reviewPresenter.getReview());
                break;
            case R.id.etInterviewDate:
                datePicker.flag = DatePickerFragment.INTERVIEW_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etInterviewDate, reviewPresenter.getReview());
                break;
            case R.id.btnEdit:
                checkReview(true);
                break;
            default:
                break;
        }
    }

    private void checkReview(boolean fromAccount) {
        String pluses = etPluses.getText().toString().trim();
        String minuses = etMinuses.getText().toString().trim();
        String position = etPosition.getText().toString().trim();
        if (fromAccount) {
            reviewPresenter.checkReview(pluses, minuses, position, null, null, true);
        } else {
            reviewPresenter.checkReview(pluses, minuses, position, companyDetail.getId(), companyDetail.getName(), false);
        }
    }

    @Override
    public void showWorkingDate() {
        ltEmploymentDate.setVisibility(View.VISIBLE);
        ltDismissalDate.setVisibility(View.GONE);
        ltInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void showWorkedDate() {
        ltEmploymentDate.setVisibility(View.VISIBLE);
        ltDismissalDate.setVisibility(View.VISIBLE);
        ltInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void showInterviewDate() {
        ltInterviewDate.setVisibility(View.VISIBLE);
        ltEmploymentDate.setVisibility(View.GONE);
        ltDismissalDate.setVisibility(View.GONE);
    }

    @Override
    public void successfulAddition() {
        Toast.makeText(getActivity().getApplicationContext(), R.string.review_added,
                Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showErrorAdding() {
        Toast.makeText(getActivity().getApplicationContext(), R.string.error_review_add,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setIsIndicatorRatingBar(boolean indicator) {
        setIsIndicator(indicator);
    }

    @Override
    public void clearRatingBar() {
        salary.setRating(0F);
        chief.setRating(0F);
        workplace.setRating(0F);
        career.setRating(0F);
        collective.setRating(0F);
        socialPackage.setRating(0F);
    }

    private void setIsIndicator(boolean indicator) {
        salary.setIsIndicator(indicator);
        chief.setIsIndicator(indicator);
        workplace.setIsIndicator(indicator);
        career.setIsIndicator(indicator);
        collective.setIsIndicator(indicator);
        socialPackage.setIsIndicator(indicator);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        public static final String TAG = "DatePickerDialog";

        public static final int EMPLOYMENT_DATE = 0;
        public static final int DISMISSAL_DATE = 1;
        public static final int INTERVIEW_DATE = 2;

        private int flag = -1;

        private EditText etDate;
        private Review review;

        Calendar c = Calendar.getInstance();

        public void setData(EditText date, Review review) {
            this.etDate = date;
            this.review = review;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            long date;

            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = c.getTimeInMillis();
            etDate.setText(Utils.getDate(date));

            switch (flag) {
                case EMPLOYMENT_DATE:
                    review.setEmploymentDate(date);
                    break;
                case DISMISSAL_DATE:
                    review.setDismissalDate(date);
                    break;
                case INTERVIEW_DATE:
                    review.setInterviewDate(date);
                    break;
                default:
                    break;
            }
        }
    }
}
