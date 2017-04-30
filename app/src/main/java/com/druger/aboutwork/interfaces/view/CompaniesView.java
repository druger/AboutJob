package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;

import java.util.List;

/**
 * Created by druger on 01.05.2017.
 */

public interface CompaniesView extends MvpView {

    void showCompanies(List<Company> companies);

    void showCompanyDetail(CompanyDetail companyDetail);
}
