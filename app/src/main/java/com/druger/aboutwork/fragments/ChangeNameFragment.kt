package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.druger.aboutwork.Const.Bundles.NAME
import com.druger.aboutwork.R
import com.druger.aboutwork.databinding.FragmentChangeNameBinding
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.viewmodels.ChangeNameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNameFragment : BaseSupportFragment() {

    private val viewModel: ChangeNameViewModel by viewModels()

    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!

    private var name: String? = null

    private var inputMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLoading()
        observeSuccess()
        observeError()
    }

    private fun observeError() {
        viewModel.errorState.observe(this) {
            showErrorMessage()
        }
    }

    private fun observeSuccess() {
        viewModel.successState.observe(this) {
            showSuccessMessage()
        }
    }

    private fun observeLoading() {
        viewModel.progressState.observe(this) {
            showProgress(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        setInputMode()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        mProgressBar = binding.progressBar
        showName()
        binding.btnChangeName.setOnClickListener {
            viewModel.changeName(
                binding.etName.text.toString().trim()
            )
        }
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
        setActionBar(binding.toolbar.toolbar)
        actionBar?.setTitle(R.string.change_name)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showName() {
        name = arguments?.getString(NAME)
        binding.etName.setText(name)
        setCursorToEnd()
    }

    private fun setCursorToEnd() {
        binding.apply {
            etName.requestFocus()
            etName.setSelection(etName.text.length)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.hideKeyboard(requireContext(), binding.etName)
        activity?.window?.setSoftInputMode(inputMode)
        actionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = null
    }

    private fun showSuccessMessage() {
        Toast.makeText(activity, R.string.profile_updated, Toast.LENGTH_SHORT).show()
        fragmentManager?.popBackStackImmediate()
    }

    private fun showErrorMessage() =
        Toast.makeText(activity, R.string.updating_error, Toast.LENGTH_SHORT).show()

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        binding.apply {
            if (show) {
                etName.visibility = View.INVISIBLE
                btnChangeName.visibility = View.INVISIBLE
            } else {
                etName.visibility = View.VISIBLE
                btnChangeName.visibility = View.VISIBLE
            }
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
