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

package com.qmdeve.liquidglass.impl;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.RuntimeShader;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.qmdeve.liquidglass.Config;
import com.qmdeve.liquidglass.R;

import org.intellij.lang.annotations.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public final class LiquidGlassimpl implements Impl {

    private final View host, target;
    private final RenderNode node;
    private RenderEffect cachedBlurEffect;
    private final int[] tp = new int[2];
    private final int[] hp = new int[2];
    private final RuntimeShader liquidShader;
    private float lastCornerRadius, lastEccentricFactor, lastRefractionHeight, lastRefractionAmount,
            lastContrast, lastWhitePoint, lastChromaMultiplier, lastSigma,
            lastChromaticAberration, lastDepthEffect, lastBlurLevel,
            lastTintRed, lastTintGreen, lastTintBlue, lastTintAlpha, lastBorderWidth;

    private boolean needsUpdate = true;
    private long lastBlurUpdateTime = 0;
    private long lastRecordTime = 0;
    private static final long MIN_RECORD_INTERVAL_MS = 16;
    private static final long MIN_BLUR_UPDATE_INTERVAL_MS = 100;
    private final Config config;

    public LiquidGlassimpl(View host, View target, Config config) {
        this.host = host;
        this.target = target;
        this.config = config;
        this.node = new RenderNode("AndroidLiquidGlassView");
        this.liquidShader = loadAgsl(target.getResources(), R.raw.liquidglass_effect);

        lastCornerRadius = Float.NaN;
        lastEccentricFactor = Float.NaN;
        lastRefractionHeight = Float.NaN;
        lastRefractionAmount = Float.NaN;
        lastContrast = Float.NaN;
        lastWhitePoint = Float.NaN;
        lastChromaMultiplier = Float.NaN;
        lastSigma = Float.NaN;
        lastBlurLevel = Float.NaN;
        lastChromaticAberration = Float.NaN;
        lastDepthEffect = Float.NaN;
        lastTintRed = Float.NaN;
        lastTintGreen = Float.NaN;
        lastTintBlue = Float.NaN;
        lastTintAlpha = Float.NaN;
        lastBorderWidth = Float.NaN;

        host.post(this::applyRenderEffect);
    }

    @Override
    public void onSizeChanged(int w, int h) {
        node.setPosition(0, 0, w, h);
        record();
        applyRenderEffect();
    }

    @Override
    public void onPreDraw() {
        long now = System.currentTimeMillis();
        if (now - lastRecordTime >= MIN_RECORD_INTERVAL_MS) {
            record();
            lastRecordTime = now;
        }

        float cornerRadius = config.CORNER_RADIUS_PX;
        float eccentricFactor = config.ECCENTRIC_FACTOR;
        float refractionHeight = config.REFRACTION_HEIGHT;
        float refractionAmount = config.REFRACTION_OFFSET;
        float contrast = config.CONTRAST;
        float whitePoint = config.WHITE_POINT;
        float chromaMultiplier = config.CHROMA_MULTIPLIER;
        float blurLevel = config.BLUR_RADIUS;
        float chromaticAberration = config.DISPERSION;
        float depthEffect = config.DEPTH_EFFECT;
        float tintRed = config.TINT_COLOR_RED;
        float tintGreen = config.TINT_COLOR_GREEN;
        float tintBlue = config.TINT_COLOR_BLUE;
        float tintAlpha = config.TINT_ALPHA;
        float borderWidth = config.BORDER_WIDTH;

        boolean paramsChanged = needsUpdate;
        if (!paramsChanged) {
            paramsChanged = (Math.abs(lastCornerRadius - cornerRadius) > 0.1f) ||
                    (Math.abs(lastEccentricFactor - eccentricFactor) > 0.001f) ||
                    (Math.abs(lastRefractionHeight - refractionHeight) > 0.1f) ||
                    (Math.abs(lastRefractionAmount - refractionAmount) > 0.1f) ||
                    (Math.abs(lastContrast - contrast) > 0.001f) ||
                    (Math.abs(lastWhitePoint - whitePoint) > 0.001f) ||
                    (Math.abs(lastChromaMultiplier - chromaMultiplier) > 0.001f) ||
                    (Math.abs(lastBlurLevel - blurLevel) > 0.01f) ||
                    (Math.abs(lastChromaticAberration - chromaticAberration) > 0.001f) ||
                    (Math.abs(lastDepthEffect - depthEffect) > 0.001f) ||
                    (Math.abs(lastTintRed - tintRed) > 0.001f) ||
                    (Math.abs(lastTintGreen - tintGreen) > 0.001f) ||
                    (Math.abs(lastTintBlue - tintBlue) > 0.001f) ||
                    (Math.abs(lastTintAlpha - tintAlpha) > 0.001f) ||
                    (Math.abs(lastBorderWidth - borderWidth) > 0.1f);
        }

        if (paramsChanged) {
            lastCornerRadius = cornerRadius;
            lastEccentricFactor = eccentricFactor;
            lastRefractionHeight = refractionHeight;
            lastRefractionAmount = refractionAmount;
            lastContrast = contrast;
            lastWhitePoint = whitePoint;
            lastChromaMultiplier = chromaMultiplier;
            lastBlurLevel = blurLevel;
            lastChromaticAberration = chromaticAberration;
            lastDepthEffect = depthEffect;
            lastTintRed = tintRed;
            lastTintGreen = tintGreen;
            lastTintBlue = tintBlue;
            lastTintAlpha = tintAlpha;
            lastBorderWidth = borderWidth;
            needsUpdate = false;
            applyRenderEffect();
        }
    }

    private void record() {
        if (target.getVisibility() != View.VISIBLE || host.getVisibility() != View.VISIBLE) {
            return;
        }
        int w = target.getWidth(), h = target.getHeight();
        if (w == 0 || h == 0) return;
        
        target.getLocationInWindow(tp);
        host.getLocationInWindow(hp);
        int dx = hp[0] - tp[0];
        int dy = hp[1] - tp[1];
        
        if (dx == 0 && dy == 0 && w == host.getWidth() && h == host.getHeight()) {
            Canvas rec = node.beginRecording(w, h);
            if (rec != null) {
                target.draw(rec);
            }
        } else {
            Canvas rec = node.beginRecording(w, h);
            if (rec != null) {
                rec.translate(-dx, -dy);
                target.draw(rec);
            }
        }
        node.endRecording();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!canvas.isHardwareAccelerated()) {
            return;
        }
        if (node.hasDisplayList()) {
            canvas.drawRenderNode(node);
        }
    }

    private void applyRenderEffect() {
        int width = target.getWidth();
        int height = target.getHeight();
        if (width == 0 || height == 0) return;

        float cornerRadiusPx = config.CORNER_RADIUS_PX;
        float refractionHeight = config.REFRACTION_HEIGHT;
        float refractionAmount = config.REFRACTION_OFFSET;
        float contrast = config.CONTRAST;
        float whitePoint = config.WHITE_POINT;
        float chromaMultiplier = config.CHROMA_MULTIPLIER;
        float blurLevel = Math.max(0f, config.BLUR_RADIUS);
        float chromaticAberration = config.DISPERSION;
        float depthEffect = config.DEPTH_EFFECT;
        float tintRed = config.TINT_COLOR_RED;
        float tintGreen = config.TINT_COLOR_GREEN;
        float tintBlue = config.TINT_COLOR_BLUE;
        float tintAlpha = config.TINT_ALPHA;
        float[] size = new float[]{config.WIDTH, config.HEIGHT};
        float[] offset = new float[]{0f, 0f};
        float[] cornerRadii = new float[]{
                cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx
        };

        RenderEffect contentEffect = null;
        if (blurLevel > 0.01f) {
            long now = System.currentTimeMillis();
            if (cachedBlurEffect == null || Math.abs(blurLevel - lastSigma) > 0.3f || now - lastBlurUpdateTime > MIN_BLUR_UPDATE_INTERVAL_MS) {
                try {
                    contentEffect = RenderEffect.createBlurEffect(blurLevel, blurLevel, Shader.TileMode.CLAMP);
                    cachedBlurEffect = contentEffect;
                    lastSigma = blurLevel;
                    lastBlurUpdateTime = now;
                } catch (Exception e) {
                    contentEffect = cachedBlurEffect;
                }
            } else {
                contentEffect = cachedBlurEffect;
            }
        }

        liquidShader.setFloatUniform("size", size);
        liquidShader.setFloatUniform("offset", offset);
        liquidShader.setFloatUniform("cornerRadii", cornerRadii);
        liquidShader.setFloatUniform("refractionHeight", refractionHeight);
        liquidShader.setFloatUniform("refractionAmount", refractionAmount);
        liquidShader.setFloatUniform("depthEffect", depthEffect);
        liquidShader.setFloatUniform("chromaticAberration", chromaticAberration);
        liquidShader.setFloatUniform("contrast", contrast);
        liquidShader.setFloatUniform("whitePoint", whitePoint);
        liquidShader.setFloatUniform("chromaMultiplier", chromaMultiplier);
        liquidShader.setFloatUniform("tintColor", new float[]{tintRed, tintGreen, tintBlue});
        liquidShader.setFloatUniform("tintAlpha", tintAlpha);
        liquidShader.setFloatUniform("borderWidth", config.BORDER_WIDTH);

        RenderEffect shaderEffect = RenderEffect.createRuntimeShaderEffect(liquidShader, "content");
        RenderEffect finalEffect = (contentEffect != null)
                ? RenderEffect.createChainEffect(shaderEffect, contentEffect)
                : shaderEffect;

        node.setRenderEffect(finalEffect);
    }

    private RuntimeShader loadAgsl(Resources resources, int resourceId) {
        @Language("AGSL")
        String shaderCode = loadRaw(resources, resourceId);
        return new RuntimeShader(shaderCode);
    }

    private String loadRaw(Resources resources, int resourceId) {
        try (InputStream inputStream = resources.openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error loading shader: " + resourceId, e);
        }
    }
}