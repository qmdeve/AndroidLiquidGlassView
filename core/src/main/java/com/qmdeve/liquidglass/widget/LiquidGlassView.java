/**
 * Copyright 2025 QmDeve
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author QmDeve
 * @github https://github.com/QmDeve
 * @since 2025-11-01
 */

package com.qmdeve.liquidglass.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qmdeve.liquidglass.LiquidGlass;
import com.qmdeve.liquidglass.Config;
import com.qmdeve.liquidglass.util.LiquidTracker;
import com.qmdeve.liquidglass.util.Utils;

public class LiquidGlassView extends ViewGroup {

    private LiquidGlass glass;
    private ViewGroup customSource;
    private final Context context;
    private float cornerRadius = Utils.dp2px(getResources(), 40), refractionHeight = Utils.dp2px(getResources(), 20), refractionOffset = -Utils.dp2px(getResources(), 70), tintAlpha = 0.0f, tintColorRed = 1.0f, tintColorGreen = 1.0f, tintColorBlue = 1.0f, blurRadius = 0.01f, dispersion = 0.5f, downX, downY, startTx, startTy;
    private boolean draggableEnabled = false;
    private boolean elasticEnabled = false;
    private boolean touchEffectEnabled = false;
    private Config config;
    private LiquidTracker liquidTracker;

    // Glow effect variables
    private Paint glowPaint;
    private float glowX, glowY;
    private boolean isTouching = false;

    public LiquidGlassView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        this.context = context;
        init();
    }

    public LiquidGlassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        this.context = context;
        init();
    }

    public LiquidGlassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        this.context = context;
        init();
    }

    private void init() {
        setClipToPadding(false);
        setClipChildren(false);
        liquidTracker = new LiquidTracker(this);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (touchEffectEnabled && isTouching) {
            Path path = new Path();
            RectF rect = new RectF(0, 0, getWidth(), getHeight());
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.save();
            canvas.clipPath(path);

            float radius = Math.max(getWidth(), getHeight()) * 0.8f;
            int[] colors = {Color.argb(60, 255, 255, 255), Color.TRANSPARENT};
            float[] stops = {0f, 1f};
            RadialGradient gradient = new RadialGradient(glowX, glowY, radius, colors, stops, Shader.TileMode.CLAMP);
            glowPaint.setShader(gradient);
            canvas.drawRect(rect, glowPaint);

            canvas.restore();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout all child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Layout children to fill the entire view
                child.layout(0, 0, getWidth(), getHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Measure all child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    /**
     * Bind sampling source
     *
     * @param source ViewGroup
     */
    public void bind(ViewGroup source) {
        this.customSource = source;
        if (glass != null && source != null) {
            glass.init(source);
        }
    }

    /**
     * Set the corner radius px
     *
     * @param px float
     */
    public void setCornerRadius(float px) {
        float maxPx = getHeight() > 0 ? getHeight() / 2f : Utils.dp2px(getResources(), 99);
        this.cornerRadius = Math.max(0, Math.min(px, maxPx));
        updateConfig();
    }

    /**
     * Set the refraction height px
     *
     * @param px float
     */
    public void setRefractionHeight(float px) {
        float minPx = Utils.dp2px(getResources(), 12);
        float maxPx = Utils.dp2px(getResources(), 50);
        this.refractionHeight = Math.max(minPx, Math.min(maxPx, px));
        updateConfig();
    }

    /**
     * Set the refraction offset px
     * Positive value will be converted to negative
     *
     * @param px float
     */
    public void setRefractionOffset(float px) {
        float minPx = Utils.dp2px(getResources(), 20);
        float maxPx = Utils.dp2px(getResources(), 120);
        px = Math.max(minPx, Math.min(maxPx, px));
        this.refractionOffset = -px;
        updateConfig();
    }

    /**
     * Set the tint color (R)
     *
     * @param red float (0f-1f)
     */
    public void setTintColorRed(float red) {
        this.tintColorRed = red;
        updateConfig();
    }

    /**
     * Set the tint color (G)
     *
     * @param green float (0f-1f)
     */
    public void setTintColorGreen(float green) {
        this.tintColorGreen = green;
        updateConfig();
    }

    /**
     * Set the tint color (B)
     *
     * @param blue float (0f-1f)
     */
    public void setTintColorBlue(float blue) {
        this.tintColorBlue = blue;
        updateConfig();
    }

    /**
     * Set the tint Alpha
     *
     * @param alpha float (0f-1f)
     */
    public void setTintAlpha(float alpha) {
        this.tintAlpha = alpha;
        updateConfig();
    }

    /**
     * Set dispersion
     *
     * @param dispersion float (0f-1f)
     */
    public void setDispersion(float dispersion) {
        this.dispersion = Math.max(0f, Math.min(1f, dispersion));
        updateConfig();
    }

    /**
     * Set the blur radius
     *
     * @param radius float
     */
    public void setBlurRadius(float radius) {
        this.blurRadius = Math.max(0.01f, Math.min(50, radius));
        updateConfig();
    }

    /**
     * Set whether the View is draggable or not
     *
     * @param enabled boolean
     */
    public void setDraggableEnabled(boolean enabled) {
        this.draggableEnabled = enabled;
        if (!enabled) {
            liquidTracker.recycle();
        }
    }

    /**
     * Set whether elastic effect is needed or not
     * @param enabled boolean
     */
    public void setElasticEnabled(boolean enabled) {
        this.elasticEnabled = enabled;
        if (!enabled) {
            liquidTracker.recycle();
        }
    }

    /**
     * Set whether the touch effect (iOS style press animation) is enabled
     * @param enabled boolean
     */
    public void setTouchEffectEnabled(boolean enabled) {
        this.touchEffectEnabled = enabled;
    }

    private void updateConfig() {
        if (glass == null) {
            rebuild();
            return;
        }

        int w = getWidth();
        int h = getHeight();
        if (w <= 0) w = Utils.getDeviceWidthPx(context);
        if (h <= 0) h = getResources().getDisplayMetrics().heightPixels;

        config.CORNER_RADIUS_PX = cornerRadius;
        config.REFRACTION_HEIGHT = refractionHeight;
        config.REFRACTION_OFFSET = refractionOffset;
        config.BLUR_RADIUS = blurRadius;
        config.WIDTH = w;
        config.HEIGHT = h;
        config.DISPERSION = dispersion;
        config.TINT_ALPHA = tintAlpha;
        config.TINT_COLOR_BLUE = tintColorBlue;
        config.TINT_COLOR_GREEN = tintColorGreen;
        config.TINT_COLOR_RED = tintColorRed;

        glass.post(() -> glass.updateParameters());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(this::ensureGlass);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeGlass();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            if (w > 0 && h > 0) {
                float maxPx = h / 2f;
                if (cornerRadius > maxPx) {
                    cornerRadius = maxPx;
                }
                rebuild();
            }
        }
    }

    private void rebuild() {
        removeGlass();
        post(this::ensureGlass);
    }

    private void ensureGlass() {
        if (glass != null) return;

        int w = getWidth();
        int h = getHeight();
        if (w <= 0) w = Utils.getDeviceWidthPx(context);
        if (h <= 0) h = getResources().getDisplayMetrics().heightPixels;

        config = new Config();
        config.configure(new Config.Overrides()
                .noFilter()
                .contrast(0f)
                .whitePoint(0f)
                .chromaMultiplier(1f)
                .blurRadius(blurRadius)
                .cornerRadius(cornerRadius)
                .refractionHeight(refractionHeight)
                .refractionOffset(refractionOffset)
                .tintAlpha(tintAlpha)
                .tintColorRed(tintColorRed)
                .tintColorGreen(tintColorGreen)
                .tintColorBlue(tintColorBlue)
                .dispersion(dispersion)
                .size(w, h)
        );

        glass = new LiquidGlass(getContext(), config);

        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(glass, lp);

        ViewGroup source = customSource;
        if (source == null && getParent() instanceof ViewGroup) {
            return;
        }
        glass.init(source);
    }

    private void removeGlass() {
        if (glass != null) {
            removeView(glass);
            glass = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(android.view.MotionEvent e) {
        if (!draggableEnabled && !touchEffectEnabled) return super.onTouchEvent(e);
        if (elasticEnabled) liquidTracker.applyMovement(e);

        switch (e.getActionMasked()) {
            case android.view.MotionEvent.ACTION_DOWN:
                if (touchEffectEnabled) {
                    isTouching = true;
                    liquidTracker.animateScale(1.02f);

                    glowX = e.getX();
                    glowY = e.getY();
                    invalidate();
                }

                if (draggableEnabled) {
                    downX = e.getRawX();
                    downY = e.getRawY();
                    startTx = getTranslationX();
                    startTy = getTranslationY();
                    return true;
                }
                break;
            case android.view.MotionEvent.ACTION_MOVE: {
                if (touchEffectEnabled) {
                    glowX = e.getX();
                    glowY = e.getY();
                    invalidate();
                }

                if (draggableEnabled) {
                    float dx = e.getRawX() - downX;
                    float dy = e.getRawY() - downY;
                    float tx = startTx + dx;
                    float ty = startTy + dy;

                    ViewGroup parent = (ViewGroup) getParent();
                    if (parent != null) {
                        int pw = parent.getWidth(), ph = parent.getHeight();
                        int w = getWidth(), h = getHeight();
                        if (pw > 0 && ph > 0 && w > 0 && h > 0) {
                            float minX = -getLeft();
                            float maxX = pw - getLeft() - w;
                            float minY = -getTop();
                            float maxY = ph - getTop() - h;
                            if (tx < minX) tx = minX;
                            if (tx > maxX) tx = maxX;
                            if (ty < minY) ty = minY;
                            if (ty > maxY) ty = maxY;
                        }
                    }
                    setTranslationX(tx);
                    setTranslationY(ty);
                    return true;
                }
                break;
            }
            case android.view.MotionEvent.ACTION_UP:
            case android.view.MotionEvent.ACTION_CANCEL:
                if (touchEffectEnabled) {
                    isTouching = false;
                    liquidTracker.animateScale(1f);
                    invalidate();
                }
                if (draggableEnabled) return true;
                break;
        }

        boolean superResult = super.onTouchEvent(e);
        return touchEffectEnabled || superResult;
    }
}