/*
 * MIT License
 *
 * Copyright (c) 2025 QmDeve
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * ===========================================
 * Project: AndroidLiquidGlassView
 * Created Date: 2025-11-01
 * Author: QmDeve
 * GitHub: https://github.com/QmDeve/AndroidLiquidGlassView
 *
 * Contributors:
 * - Donny Yale - https://github.com/QmDeve
 * - Ahmed Sbai - https://github.com/sbaiahmed1
 * ===========================================
 */

package com.qmdeve.liquidglass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.qmdeve.liquidglass.impl.Impl;
import com.qmdeve.liquidglass.impl.LiquidGlassimpl;

import java.lang.ref.WeakReference;

@SuppressLint("ViewConstructor")
public class LiquidGlass extends FrameLayout {
    private Impl impl;
    private ViewGroup target;
    private boolean listenerAdded = false;
    private final Config config;

    private static class PreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private final WeakReference<LiquidGlass> liquidGlassRef;

        public PreDrawListener(LiquidGlass liquidGlass) {
            this.liquidGlassRef = new WeakReference<>(liquidGlass);
        }

        @Override
        public boolean onPreDraw() {
            LiquidGlass liquidGlass = liquidGlassRef.get();
            if (liquidGlass != null && liquidGlass.impl != null) {
                liquidGlass.impl.onPreDraw();
            }
            return true;
        }
    }

    private static class RoundRectOutlineProvider extends ViewOutlineProvider {
        private final float cornerRadius;

        public RoundRectOutlineProvider(float cornerRadius) {
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void getOutline(View v, Outline o) {
            o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), cornerRadius);
        }
    }

    private final PreDrawListener preDrawListener = new PreDrawListener(this);

    public LiquidGlass(Context c, Config config) {
        super(c);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        this.config = config;
        init();
    }

    public void init(ViewGroup target) {
        if (this.target != null) removePreDrawListener();

        this.target = target;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            impl = new LiquidGlassimpl(this, target, config);
            addPreDrawListener();
            requestLayout();
            invalidate();
        } else {
            removePreDrawListener();
        }
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        updateOutlineProvider();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (impl != null) impl.draw(canvas);
    }

    public void updateParameters() {
        if (impl != null) {
            impl.onPreDraw();
            invalidate();
        }
        updateOutlineProvider();
    }

    private void updateOutlineProvider() {
        if (config.CORNER_RADIUS_PX > 0) {
            setOutlineProvider(new RoundRectOutlineProvider(config.CORNER_RADIUS_PX));
            setClipToOutline(true);
            invalidateOutline();
        } else {
            setOutlineProvider(null);
            setClipToOutline(false);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (impl != null) impl.onSizeChanged(w, h);
        updateOutlineProvider();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addPreDrawListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        removePreDrawListener();
        if (impl != null) impl.dispose();
        super.onDetachedFromWindow();
    }

    private void addPreDrawListener() {
        if (target != null && !listenerAdded) {
            target.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            listenerAdded = true;
        }
    }

    private void removePreDrawListener() {
        if (target != null && listenerAdded) {
            target.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
            listenerAdded = false;
        }
    }
}