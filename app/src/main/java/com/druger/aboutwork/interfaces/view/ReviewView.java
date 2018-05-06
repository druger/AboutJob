package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.City;
import com.druger.aboutwork.model.Vacancy;

import java.util.List;

/**
 * Created by druger on 07.05.2017.
 */

public interface ReviewView extends MvpView {

    void showWorkingDate();

    void showWorkedDate();

    void showInterviewDate();

    void successfulAddition();

    void showErrorAdding();

    void setIsIndicatorRatingBar(boolean indicator);

    void clearRatingBar();

    void showCities(List<City> cities);

    void showRating(boolean show);

    void showVacancies(List<Vacancy> vacancies);
}
