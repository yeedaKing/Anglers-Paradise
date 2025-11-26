// app/src/main/java/com/anglersparadise/ui/tank/SpriteProvider.kt

package com.anglersparadise.ui.tank

import android.content.Context
import android.graphics.*
import android.util.LruCache
import com.anglersparadise.R

object SpriteProvider {
    private val cache = object : LruCache<String, Bitmap>(4 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    // Replace with real drawables when art lands (e.g., R.drawable.fish_bass)
    private fun speciesToResId(species: String): Int? = null  // null => always use placeholder for now

    fun get(context: Context, species: String, targetW: Int, targetH: Int): Bitmap {
        val w = targetW.coerceAtLeast(8)
        val h = targetH.coerceAtLeast(8)
        val resId = speciesToResId(species)  // currently always null

        val key = (resId?.toString() ?: "placeholder") + "@${w}x${h}"
        cache.get(key)?.let { return it }

        val bmp = resId?.let { tryDecodeScaled(context, it, w, h) } ?: drawPlaceholder(w, h, species)
        cache.put(key, bmp)
        return bmp
    }

    private fun tryDecodeScaled(ctx: Context, resId: Int, w: Int, h: Int): Bitmap {
        // This path is for real PNGs later; keep it.
        val base = BitmapFactory.decodeResource(ctx.resources, resId)
            ?: return drawPlaceholder(w, h, "fallback")
        return if (base.width == w && base.height == h) base
        else Bitmap.createScaledBitmap(base, w, h, true)
    }

    /** Simple vector-ish fish placeholder so we see *something* now. */
    private fun drawPlaceholder(w: Int, h: Int, label: String): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.TRANSPARENT)

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4FC3F7") }
        val eyePaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        val pupil     = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }

        // Body (oval)
        val body = RectF(w * 0.1f, h * 0.2f, w * 0.8f, h * 0.8f)
        c.drawOval(body, bodyPaint)

        // Tail (triangle)
        val tail = Path().apply {
            moveTo(w * 0.75f, h * 0.5f)
            lineTo(w * 0.95f, h * 0.3f)
            lineTo(w * 0.95f, h * 0.7f)
            close()
        }
        c.drawPath(tail, bodyPaint)

        // Eye
        val ex = w * 0.28f
        val ey = h * 0.45f
        c.drawCircle(ex, ey, (h * 0.07f), eyePaint)
        c.drawCircle(ex, ey, (h * 0.035f), pupil)

        return bmp
    }

    fun clear() = cache.evictAll()
}
