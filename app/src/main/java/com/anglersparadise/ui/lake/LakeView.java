// app/src/main/java/com/anglersparadise/ui/lake/LakeView.java
package com.anglersparadise.ui.lake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.anglersparadise.R;

public class LakeView extends View {
    private Bitmap bg; // scaled to view size

    private LakeState state = LakeState.IDLE;

    private final Paint water   = new Paint();
    private final Paint surface = new Paint();
    private final Paint baitPaint = new Paint();
    @SuppressWarnings("FieldCanBeLocal")
    private final Paint textPaint = new Paint();

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
        state = newState;
        invalidate();
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

        // Decode without density auto-scaling
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;

        Bitmap base = BitmapFactory.decodeResource(getResources(), R.drawable.lake_bg, opts);
        if (base == null) {
            bg = null; // fallback to painted background in onDraw
            return;
        }

        if (base.getWidth() == w && base.getHeight() == h) {
            bg = base;
        } else {
            // Set to false if art is pixel art and nearest-neighbor is wanted
            boolean smooth = true;
            bg = Bitmap.createScaledBitmap(base, w, h, smooth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        // 1) Draw background image if available; else paint water/surface
        if (bg != null) {
            canvas.drawBitmap(bg, null, new RectF(0, 0, w, h), null);
        } else {
            canvas.drawRect(0f, 0f, w, h, water);
            float surfaceH = Math.min(18f, Math.max(8f, h * 0.06f));
            canvas.drawRect(0f, 0f, w, surfaceH, surface);
        }

        // 2) Bait position
        float baitY;
        switch (state) {
            case HOOKED:  baitY = h * 0.6f;  break;
            case WAITING: baitY = h * 0.12f; break;
            default:      baitY = h * 0.10f; break;
        }
        float baitX = w * 0.55f;
        float baitSize = Math.min(w, h) * 0.03f;
        canvas.drawRect(baitX - baitSize, baitY - baitSize, baitX + baitSize, baitY + baitSize, baitPaint);
    }
}
