package com.druger.aboutwork.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.text.Layout
import android.text.ParcelableSpan
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.Px

class MyQuoteSpan(@ColorInt private var mColor: Int,
                  @Px private var mStripeWidth: Int,
                  @Px private var mGapWidth: Int) : LeadingMarginSpan, ParcelableSpan {

    override fun getSpanTypeId(): Int {
        return getSpanTypeIdInternal()
    }

    /**
     * @hide
     */
    fun getSpanTypeIdInternal(): Int {
        return 9 // TextUtils.QUOTE_SPAN
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeToParcelInternal(dest, flags)
    }

    /**
     * @hide
     */
    fun writeToParcelInternal(dest: Parcel, flags: Int) {
        dest.writeInt(mColor)
        dest.writeInt(mStripeWidth)
        dest.writeInt(mGapWidth)
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return mStripeWidth + mGapWidth
    }

    override fun drawLeadingMargin(@NonNull c: Canvas, @NonNull p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   @NonNull text: CharSequence, start: Int, end: Int,
                                   first: Boolean, @NonNull layout: Layout) {
        val style = p.style
        val color = p.color

        p.style = Paint.Style.FILL
        p.color = mColor

        c.drawRect(x.toFloat(), top.toFloat(), (x + dir * mStripeWidth).toFloat(), bottom.toFloat(), p)

        p.style = style
        p.color = color
    }
}