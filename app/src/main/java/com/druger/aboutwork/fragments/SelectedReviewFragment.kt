package com.druger.aboutwork.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import com.druger.aboutwork.Const.Bundles.EDIT_MODE
import com.druger.aboutwork.Const.Colors.DISLIKE
import com.druger.aboutwork.Const.Colors.GRAY_500
import com.druger.aboutwork.Const.Colors.LIKE
import com.druger.aboutwork.Const.Colors.PURPLE_100
import com.druger.aboutwork.Const.Colors.PURPLE_500
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.MainActivity
import com.druger.aboutwork.adapters.CommentAdapter
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.enums.Screen
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.interfaces.view.SelectedReview
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.presenters.SelectedReviewPresenter
import com.druger.aboutwork.utils.Utils
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.content_selected_review.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import org.threeten.bp.Instant
import org.threeten.bp.Period
import org.threeten.bp.ZoneId
import java.util.*

class SelectedReviewFragment : BaseSupportFragment(), SelectedReview {

    private var type = NEW

    @InjectPresenter
    lateinit var presenter: SelectedReviewPresenter

    private lateinit var commentAdapter: CommentAdapter

    private var review: Review? = null
    private var reviewKey: String? = null
    private var message: String? = null // if we came after Login shouldn't be null
    private var editMode: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        getBundles()
        setView(inflater, container)
        getReview()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setUX()
    }

    private fun getBundles() {
        arguments?.apply {
            reviewKey = getString(REVIEW_KEY)
            message = getString(MESSAGE)
            editMode = getBoolean(EDIT_MODE)
        }
    }

    override fun setupComments(user: FirebaseUser?) {
        commentAdapter = CommentAdapter(presenter.user)
        rvComments.itemAnimator = DefaultItemAnimator()
        rvComments.adapter = commentAdapter
        commentAdapter.setOnNameClickListener(object : CommentAdapter.OnNameClickListener {
            override fun onClick(comment: Comment) {
                showReviews(comment.userId)
            }
        })

        retrieveComments()
        setupListeners()
    }

    private fun retrieveComments() {
        reviewKey?.let { presenter.retrieveComments(it) }
    }

    private fun setupListeners() {
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    ivSend.isClickable = true
                    ivSend.setColorFilter(Color.parseColor(PURPLE_500))
                } else {
                    ivSend.isClickable = false
                    ivSend.setColorFilter(Color.parseColor(PURPLE_100))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        ivSend.setOnClickListener {
            when (type) {
                NEW -> sendMessage(etMessage.text.toString().trim(), NEW)
                UPDATE -> sendMessage(etMessage.text.toString().trim(), UPDATE)
            }
        }

        commentAdapter.setOnItemClickListener(object : OnItemClickListener<Comment> {
            override fun onClick(item: Comment, position: Int) {}

            override fun onLongClick(position: Int): Boolean {
                return presenter.onLongClick(position)
            }
        })
    }

    private fun sendMessage(message: String, type: Int) {
        if (message.isNotEmpty()) {
            if (type == NEW) {
                review?.firebaseKey?.let { presenter.addComment(message, it) }
            } else if (type == UPDATE) {
                presenter.updateComment(message)
                Utils.hideKeyboard(requireContext(), etMessage)
                this.type = NEW
            }
        }
    }

    private fun setView(inflater: LayoutInflater, container: ViewGroup?) {
        rootView = inflater.inflate(R.layout.fragment_selected_review, container, false)
        (activity as MainActivity).hideBottomNavigation()
    }

    private fun setUX() {
        ivLike.setOnClickListener { presenter.clickLike() }
        ivDislike.setOnClickListener { presenter.clickDislike() }
        if (editMode) {
            ivEdit.setOnClickListener { showEditReview() }
        }
    }

    private fun setupToolbar() {
        mToolbar = toolbar
        mToolbar?.let { setActionBar(it) }
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle(R.string.review)
        if (editMode) ivEdit.visibility = View.VISIBLE
        else ivEdit.visibility = View.GONE
    }

    private fun getReview() {
        reviewKey?.let { presenter.getReview(it) }
    }

    override fun setReview(review: Review?) {
        if (review != null) {
            this.review = review
            val myLike = review.myLike
            val myDislike = review.myDislike
            if (!myLike) {
                ivLike.tag = requireActivity().getString(R.string.like_inactive)
            } else {
                ivLike.tag = requireActivity().getString(R.string.like_active)
                ivLike.setColorFilter(Color.parseColor(LIKE))
            }
            if (!myDislike) {
                ivDislike.tag = requireActivity().getString(R.string.dislike_inactive)
            } else {
                ivDislike.tag = requireActivity().getString(R.string.dislike_active)
                ivDislike.setColorFilter(Color.parseColor(DISLIKE))
            }
            tvPluses.text = Utils.getQuoteSpan(requireContext(), review.pluses, R.color.review_positive)
            tvMinuses.text = Utils.getQuoteSpan(requireContext(), review.minuses, R.color.review_negative)
            tvName.text = review.name
            tvDate.text = Utils.getDate(review.date)
            tvPosition.text = review.position
            tvDislike.text = review.dislike.toString()
            tvLike.text = review.like.toString()
            review.markCompany?.let { mark ->
                rbSalary.rating = mark.salary
                rbCareer.rating = mark.career
                rbCollective.rating = mark.collective
                rbSocialPackage.rating = mark.socialPackage
                rbChief.rating = mark.chief
                rbWorkplace.rating = mark.workplace
            }
            setExperience(review)
            message?.let { etMessage.setText(it) }
            checkMessage()
            setRecommendation(review)
        }
    }

    private fun setRecommendation(review: Review) {
        review.recommended?.let { recommended ->
            ivRecommendation.visibility = View.VISIBLE
            tvRecommendation.visibility = View.VISIBLE
            if (recommended) {
                ivRecommendation.setImageResource(R.drawable.ic_recommended)
                tvRecommendation.text = getString(R.string.recommended)
            } else {
                ivRecommendation.setImageResource(R.drawable.ic_not_recommended)
                tvRecommendation.text = getString(R.string.not_recommended)
            }
        }
    }

    private fun checkMessage() {
        message?.let { sendMessage(it, NEW) }
    }

    private fun setExperience(review: Review) {
        when (review.status) {
            Review.WORKING -> {
                tvStatus.setText(R.string.working)
                setWorkingDays(review.employmentDate, Calendar.getInstance().timeInMillis)
            }
            Review.WORKED -> {
                tvStatus.setText(R.string.worked)
                setWorkingDays(review.employmentDate, review.dismissalDate)
            }
            Review.INTERVIEW -> {
                tvStatus.setText(R.string.interview)
                val interviewDate = review.interviewDate
                if (interviewDate != 0L) tvDescriptionStatus.text = Utils.getDate(interviewDate)
                groupRating.visibility = View.GONE
            }
        }
    }

    private fun setWorkingDays(first: Long, last: Long) {
        if (first != 0L) {
            val f = Date(first)
            val l = Date(last)
            val firstDate = Instant.ofEpochMilli(f.time)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val lastDate = Instant.ofEpochMilli(l.time)
                .atZone(ZoneId.systemDefault()).toLocalDate()

            val period = Period.between(firstDate, lastDate)
            val years = period.years
            val months = period.months
            val days = period.days
            val res = resources
            if (years > 0 && months > 0 && days > 0) {
                tvDescriptionStatus.text = res.getQuantityString(R.plurals.year, years, years)
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.month, months, months))
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.day, days, days))
            } else if (years >= 0 && months <= 0 && days <= 0) {
                tvDescriptionStatus.text = res.getQuantityString(R.plurals.year, years, years)
            } else if (years <= 0 && months > 0 && days >= 0) {
                tvDescriptionStatus.text = res.getQuantityString(R.plurals.month, months, months)
                tvDescriptionStatus.append(" " + res.getQuantityString(R.plurals.day, days, days))
            } else if (years <= 0 && months <= 0) {
                tvDescriptionStatus.text = res.getQuantityString(R.plurals.day, days, days)
            }
        }
    }

    private fun showEditReview() {
        reviewKey?.let {
            val reviewFragment = EditReviewFragment.newInstance(it)
            replaceFragment(reviewFragment, R.id.main_container, true)
        }
    }

    override fun onLikeClicked() {
        review?.let { review ->
            var like = review.like
            var dislike = review.dislike
            if (!review.myLike) {
                ivLike.setColorFilter(Color.parseColor(LIKE))
                review.like = ++like
                tvLike.text = like.toString()
                review.myLike = true

                if (review.myDislike) {
                    ivDislike.setColorFilter(Color.parseColor(GRAY_500))
                    review.dislike = --dislike
                    tvDislike.text = dislike.toString()
                    review.myDislike = false
                    FirebaseHelper.dislikeReview(review)
                }
            } else {
                ivLike.setColorFilter(Color.parseColor(GRAY_500))
                review.like = --like
                tvLike.text = like.toString()
                review.myLike = false
            }
            FirebaseHelper.likeReview(review)
        }
    }

    override fun onDislikeClicked() {
        review?.let { review ->
            var like = review.like
            var dislike = review.dislike
            if (!review.myDislike) {
                ivDislike.setColorFilter(Color.parseColor(DISLIKE))
                review.dislike = ++dislike
                tvDislike.text = dislike.toString()
                review.myDislike = true

                if (review.myLike) {
                    ivLike.setColorFilter(Color.parseColor(GRAY_500))
                    review.like = --like
                    tvLike.text = like.toString()
                    review.myLike = false
                    FirebaseHelper.likeReview(review)
                }
            } else {
                ivDislike.setColorFilter(Color.parseColor(GRAY_500))
                review.dislike = --dislike
                tvDislike.text = dislike.toString()
                review.myDislike = false
            }
            FirebaseHelper.dislikeReview(review)
        }
    }

    private fun showReviews(userId: String?) {
        val reviews = userId?.let { UserReviewsFragment.newInstance(it) }

        fragmentManager?.beginTransaction()?.apply {
            reviews?.let { replace(R.id.main_container, it) }
            addToBackStack(null)
            commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (editMode) {
            (activity as MainActivity).showBottomNavigation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeListeners()
    }

    override fun showChangeDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(R.array.comments_change) { _, which ->
            when (which) {
                0 -> presenter.deleteComment(position)
                1 -> {
                    etMessage.setText(presenter.comment.message)
                    Utils.showKeyboard(requireContext())
                    etMessage.isFocusableInTouchMode = true
                    presenter.comment.message?.length?.let { etMessage.setSelection(it) }
                    type = UPDATE
                }
            }
        }
        builder.show()
    }

    override fun notifyItemRemoved(position: Int, size: Int) {
        commentAdapter.notifyItemRemoved(position)
        commentAdapter.notifyItemRangeChanged(position, size)
    }

    override fun showComments(comments: List<Comment>) {
        commentAdapter.clear()
        commentAdapter.addItems(comments)
    }

    override fun showAuth(title: Int) {
        Utils.hideKeyboard(requireContext(), etMessage)
        replaceFragment(
            AuthFragment.newInstance(
                getString(title),
                Screen.REVIEW.name,
                null,
                reviewKey,
                etMessage.text.toString().trim()),
            R.id.content_review)
    }

    override fun clearMessage() {
        etMessage.text = null
    }

    companion object {
        private const val NEW = 0
        private const val UPDATE = 1
        private const val REVIEW_KEY = "review_key"
        private const val MESSAGE = "message"

        fun newInstance(reviewKey: String, editMode: Boolean, message: String? = null): SelectedReviewFragment {

            val args = Bundle()
            args.putString(REVIEW_KEY, reviewKey)
            args.putString(MESSAGE, message)
            args.putBoolean(EDIT_MODE, editMode)

            val fragment = SelectedReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
