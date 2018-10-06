package com.druger.aboutwork.utils

import android.content.Context
import android.graphics.*
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by druger on 19.08.2016.
 */
object Utils {

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
        val width = 400
        val height = 400
        val stroke = 20f
        val padding = 5
        val density = context.resources.displayMetrics.density

        val arcStroke = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        arcStroke.strokeWidth = stroke
        arcStroke.style = Paint.Style.STROKE
        arcStroke.strokeCap = Paint.Cap.ROUND

        val text = Paint(Paint.ANTI_ALIAS_FLAG)
        text.textSize = 5f / density
        text.color = Color.BLUE
        text.textAlign = Paint.Align.CENTER

        val arc = RectF()
        arc.set(stroke / 2 + padding, stroke / 2 + padding,
                width - padding - stroke / 2,
                height - padding - stroke / 2)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        arcStroke.color = Color.argb(75, 255, 255, 255);
        canvas.drawArc(arc, 135f, 275f, false, arcStroke)
        canvas.drawText(percent.toString() + "/5", (bitmap.width / 2).toFloat(),
                (bitmap.height - text.ascent()) / 2,
                text)
        canvas.drawText("Salary", (bitmap.width / 2).toFloat(),
                bitmap.height - (stroke + padding), text)

        return bitmap
    }
}
