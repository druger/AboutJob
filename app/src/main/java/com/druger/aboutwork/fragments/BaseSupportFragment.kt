package com.druger.aboutwork.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.druger.aboutwork.R
import com.druger.aboutwork.enums.TypeMessage
import com.druger.aboutwork.interfaces.view.NetworkView

/**
 * Created by druger on 06.08.2017.
 */

abstract class BaseSupportFragment : Fragment(), NetworkView {
    protected var rootView: View? = null
    protected var mProgressBar: ProgressBar? = null
    protected var mLtError: ConstraintLayout? = null

    protected val actionBar: ActionBar?
        get() = (activity as AppCompatActivity).supportActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun showProgress(show: Boolean) {
        if (show) mProgressBar?.visibility = View.VISIBLE
        else mProgressBar?.visibility = View.INVISIBLE
    }

    override fun showMessage(@StringRes message: Int, typeMessage: TypeMessage) {
        when (typeMessage) {
            TypeMessage.SUCCESS -> showToast(message)
            TypeMessage.ERROR -> showToast(message)
            else -> showToast(R.string.network_error)
        }
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

    override fun showErrorScreen(show: Boolean) {
        if (show) mLtError?.visibility = View.VISIBLE
        else mLtError?.visibility = View.GONE
    }

    private fun showToast(@StringRes message: Int) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    protected fun setActionBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    protected fun replaceFragment(
        fragment: Fragment,
        @IdRes container: Int,
        addToBackStack: Boolean = false,
        view: View? = null,
        transitionName: String = ""
    ) {
        fragmentManager?.beginTransaction()?.apply {
            view?.let { addSharedElement(it, transitionName) }
            replace(container, fragment)
            if (addToBackStack) addToBackStack(null)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }
    }

    protected fun addFragment(fragment: Fragment, @IdRes container: Int, addToBackStack: Boolean = false) {
        fragmentManager?.beginTransaction()?.apply {
            add(container, fragment)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

    protected fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // TODO change on WorkManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }

    //    @Override
    //    public void onDestroy() {
    //        super.onDestroy();
    //        if (BuildConfig.DEBUG) {
    //            RefWatcher refWatcher = App.Companion.getRefWatcher(getActivity());
    //            refWatcher.watch(this);
    //        }
    //    }
}
