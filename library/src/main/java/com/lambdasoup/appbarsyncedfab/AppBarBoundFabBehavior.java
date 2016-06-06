/*
 * Copyright 2016 Juliane Lehmann <jl@lambdasoup.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lambdasoup.appbarsyncedfab;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Behavior for FABs that does not support anchoring to AppBarLayout, but instead translates the FAB
 * out of the bottom in sync with the AppBarLayout collapsing towards the top.
 * <p>
 * Extends FloatingActionButton.Behavior to keep using the pre-Lollipop shadow padding offset.
 */
public class AppBarBoundFabBehavior extends FloatingActionButton.Behavior {

    public AppBarBoundFabBehavior(Context context, AttributeSet attrs) {
        super();
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new FabOffsetter(parent, child));
        }
        return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        //noinspection SimplifiableIfStatement
        if (dependency instanceof AppBarLayout) {
            // if the dependency is an AppBarLayout, do not allow super to react on that
            // we don't want that behavior
            return true;
        }
        return super.onDependentViewChanged(parent, fab, dependency);
    }
}
