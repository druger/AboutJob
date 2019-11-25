package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.Const.Bundles.USER_ID
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.MyReviewAdapter
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.UserReviews
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.UserReviewsPresenter
import kotlinx.android.synthetic.main.fragment_user_reviews.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter

class UserReviewsFragment : BaseSupportFragment(), UserReviews {

    @InjectPresenter
    lateinit var reviewsPresenter: UserReviewsPresenter

    private var reviewAdapter: MyReviewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_user_reviews, container, false)
        arguments?.getString(USER_ID)?.let { reviewsPresenter.fetchReviews(it) }
        arguments?.getString(USER_ID)?.let { reviewsPresenter.getUserName(it) }
        (activity as MainActivity).hideBottomNavigation()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecycler()
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        setActionBar(mToolbar)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecycler() {
        reviewAdapter = MyReviewAdapter()
        rvReviews.itemAnimator = DefaultItemAnimator()
        rvReviews.adapter = reviewAdapter

        reviewAdapter?.setOnClickListener(object : OnItemClickListener<Review> {
            override fun onClick(review: Review, position: Int) {
                review.firebaseKey?.let {
                    val reviewFragment = SelectedReviewFragment.newInstance(it, false)

                    fragmentManager?.beginTransaction()?.apply {
                        replace(R.id.main_container, reviewFragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            }

            override fun onLongClick(position: Int): Boolean {
                return false
            }
        })
    }

    override fun notifyDataSetChanged() {
        reviewAdapter?.notifyDataSetChanged()
    }

    override fun showReviews(reviews: List<Review>) {
        reviewAdapter?.addReviews(reviews)
    }

    override fun showName(name: String) {
        tvName.text = name
        actionBar.title = name
    }

    override fun onStop() {
        super.onStop()
        reviewsPresenter.removeListeners()
    }

    companion object {

        fun newInstance(userId: String): UserReviewsFragment {

            val args = Bundle()

            val fragment = UserReviewsFragment()
            args.putString(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }
}
