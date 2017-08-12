package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.Company;

import java.util.List;

/**
 * Created by druger on 01.05.2017.
 */

public interface CompaniesView extends MvpView, NetworkView {

    void showCompanies(List<Company> companies);
}
