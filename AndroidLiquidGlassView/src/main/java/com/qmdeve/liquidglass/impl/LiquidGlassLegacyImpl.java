package com.qmdeve.liquidglass.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
    private Bitmap buffer;
    private Bitmap blurredBuffer;
    private RenderNode renderNode;
    private long lastCaptureTime = 0;
    private static final long MIN_CAPTURE_INTERVAL_MS = 16;

    public LiquidGlassLegacyImpl(@NonNull View host, @NonNull View target, @NonNull Config config) {
        this.host = host;
        this.target = target;
        this.config = config;
        tintPaint.setStyle(Paint.Style.FILL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            renderNode = new RenderNode("AndroidLiquidGlassViewLegacy");
        }
    }

    @Override
    public void onSizeChanged(int w, int h) {
        recreateBuffers(w, h);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && renderNode != null) {
            renderNode.setPosition(0, 0, w, h);
        }
    }

    @Override
    public void onPreDraw() {
        long now = System.currentTimeMillis();
        if (now - lastCaptureTime >= MIN_CAPTURE_INTERVAL_MS) {
            captureTarget();
            lastCaptureTime = now;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                applyCpuBlur();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                updateRenderNode();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (buffer == null) return;
        float borderWidth = config.BORDER_WIDTH;
        if (borderWidth > 0.0f) {
            drawWithBorderMask(canvas);
        } else {
            drawFull(canvas);
        }
    }

    private void drawFull(Canvas canvas) {
        if (!canvas.isHardwareAccelerated() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            canvas.drawBitmap(blurredBuffer != null ? blurredBuffer : buffer, 0, 0, null);
            return;
        }
        float tintAlpha = config.TINT_ALPHA;
        float tintRed = config.TINT_COLOR_RED;
        float tintGreen = config.TINT_COLOR_GREEN;
        float tintBlue = config.TINT_COLOR_BLUE;
        tintPaint.setColor(Color.argb(clampColor(tintAlpha), clampColor(tintRed), clampColor(tintGreen), clampColor(tintBlue)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && renderNode != null) {
            canvas.drawRenderNode(renderNode);
        } else {
            canvas.drawBitmap(blurredBuffer != null ? blurredBuffer : buffer, 0, 0, null);
        }
        if (tintAlpha > 0.001f) canvas.drawRect(0, 0, buffer.getWidth(), buffer.getHeight(), tintPaint);
    }

    private void drawWithBorderMask(Canvas canvas) {
        int width = buffer.getWidth();
        int height = buffer.getHeight();
        if (width == 0 || height == 0) return;
        float borderWidth = config.BORDER_WIDTH;
        android.graphics.Paint maskPaint = new android.graphics.Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));
        int savedLayer = canvas.saveLayer(0, 0, width, height, null);
        drawFull(canvas);
        float cornerRadius = config.CORNER_RADIUS_PX;
        android.graphics.Path path = new android.graphics.Path();
        float innerWidth = Math.max(0, width - borderWidth * 2);
        float innerHeight = Math.max(0, height - borderWidth * 2);
        float innerCornerRadius = Math.max(0, cornerRadius - borderWidth);
        android.graphics.RectF innerRect = new android.graphics.RectF(
                borderWidth, borderWidth,
                borderWidth + innerWidth, borderWidth + innerHeight
        );
        path.addRoundRect(innerRect, innerCornerRadius, innerCornerRadius, android.graphics.Path.Direction.CW);
        canvas.drawPath(path, maskPaint);
        canvas.restoreToCount(savedLayer);
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
        if (target.getVisibility() != View.VISIBLE || host.getVisibility() != View.VISIBLE) {
            return;
        }
        int width = host.getWidth();
        int height = host.getHeight();
        if (width == 0 || height == 0) return;
        target.getLocationInWindow(targetPos);
        host.getLocationInWindow(hostPos);
        int dx = hostPos[0] - targetPos[0];
        int dy = hostPos[1] - targetPos[1];
        int hostVisibility = host.getVisibility();
        if (hostVisibility == View.VISIBLE) {
            host.setVisibility(View.INVISIBLE);
        }
        try {
            Canvas rec = new Canvas(buffer);
            rec.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            rec.save();
            rec.translate(-dx, -dy);
            target.draw(rec);
            rec.restore();
        } finally {
            if (hostVisibility == View.VISIBLE) {
                host.setVisibility(hostVisibility);
            }
        }
    }

    private void applyCpuBlur() {
        if (blurredBuffer == null || buffer == null) return;
        copyBitmap(buffer, blurredBuffer);
        int radius = Math.min(25, Math.max(0, Math.round(config.BLUR_RADIUS)));
        if (radius == 0) return;
        boxBlur(blurredBuffer, radius);
    }

    private void updateRenderNode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || renderNode == null || buffer == null) return;
        int w = buffer.getWidth();
        int h = buffer.getHeight();
        if (w == 0 || h == 0) return;
        Canvas rec = renderNode.beginRecording(w, h);
        if (rec != null) {
            rec.drawBitmap(buffer, 0, 0, blurPaint);
        }
        renderNode.endRecording();
        float sigma = Math.max(0f, config.BLUR_RADIUS);
        RenderEffect effect = sigma > 0.01f ? RenderEffect.createBlurEffect(sigma, sigma, Shader.TileMode.CLAMP) : null;
        renderNode.setRenderEffect(effect);
    }

    private void boxBlur(Bitmap bmp, int radius) {
        if (radius <= 0) return;
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] temp = new int[w * h];
        boxBlurHorizontal(pixels, temp, w, h, radius);
        boxBlurVertical(temp, pixels, w, h, radius);
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    private void boxBlurHorizontal(int[] src, int[] dst, int w, int h, int radius) {
        for (int y = 0; y < h; y++) {
            int idx = y * w;
            int sumR = 0, sumG = 0, sumB = 0, sumA = 0;
            int count = 0;
            for (int i = -radius; i <= radius; i++) {
                int px = clamp(i, 0, w - 1);
                int pix = src[idx + px];
                sumA += (pix >>> 24) & 0xFF;
                sumR += (pix >> 16) & 0xFF;
                sumG += (pix >> 8) & 0xFF;
                sumB += pix & 0xFF;
                count++;
            }
            for (int x = 0; x < w; x++) {
                dst[idx + x] = ((sumA / count) << 24) | ((sumR / count) << 16) | ((sumG / count) << 8) | (sumB / count);
                int left = x - radius;
                int right = x + radius + 1;
                if (left >= 0) {
                    int pix = src[idx + left];
                    sumA -= (pix >>> 24) & 0xFF;
                    sumR -= (pix >> 16) & 0xFF;
                    sumG -= (pix >> 8) & 0xFF;
                    sumB -= pix & 0xFF;
                    count--;
                }
                if (right < w) {
                    int pix = src[idx + right];
                    sumA += (pix >>> 24) & 0xFF;
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                    count++;
                } else if (left < 0) {
                    int pix = src[idx + clamp(x, 0, w - 1)];
                    sumA += (pix >>> 24) & 0xFF;
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                    count++;
                }
            }
        }
    }

    private void boxBlurVertical(int[] src, int[] dst, int w, int h, int radius) {
        for (int x = 0; x < w; x++) {
            int sumR = 0, sumG = 0, sumB = 0, sumA = 0;
            int count = 0;
            for (int i = -radius; i <= radius; i++) {
                int py = clamp(i, 0, h - 1);
                int pix = src[py * w + x];
                sumA += (pix >>> 24) & 0xFF;
                sumR += (pix >> 16) & 0xFF;
                sumG += (pix >> 8) & 0xFF;
                sumB += pix & 0xFF;
                count++;
            }
            for (int y = 0; y < h; y++) {
                dst[y * w + x] = ((sumA / count) << 24) | ((sumR / count) << 16) | ((sumG / count) << 8) | (sumB / count);
                int top = y - radius;
                int bottom = y + radius + 1;
                if (top >= 0) {
                    int pix = src[top * w + x];
                    sumA -= (pix >>> 24) & 0xFF;
                    sumR -= (pix >> 16) & 0xFF;
                    sumG -= (pix >> 8) & 0xFF;
                    sumB -= pix & 0xFF;
                    count--;
                }
                if (bottom < h) {
                    int pix = src[bottom * w + x];
                    sumA += (pix >>> 24) & 0xFF;
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                    count++;
                } else if (top < 0) {
                    int pix = src[clamp(y, 0, h - 1) * w + x];
                    sumA += (pix >>> 24) & 0xFF;
                    sumR += (pix >> 16) & 0xFF;
                    sumG += (pix >> 8) & 0xFF;
                    sumB += pix & 0xFF;
                    count++;
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
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColorFilter(new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC));
        c.drawBitmap(src, 0, 0, null);
    }
}

