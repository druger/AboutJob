package com.druger.aboutwork.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.Utils;
import com.druger.aboutwork.db.DBHelper;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.ui.dialog.DatePickerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.RefWatcher;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener {
    public static final String TAG = ReviewFragment.class.getSimpleName();

    private FirebaseUser user;
    private Review review;
    private MarkCompany mark;

    private int companyId;
    private long date;
    private int status = -1;

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

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Intent intent = getActivity().getIntent();
        companyId = intent.getIntExtra("id", 0);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(intent.getStringExtra("name"));

        user = FirebaseAuth.getInstance().getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        date = calendar.getTimeInMillis();

        review = new Review(companyId, user.getUid(), user.getDisplayName(), date);

        etPluses = (TextInputEditText) view.findViewById(R.id.et_pluses);
        etMinuses = (TextInputEditText) view.findViewById(R.id.et_minuses);
        etPosition = (TextInputEditText) view.findViewById(R.id.et_position);

        salary = (RatingBar) view.findViewById(R.id.ratingbar_salary);
        chief = (RatingBar) view.findViewById(R.id.ratingbar_chief);
        workplace = (RatingBar) view.findViewById(R.id.ratingbar_workplace);
        career = (RatingBar) view.findViewById(R.id.ratingbar_career);
        collective = (RatingBar) view.findViewById(R.id.ratingbar_collective);
        socialPackage = (RatingBar) view.findViewById(R.id.ratingbar_social_package);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);

        etEmploymentDate = (EditText) view.findViewById(R.id.employment_date);
        etDismissalDate = (EditText) view.findViewById(R.id.dismissal_date);
        etInterviewDate = (EditText) view.findViewById(R.id.interview_date);

        Button add = (Button) view.findViewById(R.id.btn_add);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        etEmploymentDate.setVisibility(View.GONE);
        etDismissalDate.setVisibility(View.GONE);
        etInterviewDate.setVisibility(View.GONE);

        setCompanyRating();
        setDatePickerDialog();

        return view;
    }

    private void setCompanyRating() {

        mark = new MarkCompany(user.getUid(), companyId);

        salary.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setSalary(rating);
            }
        });

        chief.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setChief(rating);
            }
        });

        workplace.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setWorkplace(rating);
            }
        });

        career.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setCareer(rating);
            }
        });

        collective.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setCollective(rating);
            }
        });

        socialPackage.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setSocialPackage(rating);
            }
        });

        review.setMarkCompany(mark);
    }

    private void setDatePickerDialog() {
        etEmploymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DialogFragment datePicker = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etEmploymentDate.setText(Utils.getDate(c.getTimeInMillis()));
                        review.setEmploymentDate(c.getTimeInMillis());
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        super.onCancel(dialog);
                        etEmploymentDate.setText(null);
                    }
                };
                datePicker.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        etDismissalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DialogFragment datePicker = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDismissalDate.setText(Utils.getDate(c.getTimeInMillis()));
                        review.setDismissalDate(c.getTimeInMillis());
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        super.onCancel(dialog);
                        etDismissalDate.setText(null);
                    }
                };
                datePicker.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        etInterviewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DialogFragment datePicker = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etInterviewDate.setText(Utils.getDate(c.getTimeInMillis()));
                        review.setInterviewDate(c.getTimeInMillis());
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        super.onCancel(dialog);
                        etInterviewDate.setText(null);
                    }
                };
                datePicker.show(getFragmentManager(), "DatePickerDialog");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        View radioButton;

        switch (checkedId) {
            case R.id.radio_working:
                etEmploymentDate.setVisibility(View.VISIBLE);
                etDismissalDate.setVisibility(View.GONE);
                etInterviewDate.setVisibility(View.GONE);

                radioButton = group.findViewById(R.id.radio_working);
                status = group.indexOfChild(radioButton);
                break;
            case R.id.radio_worked:
                etEmploymentDate.setVisibility(View.VISIBLE);
                etDismissalDate.setVisibility(View.VISIBLE);
                etInterviewDate.setVisibility(View.GONE);

                radioButton = group.findViewById(R.id.radio_worked);
                status = group.indexOfChild(radioButton);
                break;
            case R.id.radio_interview:
                etInterviewDate.setVisibility(View.VISIBLE);
                etEmploymentDate.setVisibility(View.GONE);
                etDismissalDate.setVisibility(View.GONE);

                radioButton = group.findViewById(R.id.radio_interview);
                status = group.indexOfChild(radioButton);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                if (checkReview()) {
                    DBHelper.addReview(review);
                    Log.d(TAG, "Review list size = " + DBHelper.getReviews().size());
                    Toast.makeText(getActivity().getApplicationContext(), R.string.review_added,
                            Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_review_add,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                getFragmentManager().popBackStackImmediate();
                break;
        }
    }

    private boolean checkReview() {
        String pluses = etPluses.getText().toString().trim();
        String minuses = etMinuses.getText().toString().trim();
        String position = etPosition.getText().toString().trim();

        if (!TextUtils.isEmpty(pluses) && !TextUtils.isEmpty(minuses) && status > -1
                && mark.getRating() != 0) {
            review.setPluses(pluses);
            review.setMinuses(minuses);
            review.setStatus(status);

            if (!TextUtils.isEmpty(position)) {
                review.setPosition(position);
            }
            return true;
        }
        return false;
    }
}
