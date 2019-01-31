package com.druger.aboutwork.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.ReviewView;
import com.druger.aboutwork.model.City;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.Vacancy;
import com.druger.aboutwork.presenters.ReviewPresenter;
import com.druger.aboutwork.utils.Utils;

import java.util.Calendar;
import java.util.List;

public abstract class ReviewFragment extends BaseFragment implements ReviewView, View.OnClickListener,
        AdapterView.OnItemSelectedListener {

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
    private AutoCompleteTextView etPosition;
    private AutoCompleteTextView etCity;

    private TextInputLayout ltEmploymentDate;
    private TextInputLayout ltDismissalDate;
    private TextInputLayout ltInterviewDate;
    private EditText etEmploymentDate;
    private EditText etDismissalDate;
    private EditText etInterviewDate;

    protected Spinner spinnerWorkStatus;
    private ImageView ivClose;
    private ImageView ivDone;
    protected TextView tvTitle;

    private DatePickerFragment datePicker;
    private Review review;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @ProvidePresenter
    ReviewPresenter provideReviewPresenter() {
        return App.Companion.getAppComponent().getReviewPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getBundles();
        rootView = inflater.inflate(R.layout.fragment_review, container, false);
        setupToolbar();
        setupUI();
        setDateVisibility();
        setupWorkStatus();
        setupListeners();
        setupCompanyRating();
        return rootView;
    }

    protected abstract void setupCompanyRating();

    private void setupWorkStatus() {
        spinnerWorkStatus = bindView(R.id.spinnerStatus);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.work_status, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerWorkStatus.setAdapter(adapter);
    }

    protected abstract void getBundles();

    protected void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        ivDone = bindView(R.id.ivDone);
        ivClose = bindView(R.id.ivClose);
        tvTitle = bindView(R.id.tvTitle);

        ivDone.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    private void setupListeners() {
        etEmploymentDate.setOnClickListener(this);
        etDismissalDate.setOnClickListener(this);
        etInterviewDate.setOnClickListener(this);
        cityChanges();
        positionChanges();
        spinnerWorkStatus.setOnItemSelectedListener(this);
        setupRatingChanges();
    }

    private void setupRatingChanges() {
        salary.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setSalary(rating));
        chief.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setChief(rating));
        workplace.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setWorkplace(rating));
        career.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setCareer(rating));
        collective.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setCollective(rating));
        socialPackage.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> reviewPresenter.setSocialPackage(rating));
    }

    private void positionChanges() {
        etPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reviewPresenter.getVacancies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void cityChanges() {
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reviewPresenter.getCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupUI() {
        datePicker = new DatePickerFragment();

        etPluses = bindView(R.id.etPluses);
        etMinuses = bindView(R.id.etMinuses);
        etPosition = bindView(R.id.etPosition);
        etCity = bindView(R.id.etCity);

        salary = bindView(R.id.ratingbar_salary);
        chief = bindView(R.id.ratingbar_chief);
        workplace = bindView(R.id.ratingbar_workplace);
        career = bindView(R.id.ratingbar_career);
        collective = bindView(R.id.ratingbar_collective);
        socialPackage = bindView(R.id.ratingbar_social_package);

        ltEmploymentDate = bindView(R.id.ltEmploymentDate);
        ltDismissalDate = bindView(R.id.ltDismissalDate);
        ltInterviewDate = bindView(R.id.ltInterviewDate);
        etEmploymentDate = bindView(R.id.etEmploymentDate);
        etDismissalDate = bindView(R.id.etDismissalDate);
        etInterviewDate = bindView(R.id.etInterviewDate);
    }

    private void setDateVisibility() {
        ltEmploymentDate.setVisibility(View.GONE);
        ltDismissalDate.setVisibility(View.GONE);
        ltInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(rootView);
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
            case R.id.ivDone:
                doneClick();
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
            case R.id.ivClose:
                getFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
    }

    private void doneClick() {
        review = reviewPresenter.getReview();

        review.setPluses(etPluses.getText().toString().trim());
        review.setMinuses(etMinuses.getText().toString().trim());
        review.setPosition(etPosition.getText().toString().trim());
        review.setCity(etCity.getText().toString());

        reviewPresenter.doneClick();
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

    @Override
    public void showCities(List<City> cities) {
        showSuggestions(cities, etCity);
    }

    @Override
    public void showVacancies(List<Vacancy> vacancies) {
        showSuggestions(vacancies, etPosition);
    }

    @Override
    public void successfulEditing() {
        Toast.makeText(getActivity().getApplicationContext(), R.string.review_edited,
                Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showErrorEditing() {
        Toast.makeText(getActivity().getApplicationContext(), R.string.error_review_edit,
                Toast.LENGTH_SHORT).show();
    }

    private void showSuggestions(List<?> items, AutoCompleteTextView view) {
        ArrayAdapter<?> arrayAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_dropdown_item_1line, items);
        view.setAdapter(arrayAdapter);
    }

    private void setIsIndicator(boolean indicator) {
        salary.setIsIndicator(indicator);
        chief.setIsIndicator(indicator);
        workplace.setIsIndicator(indicator);
        career.setIsIndicator(indicator);
        collective.setIsIndicator(indicator);
        socialPackage.setIsIndicator(indicator);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                reviewPresenter.onSelectedWorkingStatus(position);
                break;
            case 1:
                reviewPresenter.onSelectedWorkedStatus(position);
                break;
            case 2:
                reviewPresenter.onSelectedInterviewStatus(position);
                break;
            default: break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setIsIndicator(true);
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
            etDate.setText(Utils.INSTANCE.getDate(date));

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
