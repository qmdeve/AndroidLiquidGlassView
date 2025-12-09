package com.qmdeve.liquidglass.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.qmdeve.liquidglass.Config;

public final class LiquidGlassLegacyImpl implements Impl {
    private final View host, target;
    private final Config config;
    private final Paint blurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] targetPos = new int[2];
    private final int[] hostPos = new int[2];
    private final RenderNode renderNode;
    private Bitmap buffer;
    private Bitmap blurredBuffer;

    public LiquidGlassLegacyImpl(@NonNull View host, @NonNull View target, @NonNull Config config) {
        this.host = host;
        this.target = target;
        this.config = config;
        tintPaint.setStyle(Paint.Style.FILL);
        renderNode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? new RenderNode("AndroidLiquidGlassViewLegacy") : null;
    }

    @Override
    public void onSizeChanged(int w, int h) {
        recreateBuffers(w, h);
        if (renderNode != null) renderNode.setPosition(0, 0, w, h);
    }

    @Override
    public void onPreDraw() {
        captureTarget();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            applyCpuBlur();
        } else {
            updateRenderNode();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (buffer == null) return;
        float tintAlpha = config.TINT_ALPHA;
        float tintRed = config.TINT_COLOR_RED;
        float tintGreen = config.TINT_COLOR_GREEN;
        float tintBlue = config.TINT_COLOR_BLUE;
        tintPaint.setColor(Color.argb(clampColor(tintAlpha), clampColor(tintRed), clampColor(tintGreen), clampColor(tintBlue)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (renderNode != null) canvas.drawRenderNode(renderNode);
        } else {
            canvas.drawBitmap(blurredBuffer != null ? blurredBuffer : buffer, 0, 0, null);
        }
        if (tintAlpha > 0.001f) canvas.drawRect(0, 0, buffer.getWidth(), buffer.getHeight(), tintPaint);
    }

    @Override
    public void dispose() {
        recycleBuffer(buffer);
        recycleBuffer(blurredBuffer);
    }

    private void recreateBuffers(int w, int h) {
        recycleBuffer(buffer);
        recycleBuffer(blurredBuffer);
        if (w > 0 && h > 0) {
            buffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            blurredBuffer = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ? Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) : null;
        }
    }

    private void captureTarget() {
        if (buffer == null) return;
        int width = target.getWidth();
        int height = target.getHeight();
        if (width == 0 || height == 0) return;
        target.getLocationInWindow(targetPos);
        host.getLocationInWindow(hostPos);
        int dx = hostPos[0] - targetPos[0];
        int dy = hostPos[1] - targetPos[1];
        Canvas rec = new Canvas(buffer);
        rec.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        rec.save();
        rec.translate(-dx, -dy);
        target.draw(rec);
        rec.restore();
    }

    private void applyCpuBlur() {
        if (blurredBuffer == null || buffer == null) return;
        copyBitmap(buffer, blurredBuffer);
        int radius = Math.min(25, Math.max(0, Math.round(config.BLUR_RADIUS)));
        if (radius == 0) return;
        boxBlur(blurredBuffer, radius);
    }

    private void updateRenderNode() {
        if (renderNode == null || buffer == null) return;
        int w = buffer.getWidth();
        int h = buffer.getHeight();
        if (w == 0 || h == 0) return;
        Canvas rec = renderNode.beginRecording(w, h);
        rec.drawBitmap(buffer, 0, 0, blurPaint);
        renderNode.endRecording();
        float sigma = Math.max(0f, config.BLUR_RADIUS);
        RenderEffect effect = sigma > 0.01f ? RenderEffect.createBlurEffect(sigma, sigma, Shader.TileMode.CLAMP) : null;
        renderNode.setRenderEffect(effect);
    }

    private void boxBlur(Bitmap bmp, int radius) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] temp = new int[w * h];
        boxBlurPass(pixels, temp, w, h, radius);
        boxBlurPass(temp, pixels, w, h, radius);
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    private void boxBlurPass(int[] src, int[] dst, int w, int h, int radius) {
        int div = radius * 2 + 1;
        int sumR, sumG, sumB, idx, pix;
        for (int y = 0; y < h; y++) {
            sumR = sumG = sumB = 0;
            idx = y * w;
            for (int i = -radius; i <= radius; i++) {
                pix = src[idx + clamp(i, 0, w - 1)];
                sumR += (pix >> 16) & 0xFF;
                sumG += (pix >> 8) & 0xFF;
                sumB += pix & 0xFF;
            }
            for (int x = 0; x < w; x++) {
                dst[idx + x] = (0xFF << 24) | ((sumR / div) << 16) | ((sumG / div) << 8) | (sumB / div);
                int next = x + radius + 1;
                int prev = x - radius;
                if (next < w) {
                    pix = src[idx + next];
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                }
                if (prev >= 0) {
                    pix = src[idx + prev];
                    sumR -= (pix >> 16) & 0xFF;
                    sumG -= (pix >> 8) & 0xFF;
                    sumB -= pix & 0xFF;
                }
            }
        }
        for (int x = 0; x < w; x++) {
            sumR = sumG = sumB = 0;
            idx = x;
            for (int i = -radius; i <= radius; i++) {
                pix = src[clamp(i, 0, h - 1) * w + x];
                sumR += (pix >> 16) & 0xFF;
                sumG += (pix >> 8) & 0xFF;
                sumB += pix & 0xFF;
            }
            for (int y = 0; y < h; y++) {
                dst[y * w + x] = (0xFF << 24) | ((sumR / div) << 16) | ((sumG / div) << 8) | (sumB / div);
                int next = y + radius + 1;
                int prev = y - radius;
                if (next < h) {
                    pix = src[next * w + x];
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                }
                if (prev >= 0) {
                    pix = src[prev * w + x];
                    sumR -= (pix >> 16) & 0xFF;
                    sumG -= (pix >> 8) & 0xFF;
                    sumB -= pix & 0xFF;
                }
            }
        }
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private int clampColor(float c) {
        return Math.max(0, Math.min(255, Math.round(c * 255f)));
    }

    private void recycleBuffer(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) bmp.recycle();
    }

    private void copyBitmap(Bitmap src, Bitmap dst) {
        if (src == null || dst == null) return;
        Canvas c = new Canvas(dst);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        c.drawBitmap(src, 0, 0, null);
    }
}

