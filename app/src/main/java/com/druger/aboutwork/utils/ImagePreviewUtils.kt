package com.druger.aboutwork.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

object ImagePreviewUtils {
    private const val BITMAP_SCALE = 0.3f
    private const val BLUR_RADIUS = 10f

    fun getBlurredScreenDrawable(screen: View): BitmapDrawable {
        val screenshot = takeScreenshot(screen)
        val blurred = blurBitmap(screen.context, screenshot)
        return BitmapDrawable(screen.context.resources, blurred)
    }

    private fun blurBitmap(context: Context, bitmap: Bitmap): Bitmap {
        val width = (bitmap.width * BITMAP_SCALE).roundToInt()
        val height = (bitmap.height * BITMAP_SCALE).roundToInt()

        val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val renderScript = RenderScript.create(context)
        val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val allocationIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, outputBitmap)
        intrinsicBlur.apply {
            setRadius(BLUR_RADIUS)
            setInput(allocationIn)
            forEach(allocationOut)
            allocationOut.copyTo(outputBitmap)
        }
        return outputBitmap
    }

    private fun takeScreenshot(screen: View): Bitmap {
        val bitmap = Bitmap.createBitmap(screen.width, screen.height, Bitmap.Config.ARGB_8888)
        screen.draw(Canvas(bitmap))
        return bitmap
    }
}