package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.druger.aboutwork.Const.Bundles.NAME
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.druger.aboutwork.presenters.ChangeNamePresenter
import com.druger.aboutwork.utils.Utils
import kotlinx.android.synthetic.main.fragment_change_name.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter

class ChangeNameFragment : BaseSupportFragment(), ChangeNameView {

    @InjectPresenter
    lateinit var presenter: ChangeNamePresenter

    private var name: String? = null

    private var inputMode: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_change_name, container, false)
        setInputMode()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        mProgressBar = progressBar
        showName()
        btnChangeName.setOnClickListener { presenter.changeName(etName.text.toString().trim()) }
        Utils.showKeyboard(requireContext())
    }

    private fun setInputMode() {
        activity?.window?.let { window ->
            window.attributes?.softInputMode?.let { mode ->
                inputMode = mode
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            }
        }
    }


    private fun setupToolbar() {
        mToolbar = toolbar
        setActionBar(toolbar)
        actionBar?.setTitle(R.string.change_name)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showName() {
        name = arguments?.getString(NAME)
        etName.setText(name)
        setCursorToEnd()
    }

    private fun setCursorToEnd() {
        etName.requestFocus()
        etName.setSelection(etName.text.length)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.hideKeyboard(requireContext(), etName)
        activity?.window?.setSoftInputMode(inputMode)
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

    companion object {
        fun newInstance(name: String?): ChangeNameFragment {

            val args = Bundle()
            args.putString(NAME, name)

            val fragment = ChangeNameFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
