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

package com.lambdasoup.appbarsyncedfabSample;

import android.view.View;

public class ComplexAppBarActivity extends BaseAppBarActivity {


    @Override
    protected void onBeforeInflateAppBarLayout() {
        View coordinatorLayout = findViewById(R.id.coordinator_layout);
        // Because we're using a single base layout, for proper appearance
        // of the collapsing toolbar layout, we need to do this programmatically.
        coordinatorLayout.setFitsSystemWindows(true);
    }

    @Override
    protected int getAppBarLayoutResource() {
        return R.layout.app_bar_complex_collapsing_app_bar;
    }

    @Override
    protected int getNavId() {
        return R.id.nav_complex_app_bar;
    }
}
