package com.druger.aboutwork.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Utils
import java.util.*

/**
 * Created by druger on 29.01.2017.
 */

open class ReviewAdapter(protected var reviews: MutableList<Review>) : SelectableAdapter<RecyclerView.ViewHolder>() {
    private val deletedReviews: MutableList<Review>

    private var clickListener: OnItemClickListener<Review>? = null

    init {
        deletedReviews = ArrayList()
    }

    fun getDeletedReviews(): List<Review> {
        return deletedReviews
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_card, parent, false)
        return ReviewVH(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reviewVH = holder as ReviewVH
        if (reviews.isNotEmpty()) {
            val review = reviews[position]
            reviewVH.clReviewCard.setBackgroundColor(if (isSelected(position))
                ContextCompat.getColor(reviewVH.clReviewCard.context, R.color.selected_review)
            else
                Color.WHITE)

            setStatus(reviewVH, review)
            reviewVH.tvPluses.text = Utils.getQuoteSpan(reviewVH.itemView.context,
                review.pluses, R.color.review_positive)
            reviewVH.tvMinuses.text = Utils.getQuoteSpan(reviewVH.itemView.context,
                review.minuses, R.color.review_negative)
            reviewVH.tvName.text = review.name
            reviewVH.tvCity.text = review.city
            reviewVH.tvDate.text = Utils.getDate(review.date)
            reviewVH.tvPosition.text = review.position

            if (review.status == Review.INTERVIEW) {
                reviewVH.tvRating.visibility = View.GONE
            } else {
                reviewVH.tvRating.text = review.markCompany?.averageMark.toString()
            }

            holder.itemView.setOnClickListener { itemClick(reviewVH, review) }
            holder.itemView.setOnLongClickListener { clickListener != null && clickListener!!.onLongClick(reviewVH.adapterPosition) }
        }
    }

    private fun setStatus(reviewVH: ReviewVH, review: Review) {
        when (review.status) {
            0 -> reviewVH.tvStatus.setText(R.string.working)
            1 -> reviewVH.tvStatus.setText(R.string.worked)
            2 -> reviewVH.tvStatus.setText(R.string.interview)
        }
    }

    private fun itemClick(holder: ReviewVH, review: Review) {
        if (clickListener != null) {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                clickListener?.onClick(review, pos)
            }
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ReviewVH(itemView: View) : BaseViewHolder(itemView) {
        var cvContent: CardView = bindView(R.id.cvContent)
        var clReviewCard: ConstraintLayout = bindView(R.id.clReviewCard)
        var tvStatus: TextView = bindView(R.id.tvStatus)
        var tvPluses: TextView = bindView(R.id.tvPluses)
        var tvMinuses: TextView = bindView(R.id.tvMinuses)
        var tvName: TextView = bindView(R.id.tvName)
        var tvCity: TextView = bindView(R.id.tvCity)
        var tvDate: TextView = bindView(R.id.tvDate)
        var tvPosition: TextView = bindView(R.id.tvPosition)
        var tvRating: TextView = bindView(R.id.tvRating)
    }

    fun setOnClickListener(clickListener: OnItemClickListener<Review>) {
        this.clickListener = clickListener
    }

    private fun removeItem(position: Int) {
        deletedReviews.add(reviews.removeAt(position))
        notifyItemRemoved(position)
    }

    fun removeItems(positions: MutableList<Int>) {
        deletedReviews.clear()
        positions.sortWith(Comparator { o1, o2 -> o2 - o1 })

        while (positions.isNotEmpty()) {
            if (positions.size == 1) {
                removeItem(positions[0])
                positions.removeAt(0)
            } else {
                var count = 1
                while (positions.size > count && positions[count] == positions[count - 1] - 1) {
                    ++count
                }
                if (count == 1) {
                    removeItem(positions[0])
                } else {
                    removeRange(positions[count - 1], count)
                }
                positions.subList(0, count).clear()
            }
        }
    }

    private fun removeRange(positionStart: Int, itemCount: Int) {
        for (i in 0 until itemCount) {
            deletedReviews.add(reviews.removeAt(positionStart))
        }
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun addReviews(reviews: List<Review>) {
        this.reviews.clear()
        this.reviews.addAll(reviews)
        notifyDataSetChanged()
    }

    fun addReview(review: Review, position: Int) {
        reviews.add(position, review)
        notifyItemInserted(position)
    }

    fun removeReview(position: Int) {
        reviews.removeAt(position)
        notifyItemRemoved(position)
    }
}
