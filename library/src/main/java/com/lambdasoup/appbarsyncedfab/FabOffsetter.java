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

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import static com.lambdasoup.appbarsyncedfab.Nullables.coalesce;

public class FabOffsetter implements AppBarLayout.OnOffsetChangedListener {
    private final CoordinatorLayout parent;
    private final FloatingActionButton fab;

    public FabOffsetter(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton child) {
        this.parent = parent;
        this.fab = child;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // let's see how far along the way the appBarLayout is
        // (if displacementFraction == 0.0f then no displacement, appBar is fully expanded;
        //  if displacementFraction == 1.0f then full displacement, appBar is totally collapsed)
        float displacementFraction = -verticalOffset / (float) appBarLayout.getHeight();

        // need to separate translationY on the fab that comes from this behavior
        // and one that comes from other sources
        // translationY from this behavior is stored in a tag on the fab
        float translationYFromThis = coalesce((Float) fab.getTag(R.id.fab_translationY_from_AppBarBoundFabBehavior), 0f);

        // top position, accounting for translation not coming from this behavior
        float topUntranslatedFromThis = fab.getTop() + fab.getTranslationY() - translationYFromThis;

        // total length to displace by (from position uninfluenced by this behavior) for a full appBar collapse
        float fullDisplacement = parent.getBottom() - topUntranslatedFromThis;

        // calculate and store new value for displacement coming from this behavior
        float newTranslationYFromThis = fullDisplacement * displacementFraction;
        fab.setTag(R.id.fab_translationY_from_AppBarBoundFabBehavior, newTranslationYFromThis);

        // update translation value by difference found in this step
        fab.setTranslationY(newTranslationYFromThis - translationYFromThis + fab.getTranslationY());
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
