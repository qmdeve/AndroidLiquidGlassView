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

import androidx.annotation.Nullable;

public class Config {
    public float DISPERSION, DEPTH_EFFECT = 0.3f;
    public int WIDTH, HEIGHT;
    public volatile float CORNER_RADIUS_PX;
    public volatile float ECCENTRIC_FACTOR = 1.0f;
    public volatile float REFRACTION_HEIGHT;
    public volatile float REFRACTION_OFFSET;
    public volatile float CONTRAST;
    public volatile float WHITE_POINT;
    public volatile float CHROMA_MULTIPLIER;
    public volatile float BLUR_RADIUS;
    public float TINT_ALPHA, TINT_COLOR_RED, TINT_COLOR_GREEN, TINT_COLOR_BLUE;

    public void configure(@Nullable Overrides overrides) {
        if (overrides != null) overrides.apply(this);
    }

    public static final class Overrides {
        Float cornerRadius, refractionHeight, refractionOffset, contrast, whitePoint, chromaMultiplier, blurRadius, tintAlpha, tintColorRed, tintColorGreen, tintColorBlue, dispersion;
        Integer width, height;

        public Overrides tintAlpha(float v) {
            tintAlpha = v;
            return this;
        }

        public Overrides tintColorRed(float v) {
            tintColorRed = v;
            return this;
        }

        public Overrides tintColorGreen(float v) {
            tintColorGreen = v;
            return this;
        }

        public Overrides tintColorBlue(float v) {
            tintColorBlue = v;
            return this;
        }

        public Overrides noFilter() {
            contrast(0f);
            whitePoint(0f);
            chromaMultiplier(1f);
            blurRadius(0f);
            refractionHeight(0f);
            refractionOffset(0f);

            return this;
        }

        public Overrides cornerRadius(float v) {
            cornerRadius = v;
            return this;
        }

        public Overrides refractionHeight(float v) {
            refractionHeight = v;
            return this;
        }

        public Overrides refractionOffset(float v) {
            refractionOffset = v;
            return this;
        }

        public Overrides contrast(float v) {
            contrast = v;
            return this;
        }

        public Overrides whitePoint(float v) {
            whitePoint = v;
            return this;
        }

        public Overrides chromaMultiplier(float v) {
            chromaMultiplier = v;
            return this;
        }

        public Overrides blurRadius(float v) {
            blurRadius = v;
            return this;
        }

        public Overrides dispersion(float v) {
            dispersion = v;
            return this;
        }

        public Overrides size(int w, int h) {
            width = w;
            height = h;
            return this;
        }

        void apply(Config c) {
            if (cornerRadius != null) c.CORNER_RADIUS_PX = cornerRadius;
            if (refractionHeight != null) c.REFRACTION_HEIGHT = refractionHeight;
            if (refractionOffset != null) c.REFRACTION_OFFSET = refractionOffset;
            if (contrast != null) c.CONTRAST = contrast;
            if (whitePoint != null) c.WHITE_POINT = whitePoint;
            if (chromaMultiplier != null) c.CHROMA_MULTIPLIER = chromaMultiplier;
            if (blurRadius != null) c.BLUR_RADIUS = blurRadius;
            if (width != null) c.WIDTH = width;
            if (height != null) c.HEIGHT = height;
            if (tintAlpha != null) c.TINT_ALPHA = tintAlpha;
            if (tintColorRed != null) c.TINT_COLOR_RED = tintColorRed;
            if (tintColorGreen != null) c.TINT_COLOR_GREEN = tintColorGreen;
            if (tintColorBlue != null) c.TINT_COLOR_BLUE = tintColorBlue;
            if (dispersion != null) c.DISPERSION = dispersion;
        }
    }
}