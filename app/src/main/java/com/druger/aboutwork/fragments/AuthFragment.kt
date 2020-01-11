package com.druger.aboutwork.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.activities.MainActivity
import kotlinx.android.synthetic.main.auth_layout.*

class AuthFragment : Fragment(R.layout.auth_layout) {

    private var nextScreen: String? = null
    private var companyId: String? = null
    private var reviewId: String? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nextScreen = arguments?.getString(NEXT_SCREEN)
        companyId = arguments?.getString(COMPANY_ID)
        reviewId = arguments?.getString(REVIEW_ID)
        message = arguments?.getString(MESSAGE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAuth.text = arguments?.getString(AUTH_TEXT)
        btnLogin.setOnClickListener { showLoginActivity() }
    }

    private fun showLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra(MainActivity.NEXT_SCREEN, nextScreen)
            putExtra(MainActivity.COMPANY_ID, companyId)
            putExtra(MainActivity.REVIEW_ID, reviewId)
            putExtra(MainActivity.MESSAGE, message)
        }
        startActivity(intent)
    }

    companion object {
        private const val AUTH_TEXT = "auth_text"
        private const val NEXT_SCREEN = "next_screen"
        private const val COMPANY_ID = "company_id"
        private const val REVIEW_ID = "review_id"
        private const val MESSAGE = "message"

        fun newInstance(authText: String,
                        nextScreen: String,
                        companyId: String? = null,
                        reviewId: String? = null,
                        message: String? = null
        ): AuthFragment {
            return AuthFragment().apply {
                arguments = Bundle().apply {
                    putString(AUTH_TEXT, authText)
                    putString(NEXT_SCREEN, nextScreen)
                    putString(COMPANY_ID, companyId)
                    putString(REVIEW_ID, reviewId)
                    putString(MESSAGE, message)
                }
            }
        }
    }
}