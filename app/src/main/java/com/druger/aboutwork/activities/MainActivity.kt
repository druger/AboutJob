package com.druger.aboutwork.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.enums.Screen
import com.druger.aboutwork.fragments.*
import com.druger.aboutwork.interfaces.view.MainView
import com.druger.aboutwork.presenters.MainPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    internal lateinit var mainPresenter: MainPresenter

    private var fragment: Fragment? = null
    private var nextScreen: String? = null
    private var companyId: String? = null
    private var reviewId: String? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        checkAuthUser()
        setupUI()
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        if (savedInstanceState == null) checkNextScreen()
    }

    private fun checkNextScreen() {
        getExtras()

        nextScreen?.let { screen ->
            when(Screen.valueOf(screen)) {
                Screen.COMPANY_DETAIL -> companyId?.let {
                    replaceFragment(CompanyDetailFragment.newInstance(it))
                }
                Screen.REVIEW -> {
                    reviewId?.let {
                        replaceFragment(SelectedReviewFragment.newInstance(it, false, message))
                    }
                }
                Screen.MY_REVIEWS -> { bottomNavigation.selectedItemId = R.id.action_my_reviews }
                Screen.SETTINGS -> { bottomNavigation.selectedItemId = R.id.action_setting }
            }
        }
    }

    private fun getExtras() {
        nextScreen = intent.getStringExtra(NEXT_SCREEN)
        companyId = intent.getStringExtra(COMPANY_ID)
        reviewId = intent.getStringExtra(REVIEW_ID)
        message = intent.getStringExtra(MESSAGE)
    }

    private fun checkAuthUser() {
        mainPresenter.checkAuthUser()
    }

    fun setupSearchToolbar() {
        actionBar?.setTitle(R.string.search)
        ivSearch.visibility = View.VISIBLE
        ivSearch.setOnClickListener {
            replaceFragment(SearchFragment(), R.id.main_container, true)
        }
    }

    fun hideSearchIcon() {
        ivSearch.visibility = View.GONE
    }

    fun showSearchIcon() {
        ivSearch.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbarLt.visibility = View.GONE
    }

    fun showToolbar() {
        toolbarLt.visibility = View.VISIBLE
    }

    fun showEditIcon() {
        ivEdit.visibility = View.VISIBLE
    }

    fun hideEditIcon() {
        ivEdit.visibility = View.GONE
    }

    fun showCloseIcon() {
        ivClose.visibility = View.VISIBLE
    }

    fun hideCloseIcon() {
        ivClose.visibility = View.GONE
    }

    fun showDoneIcon() {
        ivDone.visibility = View.VISIBLE
    }

    fun hideDoneIcon() {
        ivDone.visibility = View.GONE
    }

    fun setToolbarTitle(@StringRes resId: Int) {
        tvTitle.setText(resId)
    }

    fun hideToolbarTitle() {
        tvTitle.visibility = View.GONE
    }

    fun showToolbarTitle() {
        tvTitle.visibility = View.VISIBLE
    }

    fun getEditImageView(): ImageView = ivEdit

    fun getDoneImageView(): ImageView = ivDone

    fun getCloseImageView(): ImageView = ivClose

    override fun onDestroy() {
        super.onDestroy()
        //        initRefWatcher();
        removeAuthListener()
    }

    private fun removeAuthListener() {
        mainPresenter.removeAuthListener()
    }

    private fun initRefWatcher() {
        val refWatcher = App.getRefWatcher(this)
        refWatcher.watch(this)
    }

    private fun setupUI() {
        fragment = supportFragmentManager.findFragmentById(R.id.main_container)
        if (fragment == null) {
            fragment = CompaniesFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment as CompaniesFragment).commit()
        }
    }

    private fun showCompanies() {
        fragment = CompaniesFragment()
        replaceFragment(fragment)
    }

    override fun showMyReviews(userId: String?) {
        fragment = MyReviewsFragment.newInstance(userId)
        replaceFragment(fragment)
    }

    private fun showAccount() {
        fragment = AccountFragment()
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment?.let { transaction.replace(R.id.main_container, it) }
        transaction.commit()
    }

    private fun replaceFragment(
        fragment: Fragment,
        @IdRes container: Int,
        addToBackStack: Boolean = false,
        view: View? = null,
        transitionName: String = ""
    ) {
        supportFragmentManager.beginTransaction().apply {
            view?.let { addSharedElement(it, transitionName) }
            replace(container, fragment)
            if (addToBackStack) addToBackStack(null)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }
    }

    fun hideBottomNavigation() {
        bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> showCompanies()
            R.id.action_my_reviews -> mainPresenter.onClickMyReviews()
            R.id.action_setting -> showAccount()
        }
        return true
    }

    companion object {
        const val NEXT_SCREEN = "next_screen"
        const val COMPANY_ID = "company_id"
        const val REVIEW_ID = "review_id"
        const val MESSAGE = "message"
    }
}
