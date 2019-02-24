package com.druger.aboutwork.fragments


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.druger.aboutwork.R
import kotlinx.android.synthetic.main.fragment_company_description.*
import kotlinx.android.synthetic.main.toolbar.*

private const val DESCRIPTION = "description"

class CompanyDescriptionFragment : BaseSupportFragment() {

companion object {
    fun newInstance(description: String): CompanyDescriptionFragment {
        val bundle = Bundle()
        bundle.putString(DESCRIPTION, description)

        val fragment = CompanyDescriptionFragment()
        fragment.arguments = bundle
        return fragment
    }
}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_company_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDescription()
        setupToolbar()
    }

    private fun setDescription() {
        val description = arguments?.getString(DESCRIPTION) ?: getString(R.string.no_description)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            tvDescription.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
         else tvDescription.text = Html.fromHtml(description)
    }

    private fun setupToolbar() {
        setActionBar(toolbar)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.description)
    }
}
