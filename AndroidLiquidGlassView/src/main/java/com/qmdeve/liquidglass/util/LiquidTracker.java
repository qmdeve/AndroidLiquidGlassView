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