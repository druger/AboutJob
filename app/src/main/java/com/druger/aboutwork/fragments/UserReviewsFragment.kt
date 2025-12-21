package com.druger.aboutwork.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.Const.Bundles.USER_ID
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.MyReviewAdapter
import com.druger.aboutwork.databinding.FragmentUserReviewsBinding
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.viewmodels.UserReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserReviewsFragment : BaseSupportFragment() {

    private val viewModel: UserReviewsViewModel by viewModels()

    private var _binding: FragmentUserReviewsBinding? = null
    private val binding get() = _binding!!

    private var reviewAdapter: MyReviewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeReviewState()
        observeNameState()
    }

    private fun observeNameState() {
        viewModel.nameState.observe(this) { showName(it) }
    }

    private fun observeReviewState() {
        viewModel.updateReviewState.observe(this) { notifyDataSetChanged() }
        viewModel.reviewsState.observe(this) { showReviews(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserReviewsBinding.inflate(inflater, container, false)
        arguments?.getString(USER_ID)?.let { viewModel.fetchReviews(it) }
        arguments?.getString(USER_ID)?.let { viewModel.getUserName(it) }
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecycler()
    }

    private fun setupToolbar() {
        setActionBar(binding.toolbar.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecycler() {
        reviewAdapter = MyReviewAdapter()
        binding.rvReviews.itemAnimator = DefaultItemAnimator()
        binding.rvReviews.adapter = reviewAdapter

        reviewAdapter?.setOnClickListener(object : OnItemClickListener<Review> {
            override fun onClick(item: Review, position: Int) {
                item.firebaseKey?.let {
                    val reviewFragment = SelectedReviewFragment.newInstance(it, false)

                    fragmentManager?.beginTransaction()?.apply {
                        replace(R.id.main_container, reviewFragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            }

            override fun onLongClick(item: Review, position: Int): Boolean {
                return false
            }
        })
    }

    private fun notifyDataSetChanged() {
        reviewAdapter?.notifyDataSetChanged()
    }

    private fun showReviews(reviews: List<Review>) {
        with(binding) {
            if (reviews.isNotEmpty()) {
                reviewAdapter?.addReviews(reviews)
                tvCountReviews.text =
                    resources.getQuantityString(R.plurals.reviews, reviews.size, reviews.size)
            } else {
                groupReviews.isVisible = false
                ltNoReviews.root.isVisible = true
                ltNoReviews.tvNoReviews.text = getString(R.string.user_no_reviews)
            }
        }
    }

    private fun showName(name: String) {
        actionBar?.title = name
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeListeners()
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
