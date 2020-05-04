package com.druger.aboutwork.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val TAG = "DatePickerDialog"
        const val EMPLOYMENT_DATE = 0
        const val DISMISSAL_DATE = 1
    }

    var flag = -1

    private var etDate: TextInputEditText? = null
    private var review: Review? = null

    private var c = Calendar.getInstance()

    fun setData(date: TextInputEditText, review: Review) {
        this.etDate = date
        this.review = review
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return context?.let {
            DatePickerDialog(it, this, year, month, day)
        } ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, monthOfYear)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date: Long = c.timeInMillis
        etDate?.setText(Utils.getDate(date))
        etDate?.background = null

        when (flag) {
            EMPLOYMENT_DATE -> review?.employmentDate = date
            DISMISSAL_DATE -> review?.dismissalDate = date
            else -> {
            }
        }
    }
}