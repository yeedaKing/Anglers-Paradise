// app/src/main/java/com/anglersparadise/ui/tank/FishTankView.kt

package com.anglersparadise.ui.tank

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.anglersparadise.domain.model.Fish
import kotlin.math.max
import kotlin.math.min

class FishTankView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var fish: List<Fish> = emptyList()
    private val water = Paint().apply { color = 0xFF0F2438.toInt() }
    private val fishPaint = Paint().apply { color = 0xFF8EE3F5.toInt() }
    private val bubble = Paint().apply { color = 0x66FFFFFF.toInt() }

    fun setFish(list: List<Fish>) {
        fish = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // Background water
        canvas.drawRect(0f, 0f, w, h, water)

        // Simple drift animation based on time
        val t = System.currentTimeMillis() / 16f

        if (fish.isEmpty()) return

        val rowH = h / max(1, min(6, fish.size))
        fish.forEachIndexed { i, f ->
            // x drifts left->right, y is per-row with slight bob
            val speed = 0.6f + (f.id % 5) * 0.15f
            val x = ((t * speed + (i * 90)) % (w + 80f)) - 40f
            val yBase = i * rowH + rowH * 0.5f
            val y = yBase + 10f * kotlin.math.sin((t + i * 20) / 16f)

            val size = 14f + (f.size.coerceIn(1, 5) * 6f)
            // Body (circle) + simple tail
            canvas.drawCircle(x, y, size, fishPaint)
            canvas.drawRect(x - size - 10f, y - 6f, x - size + 2f, y + 6f, fishPaint)

            // Occasional bubble
            val bx = x + size + 8f
            val by = y - size - ((t + i * 10) % 40f)
            if (by > 0) canvas.drawCircle(bx, by, 3f, bubble)
        }

        // Schedule next frame
        postInvalidateOnAnimation()
    }
}
