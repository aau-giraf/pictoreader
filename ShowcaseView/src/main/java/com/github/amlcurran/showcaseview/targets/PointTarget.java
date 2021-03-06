/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTICE: This file has been modified in order to enable custom size of the showcase and
 * custom positioning of text.
 */

package com.github.amlcurran.showcaseview.targets;

import android.graphics.Point;

/**
 * Showcase a specific x/y co-ordinate on the screen.
 */
public class PointTarget extends Target {

    private final Point mPoint;
    private final float radius;

    public PointTarget(final Point point, final float radius) {
        this(point, radius, 1.0f);
    }

    public PointTarget(final int xValue, final int yValue, final float radius) {
        this(xValue, yValue, radius, 1.0f);
    }

    public PointTarget(final Point point, final float radius, final float scaleMultiplier) {
        super(scaleMultiplier);
        mPoint = point;
        this.radius = radius;
    }

    public PointTarget(final int xValue, final int yValue, final float radius, final float scaleMultiplier) {
        super(scaleMultiplier);
        mPoint = new Point(xValue, yValue);
        this.radius = radius;
    }

    @Override
    public Point getPoint() {
        return mPoint;
    }

    @Override
    public float getRadius() {
        return this.radius;
    }
}
