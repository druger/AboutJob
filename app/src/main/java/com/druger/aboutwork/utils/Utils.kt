package com.druger.aboutwork.utils

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.QuoteSpan
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by druger on 19.08.2016.
 */
object Utils {

    @JvmStatic
    fun getDate(date: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getNameByEmail(email: String): String {
        val name = ""
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email.substring(0, email.indexOf('@'))
        } else name
    }

    fun showKeyboard(context: Context) {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(context: Context, editText: EditText) {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun crateArcBitmap(context: Context, percent: Int): Bitmap {
        val width = 200
        val height = 200
        val stroke = 10f
        val padding = 5
        val density = context.resources.displayMetrics.density

        val arcStroke = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        arcStroke.strokeWidth = stroke
        arcStroke.style = Paint.Style.STROKE
        arcStroke.strokeCap = Paint.Cap.ROUND

        val text = Paint(Paint.ANTI_ALIAS_FLAG)
        text.textSize = 5f / density
        text.color = Color.BLACK
        text.textAlign = Paint.Align.CENTER

        val arc = RectF()
        arc.set(stroke / 2 + padding, stroke / 2 + padding,
                width - padding - stroke / 2,
                height - padding - stroke / 2)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        arcStroke.color = Color.argb(75, 0, 0, 255);
        canvas.drawArc(arc, 135f, 275f, false, arcStroke)
        arcStroke.color = Color.BLUE
        canvas.drawArc(arc, 135f, 200f, false, arcStroke)
        text.textSize = 180 / density
        canvas.drawText(percent.toString() + "/5", (bitmap.width / 2).toFloat(),
                (bitmap.height - text.ascent()) / 2,
                text)
        return bitmap
    }

    // TODO добавить реализацию для api<28
    @JvmStatic
    fun getQuoteSpan(context: Context, text: String, color: Int): SpannableString {
        val string = SpannableString(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            string.setSpan(QuoteSpan(ContextCompat.getColor(context, color),
                    15, 30),
                    0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return string
    }

    fun getDiffYears(date: Long): Int {
        val first = Calendar.getInstance()
        val last = Calendar.getInstance().apply { time = Date(date) }

        var diff = last.get(Calendar.YEAR) - first.get(Calendar.YEAR)
        if (first.get(Calendar.MONTH) > last.get(Calendar.MONTH) ||
                (first.get(Calendar.MONTH) == last.get(Calendar.MONTH)) &&
                first.get(Calendar.DATE) > last.get(Calendar.DATE)) {
            diff--
        }
        return diff
    }

    fun getDiffMonths(date: Long): Int {
        val first = Calendar.getInstance()
        val last = Calendar.getInstance().apply { time = Date(date) }

        return last.get(Calendar.MONTH) - first.get(Calendar.MONTH)
    }

    fun getDiffDays(date: Long): Int {
        val diff = Calendar.getInstance().time.time - date
        val sec = diff / 1000
        val min = sec / 60
        val hour = min / 60
        return (hour / 24).toInt()
    }
}
