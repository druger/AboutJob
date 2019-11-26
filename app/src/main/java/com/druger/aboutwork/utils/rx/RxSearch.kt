package com.druger.aboutwork.utils.rx

import androidx.appcompat.widget.SearchView

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by druger on 05.07.2017.
 */

object RxSearch {

    fun fromSearchView(searchView: SearchView): Observable<String> {
        val subject = BehaviorSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                subject.onComplete()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    subject.onNext(newText)
                }
                return true
            }
        })
        return subject
    }
}
