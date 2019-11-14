package com.druger.aboutwork.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.fragments.AccountFragment
import com.druger.aboutwork.fragments.CompaniesFragment
import com.druger.aboutwork.fragments.MyReviewsFragment
import com.druger.aboutwork.interfaces.view.MainView
import com.druger.aboutwork.presenters.MainPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    internal lateinit var mainPresenter: MainPresenter

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        checkAuthUser()
        setupUI()
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    private fun checkAuthUser() {
        mainPresenter.checkAuthUser()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

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
            R.id.action_companies -> showCompanies()
            R.id.action_ratings -> mainPresenter.onClickMyReviews()
            R.id.action_setting -> showAccount()
        }
        return true
    }
}
