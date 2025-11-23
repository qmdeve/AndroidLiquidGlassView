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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.qmdeve.liquidglass.demo.util.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Utils.transparentStatusBar(getWindow());
        Utils.transparentNavigationBar(getWindow());

        new MaterialAlertDialogBuilder(this)
                .setTitle("Hello")
                .setMessage(getString(R.string.a2))
                .setNegativeButton("OK", null)
                .show();

        findViewById(R.id.liquidglassview).setOnClickListener(v -> startActivity(new Intent(this, LiquidGlassViewActivity.class)));
        findViewById(R.id.elasticliquidglassview).setOnClickListener(v -> startActivity(new Intent(this, ElasticLiquidGlassViewActivity.class)));
        findViewById(R.id.toucheffectview).setOnClickListener(v -> startActivity(new Intent(this, TouchEffectActivity.class)));
        findViewById(R.id.github).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/QmDeve/AndroidLiquidGlassView"))));
    }
}