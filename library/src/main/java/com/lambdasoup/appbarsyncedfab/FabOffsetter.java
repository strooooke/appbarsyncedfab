/*
 * Copyright 2016-2019 Juliane Lehmann <jl@lambdasoup.com>
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

import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;

/**
 * {@link com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener} implementation
 * that reacts on offset changes by translating the FAB towards the bottom. The FAB gets displaced
 * such that when the AppBarLayout is completely collapsed, then the top of the FAB is at the bottom
 * of the parent view (typically a {@link androidx.coordinatorlayout.widget.CoordinatorLayout). For
 * intermediate states, the fraction of the FAB displacement respective to this total is relative to
 * the fraction of the AppBarLayout collapse.
 */
@SuppressWarnings("WeakerAccess")
public class FabOffsetter implements AppBarLayout.OnOffsetChangedListener {

    private final View parent;
    private final FloatingActionButton fab;

    // need to separate translationY on the fab that comes from this behavior
    // and one that comes from other sources
    private float fabTranslationYByThis = 0.0f;

    public FabOffsetter(@NonNull View parent, @NonNull FloatingActionButton child) {
        this.parent = parent;
        this.fab = child;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // let's see how far along the way the appBarLayout is
        // (if displacementFraction == 0.0f then no displacement, appBar is fully expanded;
        //  if displacementFraction == 1.0f then full displacement, appBar is totally collapsed)
        float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();

        // top position, accounting for translation not coming from this behavior
        float topUntranslatedFromThis = fab.getTop() + fab.getTranslationY() - fabTranslationYByThis;

        // total length to displace by (from position uninfluenced by this behavior) for a full appBar collapse
        float fullDisplacement = parent.getBottom() - topUntranslatedFromThis;

        // calculate new value for displacement coming from this behavior
        float newTranslationYFromThis = fullDisplacement * displacementFraction;

        // update translation value by difference found in this step
        fab.setTranslationY(newTranslationYFromThis - fabTranslationYByThis + fab.getTranslationY());

        // store new value
        fabTranslationYByThis = newTranslationYFromThis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FabOffsetter that = (FabOffsetter) o;

        return parent.equals(that.parent) && fab.equals(that.fab);

    }

    @Override
    public int hashCode() {
        int result = parent.hashCode();
        result = 31 * result + fab.hashCode();
        return result;
    }
}
