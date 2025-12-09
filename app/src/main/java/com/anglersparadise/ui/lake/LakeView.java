// app/src/main/java/com/anglersparadise/ui/lake/LakeView.java
package com.anglersparadise.ui.lake;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.anglersparadise.R;

public class LakeView extends View {
    private Bitmap bg; // scaled to view size

    private LakeState state = LakeState.IDLE;

    private final Paint water   = new Paint();
    private final Paint surface = new Paint();
    private final Paint baitPaint = new Paint();
    @SuppressWarnings("FieldCanBeLocal")
    private final Paint textPaint = new Paint();

    // --- Bait animation state ---
    // Fraction of view height (0 = top, 1 = bottom). NaN = hidden
    private float baitFrac = Float.NaN;
    private ValueAnimator baitAnimator;
    private static final TimeInterpolator EASE_OUT = new DecelerateInterpolator();

    public LakeView(Context context) { this(context, null); }
    public LakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        water.setColor(0xFF1B3358);     // deep blue
        surface.setColor(0xFF2C4A7C);   // lighter band
        baitPaint.setColor(0xFFFFD166); // yellow
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(36f);
        textPaint.setAntiAlias(true);
    }

    public void setLakeState(LakeState newState) {
        if (newState == state) return;
        state = newState;

        // Cancel any in-flight animation
        if (baitAnimator != null) {
            baitAnimator.cancel();
            baitAnimator = null;
        }

        switch (state) {
            case IDLE:
                // hide bait
                baitFrac = Float.NaN;
                break;

            case WAITING:
                // On cast: start near the surface then drift to a
                // comfortably-underwater position.
                float startFrac = Float.isNaN(baitFrac) ? 0.06f : baitFrac; // start just below surface
                animateBait(startFrac, 0.45f, 700); // sink to ~30% height
                break;

            case HOOKED:
                // Fish on: sink a little bit further
                float from = Float.isNaN(baitFrac) ? 0.30f : baitFrac;
                animateBait(from, 0.50f, 450);
                break;

            case CAUGHT:
            case ESCAPED:
            default:
                baitFrac = Float.NaN; // hide
                break;
        }

        invalidate();
    }

    private void animateBait(float fromFrac, float toFrac, long durationMs) {
        baitAnimator = ValueAnimator.ofFloat(fromFrac, toFrac);
        baitAnimator.setInterpolator(EASE_OUT);
        baitAnimator.setDuration(durationMs);
        baitAnimator.addUpdateListener(a -> {
            baitFrac = (float) a.getAnimatedValue();
            // Clamp a bit so we don't draw off the view
            if (baitFrac < -0.2f) baitFrac = -0.2f;
            if (baitFrac > 1.2f)  baitFrac = 1.2f;
            invalidate();
        });
        baitAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (baitAnimator != null) {
            baitAnimator.cancel();
            baitAnimator = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        loadOrScaleBg();
    }

    private void loadOrScaleBg() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;

        Bitmap base = BitmapFactory.decodeResource(getResources(), R.drawable.lake_bg, opts);
        if (base == null) {
            bg = null;
            return;
        }

        if (base.getWidth() == w && base.getHeight() == h) {
            bg = base;
        } else {
            boolean smooth = true; // set false for nearest-neighbor (pixel art)
            bg = Bitmap.createScaledBitmap(base, w, h, smooth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        // 1) Background
        if (bg != null) {
            canvas.drawBitmap(bg, null, new RectF(0, 0, w, h), null);
        } else {
            canvas.drawRect(0f, 0f, w, h, water);
            float surfaceH = Math.min(18f, Math.max(8f, h * 0.06f));
            canvas.drawRect(0f, 0f, w, surfaceH, surface);
        }

        // 2) Bait (only if visible)
        if (!Float.isNaN(baitFrac)) {
            float baitY = h * baitFrac;
            float baitX = w * 0.55f;
            float baitSize = Math.min(w, h) * 0.03f;
            canvas.drawRect(baitX - baitSize, baitY - baitSize,
                    baitX + baitSize, baitY + baitSize, baitPaint);
        }
    }
}
