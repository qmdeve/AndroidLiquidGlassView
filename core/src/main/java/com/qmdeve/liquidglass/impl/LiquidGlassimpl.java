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
            lastTintRed, lastTintGreen, lastTintBlue, lastTintAlpha;

    private boolean needsUpdate = true;
    private long lastBlurUpdateTime = 0;
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
        record();

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

        boolean paramsChanged =
                lastCornerRadius != cornerRadius ||
                        lastEccentricFactor != eccentricFactor ||
                        lastRefractionHeight != refractionHeight ||
                        lastRefractionAmount != refractionAmount ||
                        lastContrast != contrast ||
                        lastWhitePoint != whitePoint ||
                        lastChromaMultiplier != chromaMultiplier ||
                        lastBlurLevel != blurLevel ||
                        lastChromaticAberration != chromaticAberration ||
                        lastDepthEffect != depthEffect ||
                        lastTintRed != tintRed ||
                        lastTintGreen != tintGreen ||
                        lastTintBlue != tintBlue ||
                        lastTintAlpha != tintAlpha ||
                        needsUpdate;

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
            needsUpdate = false;
            applyRenderEffect();
        }
    }

    private void record() {
        int w = target.getWidth(), h = target.getHeight();
        if (w == 0 || h == 0) return;

        Canvas rec = node.beginRecording(w, h);
        target.getLocationInWindow(tp);
        host.getLocationInWindow(hp);
        rec.translate(-(hp[0] - tp[0]), -(hp[1] - tp[1]));
        target.draw(rec);
        node.endRecording();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!canvas.isHardwareAccelerated()) return;
        canvas.drawRenderNode(node);
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
            if (cachedBlurEffect == null || Math.abs(blurLevel - lastSigma) > 0.3f || now - lastBlurUpdateTime > 120) {
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