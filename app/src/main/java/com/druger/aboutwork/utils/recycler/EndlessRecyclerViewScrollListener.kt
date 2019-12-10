package com.druger.aboutwork.utils.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by druger on 04.01.2018.
 */

abstract class EndlessRecyclerViewScrollListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private val visibleThreshold = 5
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 0
    private var pages = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount

        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) loading = true
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading
            && lastVisibleItemPosition + visibleThreshold >= totalItemCount
            && dy > 0) {
            loading = if (currentPage < pages) {
                currentPage++
                onLoadMore(currentPage)
                true
            } else false
        }
    }

    abstract fun onLoadMore(page: Int)

    private fun resetPageCount(page: Int) {
        previousTotalItemCount = 0
        loading = true
        currentPage = page
        onLoadMore(currentPage)
    }

    fun resetPageCount() {
        resetPageCount(0)
    }

    fun setLoaded() {
        loading = false
    }

    fun setPages(pages: Int) {
        this.pages = pages
    }
}
