package com.druger.aboutwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.druger.aboutwork.databinding.MarkCompanyInfoBinding

class DetailMarkCompanyDialog : DialogFragment() {

    private var _binding: MarkCompanyInfoBinding? = null
    private val binding get() = _binding!!

    var salary: Float = 0.toFloat()
    var chief: Float = 0.toFloat()
    var workplace: Float = 0.toFloat()
    var career: Float = 0.toFloat()
    var collective: Float = 0.toFloat()
    var socialPackage: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
        salary = arguments?.getFloat(SALARY_KEY) ?: 0.0f
        chief = arguments?.getFloat(CHIEF_KEY) ?: 0.0f
        workplace = arguments?.getFloat(WORKPLACE_KEY) ?: 0.0f
        career = arguments?.getFloat(CAREER_KEY) ?: 0.0f
        collective = arguments?.getFloat(COLLECTIVE_KEY) ?: 0.0f
        socialPackage = arguments?.getFloat(SOCIAL_PACKAGE_KEY) ?: 0.0f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MarkCompanyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            rbSalary.rating = salary
            rbChief.rating = chief
            rbWorkplace.rating = workplace
            rbCareer.rating = career
            rbCollective.rating = collective
            rbSocialPackage.rating = socialPackage
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val SALARY_KEY = "salary"
        private const val CHIEF_KEY = "chief"
        private const val WORKPLACE_KEY = "workplace"
        private const val CAREER_KEY = "career"
        private const val COLLECTIVE_KEY = "collective"
        private const val SOCIAL_PACKAGE_KEY = "socialPackage"

        fun newInstance(
            salary: Float,
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