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

package com.qmdeve.liquidglass.util;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class LiquidTracker {
    private VelocityTracker velocityTracker;
    private final SpringAnimation springAnimX, springAnimY;
    private final SpringAnimation springAnimRotX, springAnimRotY;
    private final Handler liquidHandler;

    public LiquidTracker(View view) {
        SpringForce springX = new SpringForce();
        springX.setStiffness(180f);
        springX.setDampingRatio(0.35f);
        springAnimX = new SpringAnimation(view, DynamicAnimation.SCALE_X);
        springAnimX.setSpring(springX);

        SpringForce springY = new SpringForce();
        springY.setStiffness(180f);
        springY.setDampingRatio(0.35f);
        springAnimY = new SpringAnimation(view, DynamicAnimation.SCALE_Y);
        springAnimY.setSpring(springY);

        SpringForce springRot = new SpringForce();
        springRot.setStiffness(180f);
        springRot.setDampingRatio(0.5f);
        
        springAnimRotX = new SpringAnimation(view, DynamicAnimation.ROTATION_X);
        springAnimRotX.setSpring(springRot);
        
        springAnimRotY = new SpringAnimation(view, DynamicAnimation.ROTATION_Y);
        springAnimRotY.setSpring(springRot);

        liquidHandler = new Handler(Looper.getMainLooper());
    }

    public void applyMovement(@NonNull MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                ensureAddMovement(e);
                break;
            case MotionEvent.ACTION_MOVE:
                ensureAddMovement(e);

                float[] scaleXY = getLiquidScale();
                animateToFinalPosition(scaleXY[0], scaleXY[1]);

                liquidHandler.removeCallbacksAndMessages(null);
                liquidHandler.postDelayed(() -> {
                    animateToFinalPosition(1f, 1f);
                }, 200);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                recycle();
                animateToFinalPosition(1f, 1f);
                break;
        }
    }

    public void recycle() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private float getVelocity() {
        if (velocityTracker == null)
            return 0f;

        velocityTracker.computeCurrentVelocity(1);
        float velocityX = velocityTracker.getXVelocity();
        float velocityY = velocityTracker.getYVelocity();
        return (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY) * (velocityX > 0f ? 1f : -1f);
    }

    private float[] getLiquidScale() {
        if (velocityTracker == null)
            return new float[] { 1f, 1f };

        velocityTracker.computeCurrentVelocity(1);
        float velocityX = velocityTracker.getXVelocity();
        float velocityY = velocityTracker.getYVelocity();
        float velocity = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        return new float[] {
                Math.clamp(1f + velocity * 0.5f, 0.6f, 1.4f),
                Math.clamp(1f - velocity * 0.5f, 0.6f, 1.4f)
        };
    }

    private void ensureAddMovement(MotionEvent e) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(e);
    }

    public void animateScale(float scale) {
        animateToFinalPosition(scale, scale);
    }

    public void animateTilt(float rotX, float rotY) {
        springAnimRotX.animateToFinalPosition(rotX);
        springAnimRotY.animateToFinalPosition(rotY);
    }

    private void animateToFinalPosition(float x, float y) {
        springAnimX.animateToFinalPosition(x);
        springAnimY.animateToFinalPosition(y);
    }
}