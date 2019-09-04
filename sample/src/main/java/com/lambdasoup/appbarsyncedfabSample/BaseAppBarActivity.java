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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Common base for the different AppBarLayout example activities. Contains a RecyclerView
 * with addable/deletable items, to allow easy experimentation with various behaviors for
 * a FAB (reacting on nestedScroll events can be problematic when the NestedScrollView changes
 * its size and ceases to scroll). Contains navigation for easy navigation between the different
 * example activities.
 */
public abstract class BaseAppBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = BaseAppBarActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // ViewStub replacement is here to allow DRY with the different activities
        // that differ only in their AppBarLayout. Usually, you'd just have your AppBarLayout
        // directly declared in your activity layout xml.
        ViewStub appBarStub = findViewById(R.id.app_bar_stub);

        onBeforeInflateAppBarLayout();
        appBarStub.setLayoutResource(getAppBarLayoutResource());
        appBarStub.inflate();


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getTitle());
        }


        final RecyclerView itemsList = findViewById(R.id.content_list);
        itemsAdapter = new ItemsAdapter(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Long item) {
                Snackbar.make(itemsList, getString(R.string.item_clicked, item), Snackbar.LENGTH_LONG).show();
            }
        });
        itemsList.setAdapter(itemsAdapter);

        ItemTouchHelper swipeDismiss = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                itemsAdapter.removeItem(viewHolder.getItemId());
            }
        });
        swipeDismiss.attachToRecyclerView(itemsList);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsAdapter.addItem();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(getNavId()).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id != getNavId()) {
            switch (id) {
                case R.id.nav_simple_app_bar:
                    startActivity(new Intent(getApplicationContext(), SimpleAppBarAppBarActivity.class));
                    break;
                case R.id.nav_header_app_bar:
                    startActivity(new Intent(getApplicationContext(), HeaderAppBarActivity.class));
                    break;
                case R.id.nav_complex_app_bar:
                    startActivity(new Intent(getApplicationContext(), ComplexAppBarActivity.class));
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    break;
                default:
                    Log.d(TAG, "unhandled navigation item " + item);
                    break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Perform adjustments on layout if needed.
     */
    protected void onBeforeInflateAppBarLayout(){}

    /**
     * Provide navigation id resource for the activity.
     */
    @IdRes
    protected abstract int getNavId();

    /**
     * Provide layout resource for the app bar. A contained toolbar should have id R.id.toolbar.
     */
    @LayoutRes
    protected abstract int getAppBarLayoutResource();
}
