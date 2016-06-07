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

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Behavior for FABs that does not support anchoring to AppBarLayout, but instead translates the FAB
 * out of the bottom in sync with the AppBarLayout collapsing towards the top.
 * <p/>
 * Extends FloatingActionButton.Behavior to keep using the pre-Lollipop shadow padding offset.
 * <p/>
 * Replaces inbuilt Snackbar displacement by a relative version that does not interfere with other
 * sources of translation for the FAB; in particular not translation from the sync to the scrolling AppBarLayout.
 */
public class AppBarBoundFabBehavior extends FloatingActionButton.Behavior {

    private static final String TAG = AppBarBoundFabBehavior.class.getSimpleName();

    // Whether we already registered our OnOffsetChangedListener with the AppBarLayout
    // Does not get saved in instance state, because AppBarLayout does not save its listeners either
    private boolean listenerRegistered = false;


    private ValueAnimator snackbarFabTranslationYAnimator;
    // respect that other code may also change y translation; keep track of the part coming from us
    private float snackbarFabTranslationYByThis;

    public AppBarBoundFabBehavior(Context context, AttributeSet attrs) {
        super();
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout && !listenerRegistered) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new FabOffsetter(parent, child));
            listenerRegistered = true;
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
        } else if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(parent, fab, dependency);
            return true;
        }
        return super.onDependentViewChanged(parent, fab, dependency);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child,
                                       View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(parent, child, dependency);
        }
    }

    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
                                                 final FloatingActionButton fab, View snackbar) {

        // We want to introduce additional y-translation (with respect to what's already there),
        // by the current visible height of any snackbar
        final float targetTransYByThis = getVisibleHeightOfOverlappingSnackbar(parent, fab);

        if (snackbarFabTranslationYByThis == targetTransYByThis) {
            // We're already at (or currently animating to) the target value, return...
            return;
        }

        final float currentTransY = ViewCompat.getTranslationY(fab);

        // Calculate difference between what we want now and what we wanted earlier
        final float stepTransYDelta = targetTransYByThis - snackbarFabTranslationYByThis;

        // ... and we're going to change the current state just by the difference
        final float targetTransY = currentTransY + stepTransYDelta;

        // Make sure that any current animation is cancelled
        if (snackbarFabTranslationYAnimator != null && snackbarFabTranslationYAnimator.isRunning()) {
            snackbarFabTranslationYAnimator.cancel();
        }

        if (fab.isShown()
                && Math.abs(currentTransY - targetTransY) > (fab.getHeight() * 0.667f)) {
            // If the FAB will be travelling by more than 2/3 of it's height, let's animate
            // it instead
            if (snackbarFabTranslationYAnimator == null) {
                snackbarFabTranslationYAnimator = ValueAnimator.ofFloat(currentTransY, targetTransY);
                snackbarFabTranslationYAnimator.setInterpolator(new FastOutSlowInInterpolator());
                snackbarFabTranslationYAnimator.addUpdateListener(
                        new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                ViewCompat.setTranslationY(fab, (Float) animator.getAnimatedValue());
                            }
                        });
            }
            snackbarFabTranslationYAnimator.start();
        } else {
            // Now update the translation Y
            ViewCompat.setTranslationY(fab, targetTransY);
        }

        snackbarFabTranslationYByThis = targetTransYByThis;
    }

    /**
     * returns visible height of snackbar, if snackbar is overlapping fab
     * 0 otherwise
     */
    private float getVisibleHeightOfOverlappingSnackbar(CoordinatorLayout parent,
                                                        FloatingActionButton fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        ViewCompat.getTranslationY(view) - view.getHeight());
            }
        }

        return minOffset;
    }
}
