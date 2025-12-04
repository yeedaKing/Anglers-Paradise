// app/src/main/java/com/anglersparadise/ui/tank/FishTankView.java
package com.anglersparadise.ui.tank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.anglersparadise.domain.model.Fish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FishTankView extends View {

    private List<Fish> fish = java.util.Collections.emptyList();
    private List<Fish> pendingFish = null;

    private final Map<Long, Float> xPos = new HashMap<>();
    private final Map<Long, Float> yPos = new HashMap<>();
    private final Map<Long, Float> speed = new HashMap<>();
    private final Random rng = new Random();

    public FishTankView(Context context) { this(context, null); }
    public FishTankView(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setFish(List<Fish> list) {
        if (getWidth() == 0 || getHeight() == 0) {
            fish = list;
            pendingFish = list;
            invalidate();
            return;
        }
        applyFishList(list);
    }

    private void applyFishList(List<Fish> list) {
        fish = list;
        float w = Math.max(1f, getWidth());
        float h = Math.max(1f, getHeight());

        for (Fish f : list) {
            long id = f.getId();
            if (!xPos.containsKey(id)) {
                xPos.put(id, randFloat(-50f, w));
                yPos.put(id, randFloat(h * 0.2f, h * 0.8f));
                speed.put(id, randFloat(1.2f, 3.2f));
            }
        }
        // prune removed fish
        java.util.HashSet<Long> ids = new java.util.HashSet<>();
        for (Fish f : list) ids.add(f.getId());
        java.util.ArrayList<Long> toRemove = new java.util.ArrayList<>();
        for (Long id : xPos.keySet()) if (!ids.contains(id)) toRemove.add(id);
        for (Long id : toRemove) { xPos.remove(id); yPos.remove(id); speed.remove(id); }

        invalidate();
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (pendingFish != null) {
            applyFishList(pendingFish);
            pendingFish = null;
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fish.isEmpty()) return;

        float w = getWidth();
        float h = getHeight();

        for (Fish f : fish) {
            long id = f.getId();
            float vx = (speed.containsKey(id) ? speed.get(id) : 2f);
            float newX = (xPos.containsKey(id) ? xPos.get(id) : -50f) + vx;
            xPos.put(id, (newX > w + 50f) ? -50f : newX);
            float y = yPos.containsKey(id) ? yPos.get(id) : (h * 0.5f);

            // size -> sprite bounds
            float base = dp(48f);
            float incr = dp(8f) * (clamp(f.getSize(), 1, 5) - 1);
            int spriteW = Math.max(1, Math.round(base + incr));
            int spriteH = Math.max(1, Math.round(base * 0.7f + incr * 0.7f));

            android.graphics.Bitmap bmp =
                    SpriteProvider.get(getContext(), f.getSpecies(), spriteW, spriteH);

            float left = xPos.get(id);
            float top = y - spriteH / 2f;
            RectF dst = new RectF(left, top, left + spriteW, top + spriteH);
            canvas.drawBitmap(bmp, null, dst, null);
        }

        postInvalidateOnAnimation();
    }

    private float dp(float px) { return px * getResources().getDisplayMetrics().density; }
    private float randFloat(float a, float b) { return a + rng.nextFloat() * (b - a); }
    private int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }
}
