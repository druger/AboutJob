package com.druger.aboutwork.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.ReviewView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.ReviewPresenter;
import com.druger.aboutwork.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends MvpFragment implements ReviewView, View.OnClickListener {
    public static final String TAG = ReviewFragment.class.getSimpleName();

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

    private EditText etEmploymentDate;
    private EditText etDismissalDate;
    private EditText etInterviewDate;

    private RadioGroup radioGroup;
    private Button btnAdd;
    private Button btnCancel;

    private DatePickerFragment datePicker;

    private View view;
    private CompanyDetail detail;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review, container, false);

        detail = getActivity().getIntent().getExtras().getParcelable("companyDetail");
        if (detail != null) {
            String companyId = detail.getId();
            reviewPresenter.setCompanyId(companyId);
        }

        setupToolbar();
        setupUI();
        setupListeners();

        datePicker = new DatePickerFragment();

        reviewPresenter.setCompanyRating(salary, chief, workplace, career, collective, socialPackage);

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert detail != null;
        String companyName = detail.getName();
        if (companyName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(companyName);
        }
    }

    private void setupListeners() {
        radioGroup.setOnCheckedChangeListener(reviewPresenter);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etEmploymentDate.setOnClickListener(this);
        etDismissalDate.setOnClickListener(this);
        etInterviewDate.setOnClickListener(this);
    }

    private void setupUI() {
        etPluses = (TextInputEditText) view.findViewById(R.id.etPluses);
        etMinuses = (TextInputEditText) view.findViewById(R.id.etMinuses);
        etPosition = (TextInputEditText) view.findViewById(R.id.et_position);

        salary = (RatingBar) view.findViewById(R.id.ratingbar_salary);
        chief = (RatingBar) view.findViewById(R.id.ratingbar_chief);
        workplace = (RatingBar) view.findViewById(R.id.ratingbar_workplace);
        career = (RatingBar) view.findViewById(R.id.ratingbar_career);
        collective = (RatingBar) view.findViewById(R.id.ratingbar_collective);
        socialPackage = (RatingBar) view.findViewById(R.id.ratingbar_social_package);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

        etEmploymentDate = (EditText) view.findViewById(R.id.tvEmploymentDate);
        etDismissalDate = (EditText) view.findViewById(R.id.tvDismissalDate);
        etInterviewDate = (EditText) view.findViewById(R.id.tvInterviewDate);

        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        etEmploymentDate.setVisibility(View.GONE);
        etDismissalDate.setVisibility(View.GONE);
        etInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(view);
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
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
                if (checkReview()) {
                    Intent addedReview = new Intent(getActivity(), ReviewFragment.class);
                    addedReview.putExtra("addedReview", reviewPresenter.getReview());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, addedReview);
                    Toast.makeText(getActivity().getApplicationContext(), R.string.review_added,
                            Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_review_add,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnCancel:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.tvEmploymentDate:
                datePicker.flag = DatePickerFragment.EMPLOYMENT_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etEmploymentDate, reviewPresenter.getReview());
                break;
            case R.id.tvDismissalDate:
                datePicker.flag = DatePickerFragment.DISMISSAL_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etDismissalDate, reviewPresenter.getReview());
                break;
            case R.id.tvInterviewDate:
                datePicker.flag = DatePickerFragment.INTERVIEW_DATE;
                datePicker.show(getFragmentManager(), DatePickerFragment.TAG);
                datePicker.setData(etInterviewDate, reviewPresenter.getReview());
                break;
        }
    }

    private boolean checkReview() {
        String pluses = etPluses.getText().toString().trim();
        String minuses = etMinuses.getText().toString().trim();
        String position = etPosition.getText().toString().trim();

        return reviewPresenter.checkReview(pluses, minuses, position);
    }

    @Override
    public void showWorkingDate() {
        etEmploymentDate.setVisibility(View.VISIBLE);
        etDismissalDate.setVisibility(View.GONE);
        etInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void showWorkedDate() {
        etEmploymentDate.setVisibility(View.VISIBLE);
        etDismissalDate.setVisibility(View.VISIBLE);
        etInterviewDate.setVisibility(View.GONE);
    }

    @Override
    public void showInterviewDate() {
        etInterviewDate.setVisibility(View.VISIBLE);
        etEmploymentDate.setVisibility(View.GONE);
        etDismissalDate.setVisibility(View.GONE);
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
            }
        }
    }
}
