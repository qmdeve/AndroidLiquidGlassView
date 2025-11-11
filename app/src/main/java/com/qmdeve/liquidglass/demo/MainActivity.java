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

package com.qmdeve.liquidglass.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.qmdeve.liquidglass.demo.util.Utils;
import com.qmdeve.liquidglass.widget.LiquidGlassView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_main);

        Utils.transparentStatusBar(getWindow());
        Utils.transparentNavigationBar(getWindow());

        liquidGlassView = findViewById(R.id.liquidGlassView);
        ViewGroup content = findViewById(R.id.content_container);
        liquidGlassView.bind(content);

        initView();
        setView();

        new MaterialAlertDialogBuilder(this)
                .setTitle("Hello")
                .setMessage(getString(R.string.a2))
                .setNegativeButton("OK", null)
                .show();
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