package com.druger.aboutwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.druger.aboutwork.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_filter_review, container, false)
    }
}