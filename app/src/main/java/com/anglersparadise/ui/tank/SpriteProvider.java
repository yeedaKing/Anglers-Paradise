// app/src/main/java/com/anglersparadise/ui/tank/SpriteProvider.java
package com.anglersparadise.ui.tank;

import android.content.Context;
import android.graphics.*;
import android.util.LruCache;

public final class SpriteProvider {
    private static final LruCache<String, Bitmap> CACHE =
            new LruCache<String, Bitmap>(4 * 1024 * 1024) {
                @Override protected int sizeOf(String key, Bitmap value) { return value.getByteCount(); }
            };

    // For now always draw placeholders (return null -> no real PNG).
    private static Integer speciesToResId(String species) { return null; }

    public static Bitmap get(Context ctx, String species, int targetW, int targetH) {
        int w = Math.max(targetW, 8);
        int h = Math.max(targetH, 8);
        Integer resId = speciesToResId(species);
        String key = ((resId != null) ? String.valueOf(resId) : "placeholder") + "@" + w + "x" + h;

        Bitmap cached = CACHE.get(key);
        if (cached != null) return cached;

        Bitmap bmp = (resId != null) ? tryDecodeScaled(ctx, resId, w, h) : drawPlaceholder(w, h);
        CACHE.put(key, bmp);
        return bmp;
    }

    private static Bitmap tryDecodeScaled(Context ctx, int resId, int w, int h) {
        Bitmap base = BitmapFactory.decodeResource(ctx.getResources(), resId);
        if (base == null) return drawPlaceholder(w, h);
        if (base.getWidth() == w && base.getHeight() == h) return base;
        return Bitmap.createScaledBitmap(base, w, h, true);
    }

    private static Bitmap drawPlaceholder(int w, int h) {
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint body = new Paint(Paint.ANTI_ALIAS_FLAG); body.setColor(Color.parseColor("#4FC3F7"));
        Paint eyeW = new Paint(Paint.ANTI_ALIAS_FLAG); eyeW.setColor(Color.WHITE);
        Paint eyeB = new Paint(Paint.ANTI_ALIAS_FLAG); eyeB.setColor(Color.BLACK);

        // Body
        RectF bodyRect = new RectF(w * 0.1f, h * 0.2f, w * 0.8f, h * 0.8f);
        c.drawOval(bodyRect, body);
        // Tail
        Path tail = new Path();
        tail.moveTo(w * 0.75f, h * 0.5f);
        tail.lineTo(w * 0.95f, h * 0.3f);
        tail.lineTo(w * 0.95f, h * 0.7f);
        tail.close();
        c.drawPath(tail, body);
        // Eye
        float ex = w * 0.28f, ey = h * 0.45f;
        c.drawCircle(ex, ey, h * 0.07f, eyeW);
        c.drawCircle(ex, ey, h * 0.035f, eyeB);

        return bmp;
    }

    public static void clear() { CACHE.evictAll(); }
    private SpriteProvider() {}
}
