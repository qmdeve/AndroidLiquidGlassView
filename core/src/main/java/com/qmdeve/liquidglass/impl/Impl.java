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

package com.qmdeve.liquidglass.impl;

import android.graphics.Canvas;

public interface Impl {
    void onSizeChanged(int w, int h);
    void onPreDraw();
    void draw(Canvas c);
    default void dispose() {}
}