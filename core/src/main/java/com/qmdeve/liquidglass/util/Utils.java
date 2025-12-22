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

package com.qmdeve.liquidglass.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.WindowMetrics;

public class Utils {

    public static int getDeviceWidthPx(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = context.getSystemService(WindowManager.class);
            if (wm != null) {
                WindowMetrics metrics = wm.getCurrentWindowMetrics();
                Rect bounds = metrics.getBounds();
                return bounds.width();
            }
        }

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static float dp2px(Resources resources, float value) {
        if (value == 0) {
            return 0;
        }
        float density = resources.getDisplayMetrics().density;
        return density * value;
    }
}
