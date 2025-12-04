// app/src/main/java/com/anglersparadise/ui/lake/LakeView.java
package com.anglersparadise.ui.lake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LakeView extends View {

    private LakeState state = LakeState.IDLE;

    private final Paint water = new Paint();
    private final Paint surface = new Paint();
    private final Paint baitPaint = new Paint();
    @SuppressWarnings("FieldCanBeLocal")
    private final Paint textPaint = new Paint();

    public LakeView(Context context) { this(context, null); }
    public LakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        water.setColor(0xFF1B3358);    // deep blue
        surface.setColor(0xFF2C4A7C);  // lighter band
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        // Water
        canvas.drawRect(0f, 0f, w, h, water);
        // Surface band
        float surfaceH = Math.min(18f, Math.max(8f, h * 0.06f));
        canvas.drawRect(0f, 0f, w, surfaceH, surface);

        // Bait position
        float baitY;
        switch (state) {
            case HOOKED:  baitY = h * 0.6f; break;
            case WAITING: baitY = h * 0.12f; break;
            default:      baitY = h * 0.10f;
        }
        float baitX = w * 0.55f;
        float baitSize = Math.min(w, h) * 0.03f;
        canvas.drawRect(baitX - baitSize, baitY - baitSize, baitX + baitSize, baitY + baitSize, baitPaint);
    }
}
