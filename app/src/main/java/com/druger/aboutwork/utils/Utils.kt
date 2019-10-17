package com.druger.aboutwork.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.Spanned
import android.util.Patterns
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.druger.aboutwork.R
import com.druger.aboutwork.activities.LoginActivity
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

    @JvmStatic
    fun getQuoteSpan(context: Context, text: String?, color: Int): SpannableString {
        val string = SpannableString(text)
        string.setSpan(MyQuoteSpan(ContextCompat.getColor(context, color),
            5, 20),
            0, text?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return string
    }

    fun showAuthDialog(context: Context, @StringRes title: Int) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.auth_layout, null)
        builder.setView(dialogView)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        builder.show()

        val tvAuthTitle: TextView = dialogView.findViewById(R.id.tvAuth)
        val btnLogin: Button = dialogView.findViewById(R.id.btnLogin)

        tvAuthTitle.setText(title)
        btnLogin.setOnClickListener { context.startActivity(Intent(context, LoginActivity::class.java)) }
    }
}
