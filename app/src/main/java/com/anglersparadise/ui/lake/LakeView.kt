// app/src/main/java/com/anglersparadise/ui/lake/LakeView.kt

package com.anglersparadise.ui.lake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class LakeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var state: LakeState = LakeState.IDLE
    private val water = Paint().apply { color = 0xFF1B3358.toInt() }   // deep blue
    private val surface = Paint().apply { color = 0xFF2C4A7C.toInt() } // lighter band
    private val baitPaint = Paint().apply { color = 0xFFFFD166.toInt() } // yellow
    private val textPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 36f
        isAntiAlias = true
    }

    fun setLakeState(newState: LakeState) {
        state = newState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // Water background
        canvas.drawRect(0f, 0f, w, h, water)
        // Surface band
        val surfaceH = min(18f, max(8f, h * 0.06f))
        canvas.drawRect(0f, 0f, w, surfaceH, surface)

        // Bait position: floats near surface; sinks when HOOKED
        val baitY = when (state) {
            LakeState.HOOKED -> h * 0.6f
            LakeState.WAITING -> h * 0.12f
            else -> h * 0.10f
        }
        val baitX = w * 0.55f
        val baitSize = min(w, h) * 0.03f
        canvas.drawRect(baitX - baitSize, baitY - baitSize, baitX + baitSize, baitY + baitSize, baitPaint)

        // Optional status watermark in-canvas (helps when debugging)
        // canvas.drawText(state.name, 16f, h - 20f, textPaint)
    }
}
