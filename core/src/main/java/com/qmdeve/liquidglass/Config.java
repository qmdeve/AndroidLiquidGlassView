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