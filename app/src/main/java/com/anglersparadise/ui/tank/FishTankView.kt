// app/src/main/java/com/anglersparadise/ui/tank/FishTankView.kt

package com.anglersparadise.ui.tank

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.anglersparadise.domain.model.Fish
import kotlin.math.roundToInt

class FishTankView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var fish: List<Fish> = emptyList()
    private var pendingFish: List<Fish>? = null

    // Positions by fish id
    private val xPos = mutableMapOf<Long, Float>()
    private val yPos = mutableMapOf<Long, Float>()
    private val speed = mutableMapOf<Long, Float>()

    fun setFish(list: List<Fish>) {
        // If we don't have a measured size yet, defer initialization.
        if (width == 0 || height == 0) {
            fish = list
            pendingFish = list
            invalidate()
            return
        }
        applyFishList(list)
    }

    private fun applyFishList(list: List<Fish>) {
        fish = list
        val w = width.toFloat().coerceAtLeast(1f)
        val h = height.toFloat().coerceAtLeast(1f)

        // Initialize new fish positions safely
        for (f in list) {
            if (xPos[f.id] == null) {
                xPos[f.id] = (-50f..w).random()
                yPos[f.id] = (h * 0.2f .. h * 0.8f).random()
                speed[f.id] = (1.2f..3.2f).random()
            }
        }
        // Remove positions for fish no longer present
        val ids = list.map { it.id }.toSet()
        xPos.keys.toList().filter { it !in ids }.forEach { id ->
            xPos.remove(id); yPos.remove(id); speed.remove(id)
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // If we had deferred data, initialize now that we have size.
        pendingFish?.let {
            applyFishList(it)
            pendingFish = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (fish.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()

        for (f in fish) {
            val id = f.id
            val vx = (speed[id] ?: 2f)
            val newX = ((xPos[id] ?: -50f) + vx)
            // Wrap with a little margin
            xPos[id] = if (newX > w + 50f) -50f else newX
            val y = yPos[id] ?: (h * 0.5f)

            // Scale sprite by fish size (1..5)
            val base = dp(48f)
            val incr = dp(8f) * (f.size.coerceIn(1, 5) - 1)
            val spriteW = (base + incr).roundToInt().coerceAtLeast(1)
            val spriteH = (base * 0.7f + incr * 0.7f).roundToInt().coerceAtLeast(1)

            val bmp = SpriteProvider.get(context, f.species, spriteW, spriteH)

            val left = xPos[id] ?: 0f
            val top = y - spriteH / 2f
            val dst = RectF(left, top, left + spriteW, top + spriteH)
            canvas.drawBitmap(bmp, null, dst, null)
        }

        postInvalidateOnAnimation()
    }

    private fun dp(px: Float): Float = px * resources.displayMetrics.density
    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return (start + Math.random() * (endInclusive - start)).toFloat()
    }
}
