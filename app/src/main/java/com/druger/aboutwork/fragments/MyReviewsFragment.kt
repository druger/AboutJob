package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.MyReviewAdapter
import com.druger.aboutwork.adapters.ReviewAdapter
import com.druger.aboutwork.databinding.FragmentMyReviewsBinding
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.enums.Screen
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.MyReviewsView
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.MyReviewsPresenter
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.recycler.RecyclerItemTouchHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyReviewsFragment : BaseSupportFragment(), MyReviewsView,
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @Inject
    lateinit var myReviewsPresenter: MyReviewsPresenter

    private var _binding: FragmentMyReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: MyReviewAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var simpleCallback: RecyclerItemTouchHelper

    private var actionMode: ActionMode? = null
    private val actionModeCallback = ActionModeCallback()

    private var bottomNavigation: BottomNavigationView? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReviewsBinding.inflate(inflater, container, false)
        getData(savedInstanceState)
        (activity as MainActivity).showBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupToolbar()
        initSwipe()
        setupRecycler()
        fetchReviews()
        binding.ltError.btnRetry.setOnClickListener {
            showErrorScreen(false)
            fetchReviews()
        }
        binding.ltNoReviews.btnFind.setOnClickListener { goToSearch() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun goToSearch() {
        (activity as MainActivity).setBottomItemId(R.id.action_search)
    }

    private fun fetchReviews() {
        if (isInternetAvailable(requireContext())) {
            userId?.let { myReviewsPresenter.fetchReviews(it) } ?: showAuthAccess()
        } else showErrorScreen(true)
    }

    private fun getData(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        bundle?.let { userId = it.getString(USER_ID, userId) }
    }

    private fun showAuthAccess() {
        addFragment(
            AuthFragment.newInstance(getString(R.string.reviews_login), Screen.MY_REVIEWS.name),
            R.id.main_container, false
        )
    }

    private fun setupToolbar() {
        actionBar?.setDisplayShowTitleEnabled(true)
        setActionBar(binding.toolbar.toolbar)
        actionBar?.setTitle(R.string.my_reviews)
    }

    private fun setupRecycler() {
        reviewAdapter = MyReviewAdapter()
        with(binding.rvReviews) {
            itemAnimator = DefaultItemAnimator()
            adapter = reviewAdapter
        }

        reviewAdapter.setOnClickListener(object : OnItemClickListener<Review> {
            override fun onClick(item: Review, position: Int) {
                actionMode?.let { toggleSelection(position) } ?: showSelectedReview(item)
            }

            override fun onLongClick(item: Review, position: Int): Boolean {
                if (actionMode == null) {
                    actionMode =
                        (activity as MainActivity).startSupportActionMode(actionModeCallback)
                    simpleCallback.itemSwipe = false
                }
                toggleSelection(position)
                myReviewsPresenter.logEvent(Analytics.LONG_CLICK_MY_REVIEW)
                return true
            }
        })
    }

    private fun showSelectedReview(review: Review) {
        review.firebaseKey?.let {
            val reviewFragment = SelectedReviewFragment.newInstance(it, true)
            replaceFragment(
                reviewFragment,
                R.id.main_container,
                true,
                binding.rvReviews,
                "detail_transform"
            )
        }
    }

    private fun setupUI() {
        bottomNavigation = activity?.findViewById(R.id.bottomNavigation)
        mLtError = binding.ltError.root
    }

    private fun initSwipe() {
        simpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        touchHelper = ItemTouchHelper(simpleCallback)
        touchHelper.attachToRecyclerView(binding.rvReviews)
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
        with(binding) {
            groupReviews.isVisible = true
            ltNoReviews.root.isVisible = false
            tvCountReviews.text =
                resources.getQuantityString(R.plurals.reviews, reviews.size, reviews.size)
        }
        reviewAdapter.addReviews(reviews)
    }

    override fun showEmptyReviews() {
        with(binding) {
            groupReviews.isVisible = false
            ltNoReviews.root.isVisible = true
            ltNoReviews.tvNoReviews.text = getString(R.string.no_my_reviews)
            ltNoReviews.btnFind.isVisible = true
        }
    }

    override fun updateAdapter() {
        reviewAdapter.notifyDataSetChanged()
    }

    override fun showProgress(show: Boolean) {
        with(binding) {
            if (show) {
                reviewPlaceholder.isVisible = true
                reviewPlaceholder.startShimmer()
            } else {
                reviewPlaceholder.stopShimmer()
                reviewPlaceholder.isVisible = false
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is ReviewAdapter.ReviewVH) {
            val position = viewHolder.getAdapterPosition()
            val review = myReviewsPresenter.getReview(position)

            activity?.let {
                val snackbar = Snackbar
                    .make(
                        it.findViewById(R.id.coordinator),
                        R.string.review_deleted,
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(R.string.undo) {
                        reviewAdapter.addReview(review, position)
                        myReviewsPresenter.addReview(position, review)
                        binding.rvReviews.scrollToPosition(position)
                    }
                showSnackbar(snackbar)
            }
            reviewAdapter.removeReview(position)
            myReviewsPresenter.removeReview(position)
            myReviewsPresenter.logEvent(Analytics.SWIPE_MY_REVIEW)
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
                    .make(
                        it.findViewById(R.id.coordinator),
                        R.string.review_deleted,
                        Snackbar.LENGTH_LONG
                    )
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
