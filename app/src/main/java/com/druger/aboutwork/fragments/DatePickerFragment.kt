package com.druger.aboutwork.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object{
        const val TAG = "DatePickerDialog"
        const val EMPLOYMENT_DATE = 0
        const val DISMISSAL_DATE = 1
        const val INTERVIEW_DATE = 2
    }

    var flag = -1

    private var etDate: EditText? = null
    private var review: Review? = null

    internal var c = Calendar.getInstance()

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
        val date: Long

        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, monthOfYear)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date = c.timeInMillis
        etDate!!.setText(Utils.getDate(date))

        when (flag) {
            EMPLOYMENT_DATE -> review!!.employmentDate = date
            DISMISSAL_DATE -> review!!.dismissalDate = date
            INTERVIEW_DATE -> review!!.interviewDate = date
            else -> { }
        }
    }
}