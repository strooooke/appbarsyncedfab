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

package com.lambdasoup.appbarsyncedfabSample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

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
        // that differ only in their AppBarLayout. Ususally, you'd just have your AppBarLayout
        // directly declared in your activity layout xml.
        ViewStub appBarStub = (ViewStub) findViewById(R.id.app_bar_stub);
        //noinspection ConstantConditions
        appBarStub.setLayoutResource(getAppBarLayoutResource());
        appBarStub.inflate();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getTitle());
        }


        final RecyclerView itemsList = (RecyclerView) findViewById(R.id.content_list);
        itemsAdapter = new ItemsAdapter(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Long item) {
                //noinspection ConstantConditions
                Snackbar.make(itemsList, getString(R.string.item_clicked, item), Snackbar.LENGTH_LONG).show();
            }
        });
        //noinspection ConstantConditions
        itemsList.setAdapter(itemsAdapter);

        ItemTouchHelper swipeDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                itemsAdapter.removeItem(viewHolder.getItemId());
            }
        });
        swipeDismiss.attachToRecyclerView(itemsList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //noinspection ConstantConditions
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsAdapter.addItem();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //noinspection ConstantConditions
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
