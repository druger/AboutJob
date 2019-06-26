package com.druger.aboutwork.fragments


import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.druger.aboutwork.Const.Bundles.NAME
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.druger.aboutwork.presenters.ChangeNamePresenter

class ChangeNameFragment : BaseSupportFragment(), ChangeNameView {

    private lateinit var etName: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var btnChangeName: Button
    private lateinit var progressBar: ProgressBar
    @InjectPresenter
    lateinit var presenter: ChangeNamePresenter

    private var name: String? = null

    fun newInstance(name: String): ChangeNameFragment {

        val args = Bundle()
        args.putString(NAME, name)

        val fragment = ChangeNameFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_change_name, container, false)
        toolbar = bindView(R.id.toolbar)
        progressBar = bindView(R.id.progressBar)
        etName = bindView(R.id.etName)
        btnChangeName = bindView(R.id.btnChangeName)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        mProgressBar = progressBar
        showName()
        btnChangeName.setOnClickListener { presenter.changeName(etName.text.toString().trim()) }
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        setActionBar(toolbar)
        actionBar.setTitle(R.string.change_name)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun showName() {
        name = arguments?.getString(NAME)
        etName.setText(name)
    }

    override fun showSuccessMessage() {
        Toast.makeText(activity, R.string.profile_updated, Toast.LENGTH_SHORT).show()
        fragmentManager?.popBackStackImmediate()
    }

    override fun showErrorMessage() =
            Toast.makeText(activity, R.string.updating_error, Toast.LENGTH_SHORT).show()

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) {
            etName.visibility = View.INVISIBLE
            btnChangeName.visibility = View.INVISIBLE
        } else {
            etName.visibility = View.VISIBLE
            btnChangeName.visibility = View.VISIBLE
        }
    }
}
