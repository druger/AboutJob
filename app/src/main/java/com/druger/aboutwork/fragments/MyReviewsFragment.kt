package com.druger.aboutwork.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.MyReviewAdapter
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.MyReviewsView
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.MyReviewsPresenter
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.recycler.RecyclerItemTouchHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.auth_layout.*
import kotlinx.android.synthetic.main.fragment_my_reviews.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import javax.inject.Inject

class MyReviewsFragment : BaseSupportFragment(), MyReviewsView, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @InjectPresenter
    lateinit var myReviewsPresenter: MyReviewsPresenter

    @Inject
    lateinit var analytics: Analytics

    private lateinit var reviewAdapter: MyReviewAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var simpleCallback: RecyclerItemTouchHelper

    private var actionMode: ActionMode? = null
    private val actionModeCallback = ActionModeCallback()

    private var bottomNavigation: BottomNavigationView? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_my_reviews, container, false)
        getData(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipe()
        setupUI()
        setupToolbar()
        setupRecycler()
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        bundle?.let { userId = it.getString(USER_ID, userId) }
        userId?.let { myReviewsPresenter.fetchReviews(it) } ?: showAuthAccess()
    }

    private fun showAuthAccess() {
        content.visibility = View.INVISIBLE
        ltAuthReviews.visibility = View.VISIBLE
        tvAuth.setText(R.string.reviews_login)
        btnLogin.setOnClickListener { showLoginActivity() }
    }

    private fun showLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        setActionBar(mToolbar)
        actionBar.setTitle(R.string.my_reviews)
    }

    private fun setupRecycler() {
        reviewAdapter = MyReviewAdapter()
        rvReviews.itemAnimator = DefaultItemAnimator()
        rvReviews.adapter = reviewAdapter

        reviewAdapter.setOnClickListener(object : OnItemClickListener<Review> {
            override fun onClick(review: Review, position: Int) {
                actionMode?.let { toggleSelection(position) } ?: showSelectedReview(review)
            }

            override fun onLongClick(position: Int): Boolean {
                if (actionMode == null) {
                    actionMode = (activity as MainActivity).startSupportActionMode(actionModeCallback)
                    simpleCallback.itemSwipe = false
                }
                toggleSelection(position)
                analytics.logEvent(Analytics.LONG_CLICK_MY_REVIEW)
                return true
            }
        })
    }

    private fun showSelectedReview(review: Review) {
        val reviewFragment = SelectedReviewFragment.newInstance(review.firebaseKey, true)
        replaceFragment(reviewFragment, R.id.main_container, true)
    }

    private fun setupUI() {
        bottomNavigation = activity?.findViewById(R.id.bottomNavigation)
        mProgressBar = progressBar
    }

    private fun initSwipe() {
        simpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        touchHelper = ItemTouchHelper(simpleCallback)
        touchHelper.attachToRecyclerView(rvReviews)
    }

    override fun onStop() {
        super.onStop()
        myReviewsPresenter.removeListeners()
    }

    /**
     * Toggle the selection state of an item.
     *
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private fun toggleSelection(position: Int) {
        reviewAdapter.toggleSelection(position)
        val count = reviewAdapter.getSelectedItemCount()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    override fun showReviews(reviews: List<Review>) {
        if (reviews.isEmpty()) {
            ltNoReviews.visibility = View.VISIBLE
            content.visibility = View.GONE
        } else {
            ltNoReviews.visibility = View.INVISIBLE
            content.visibility = View.VISIBLE
            reviewAdapter.addReviews(reviews)
        }
    }

    override fun showProgress(show: Boolean) {
        super.showProgress(show)
        if (show) content.visibility = View.INVISIBLE
        else content.visibility = View.VISIBLE
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is ReviewAdapter.ReviewVH) {
            val position = viewHolder.getAdapterPosition()
            val review = myReviewsPresenter.getReview(position)

            activity?.let {
                val snackbar = Snackbar
                    .make(it.findViewById(R.id.coordinator), R.string.review_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        reviewAdapter.addReview(review, position)
                        myReviewsPresenter.addReview(position, review)
                        rvReviews.scrollToPosition(position)
                    }
                showSnackbar(snackbar)
            }
            reviewAdapter.removeReview(position)
            myReviewsPresenter.removeReview(position)
            analytics.logEvent(Analytics.SWIPE_MY_REVIEW)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_ID, userId)
    }

    // TODO сделать класс статическим
    private inner class ActionModeCallback : ActionMode.Callback {

        private fun getDeletedReviews(): List<Review> {
            val deletedReviews = reviewAdapter.deletedReviews
            for (review in deletedReviews) {
                review.firebaseKey?.let { FirebaseHelper.removeReview(it) }
            }
            return deletedReviews
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.selected_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val i = item.itemId
            return if (i == R.id.menu_delete) {
                reviewAdapter.removeItems(reviewAdapter.getSelectedItems())
                val deletedReviews = getDeletedReviews()
                val snackbar = makeSnackbar(deletedReviews)
                snackbar?.let { showSnackbar(it) }
                mode.finish()
                true
            } else {
                false
            }
        }

        private fun makeSnackbar(deletedReviews: List<Review>): Snackbar? {
            return activity?.let {
                Snackbar
                    .make(it.findViewById(R.id.coordinator), R.string.review_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        myReviewsPresenter.addDeletedReviews(deletedReviews)
                        reviewAdapter.notifyDataSetChanged()
                        for (review in deletedReviews) {
                            myReviewsPresenter.addToFirebase(review)
                        }
                    }
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            reviewAdapter.clearSelection()
            actionMode = null
            simpleCallback.itemSwipe = true
        }
    }

    private fun showSnackbar(snackbar: Snackbar) {
        val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
        bottomNavigation?.height?.let { params.setMargins(0, 0, 0, it) }
        snackbar.view.layoutParams = params
        snackbar.show()
    }

    companion object {

        private const val USER_ID = "userId"

        fun newInstance(userId: String?): MyReviewsFragment {
            val myReviews = MyReviewsFragment()
            val bundle = Bundle()
            bundle.putString(USER_ID, userId)
            myReviews.arguments = bundle
            return myReviews
        }
    }
}
