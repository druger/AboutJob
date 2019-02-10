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

    public ReviewFragment() {
        // Required empty public constructor
    }

    @ProvidePresenter
    ReviewPresenter provideReviewPresenter() {
        return App.Companion.getAppComponent().getReviewPresenter();
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
}
