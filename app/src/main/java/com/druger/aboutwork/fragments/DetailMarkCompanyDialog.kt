package com.druger.aboutwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.druger.aboutwork.R
import kotlinx.android.synthetic.main.mark_company_info.*

class DetailMarkCompanyDialog : DialogFragment() {

    var salary: Float = 0.toFloat()
    var chief: Float = 0.toFloat()
    var workplace: Float = 0.toFloat()
    var career: Float = 0.toFloat()
    var collective: Float = 0.toFloat()
    var socialPackage: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        salary = arguments?.getFloat(SALARY_KEY) ?: 0.0f
        chief = arguments?.getFloat(CHIEF_KEY) ?: 0.0f
        workplace = arguments?.getFloat(WORKPLACE_KEY) ?: 0.0f
        career = arguments?.getFloat(CAREER_KEY) ?: 0.0f
        collective = arguments?.getFloat(COLLECTIVE_KEY) ?: 0.0f
        socialPackage = arguments?.getFloat(SOCIAL_PACKAGE_KEY) ?: 0.0f
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mark_company_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rbSalary.rating = salary
        rbChief.rating = chief
        rbWorkplace.rating = workplace
        rbCareer.rating = career
        rbCollective.rating = collective
        rbSocialPackage.rating = socialPackage
    }

    companion object {
        private const val SALARY_KEY = "salary"
        private const val CHIEF_KEY = "chief"
        private const val WORKPLACE_KEY = "workplace"
        private const val CAREER_KEY = "career"
        private const val COLLECTIVE_KEY = "collective"
        private const val SOCIAL_PACKAGE_KEY = "socialPackage"

        fun newInstance(salary: Float,
                        chief: Float,
                        workplace: Float,
                        career: Float,
                        collective: Float,
                        socialPackage: Float
        ): DetailMarkCompanyDialog = DetailMarkCompanyDialog().apply {
            arguments = Bundle().apply {
                putFloat(SALARY_KEY, salary)
                putFloat(CHIEF_KEY, chief)
                putFloat(WORKPLACE_KEY, workplace)
                putFloat(CAREER_KEY, career)
                putFloat(COLLECTIVE_KEY, collective)
                putFloat(SOCIAL_PACKAGE_KEY, socialPackage)
            }
        }
    }
}