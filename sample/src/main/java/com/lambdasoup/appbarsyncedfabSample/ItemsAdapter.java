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

import android.support.design.widget.Snackbar;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

/**
 * Created by jl on 06.06.16.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private final SortedList<Long> dataset;
    private long currentMaxItem = 0;
    private final OnItemClickListener onItemClickListener;

    public ItemsAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        dataset = new SortedList<>(Long.class, new SortedListAdapterCallback<Long>(this) { // yes, we're leaking half-constructed this. Currently, it's fine.
            @Override
            public int compare(Long left, Long right) {
                return Long.compare(left, right);
            }

            @Override
            public boolean areContentsTheSame(Long oldItem, Long newItem) {
                return Objects.equals(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(Long item1, Long item2) {
                return Objects.equals(item1, item2);
            }
        });
        setHasStableIds(true);
    }

    public void addItem() {
        dataset.add(currentMaxItem);
        currentMaxItem++;
    }

    public void removeItem(Long item) {
        dataset.remove(item);
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < dataset.size()) {
            return dataset.get(position);
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(dataset.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindItem(final Long item) {
            TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
            textView.setText(textView.getContext().getString(R.string.item_text, item));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(item);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Long item);
    }
}
