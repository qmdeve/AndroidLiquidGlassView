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

package com.qmdeve.liquidglass.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.qmdeve.liquidglass.demo.util.Utils;
import com.qmdeve.liquidglass.widget.LiquidGlassView;

import java.io.IOException;
import java.io.InputStream;

public class LiquidGlassViewActivity extends AppCompatActivity {

    private LinearLayout controls;
    private LiquidGlassView liquidGlassView;
    private Slider setCorners, setRefractionHeight, setRefractionOffset, setBlurRadius, setDispersion;
    private Button button;
    private ImageView images;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    if (bitmap != null) {
                        images.setImageBitmap(bitmap);
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ignored) {
                }
            }
        });

        setContentView(R.layout.activity_liquid_glass_view);

        Utils.transparentStatusBar(getWindow());
        Utils.transparentNavigationBar(getWindow());

        liquidGlassView = findViewById(R.id.liquidGlassView);
        ViewGroup content = findViewById(R.id.content_container);

        liquidGlassView.bind(content);
        liquidGlassView.setDraggableEnabled(true);
        liquidGlassView.setElasticEnabled(false);

        initView();
        setView();
    }

    private void initView() {
        controls = findViewById(R.id.controls);

        button = findViewById(R.id.button);
        images = findViewById(R.id.images);

        setCorners = findViewById(R.id.setCorner);
        setRefractionHeight = findViewById(R.id.setRefractionHeight);
        setRefractionOffset = findViewById(R.id.setRefractionOffset);
        setBlurRadius = findViewById(R.id.setBlurRadius);
        setDispersion = findViewById(R.id.setDispersion);

        setCorners.setValueFrom(0);
        setCorners.setValueTo(99);
        setCorners.setValue(40);

        setRefractionHeight.setValueFrom(12);
        setRefractionHeight.setValueTo(50);
        setRefractionHeight.setValue(20);

        setRefractionOffset.setValueFrom(20);
        setRefractionOffset.setValueTo(120);
        setRefractionOffset.setValue(70);

        setBlurRadius.setValueFrom(0);
        setBlurRadius.setValueTo(50);
        setBlurRadius.setValue(0);

        setDispersion.setValueFrom(0f);
        setDispersion.setValueTo(1f);
        setDispersion.setValue(0.5f);
    }

    private void setView() {
        ViewGroup.MarginLayoutParams controlsParams = (ViewGroup.MarginLayoutParams) controls.getLayoutParams();
        controlsParams.bottomMargin = Utils.getNavigationBarHeight(findViewById(android.R.id.content));
        controls.setLayoutParams(controlsParams);

        ViewGroup.MarginLayoutParams buttonParams = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
        buttonParams.topMargin = (int) (Utils.getStatusBarHeight(this) + Utils.dp2px(getResources(), 6));
        button.setLayoutParams(buttonParams);

        button.setOnClickListener(v -> {
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            } else {
                Toast.makeText(this, getString(R.string.a1), Toast.LENGTH_SHORT).show();
            }
        });

        setCorners.addOnChangeListener((slider, v, b) -> {
            if (b) {
                liquidGlassView.setCornerRadius(Utils.dp2px(getResources(), v));
            }
        });

        setRefractionHeight.addOnChangeListener((slider, v, b) -> {
            if (b) {
                liquidGlassView.setRefractionHeight(Utils.dp2px(getResources(), v));
            }
        });

        setRefractionOffset.addOnChangeListener((slider, v, b) -> {
            if (b) {
                liquidGlassView.setRefractionOffset(Utils.dp2px(getResources(), v));
            }
        });

        setBlurRadius.addOnChangeListener((slider, v, b) -> {
            if (b) {
                liquidGlassView.setBlurRadius(v);
            }
        });

        setDispersion.addOnChangeListener((slider, v, b) -> {
            if (b) {
                liquidGlassView.setDispersion(v);
            }
        });
    }
}
