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

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.qmdeve.liquidglass.demo.util.Utils;
import com.qmdeve.liquidglass.widget.LiquidGlassView;

public class TouchEffectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_touch_effect);
        Utils.transparentStatusBar(getWindow());
        Utils.transparentNavigationBar(getWindow());

        LiquidGlassView liquidGlassView = findViewById(R.id.liquid_glass_view);
        android.view.ViewGroup contentContainer = findViewById(R.id.content_container);
        
        // Bind the content to be blurred
        liquidGlassView.bind(contentContainer);
        
        // Enable the touch effect (iOS style animation)
        liquidGlassView.setTouchEffectEnabled(true);
        
        // Optional: Configure other properties for better look
        liquidGlassView.setCornerRadius(Utils.dp2px(getResources(), 40));
        liquidGlassView.setBlurRadius(15f);
    }
}