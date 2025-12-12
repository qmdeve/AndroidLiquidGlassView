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

package com.qmdeve.liquidglass.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmdeve.liquidglass.widget.LiquidGlassView;

public class LiquidGlassAdapter extends RecyclerView.Adapter<LiquidGlassAdapter.ViewHolder> {

    private static final int ITEM_COUNT = 50;
    private final android.content.Context context;
    private final ViewGroup contentContainer;

    public LiquidGlassAdapter(android.content.Context context, ViewGroup contentContainer) {
        this.context = context;
        this.contentContainer = contentContainer;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_liquid_glass, parent, false);
        ViewHolder holder = new ViewHolder(view);
        if (contentContainer != null) {
            holder.liquidGlassView.bind(contentContainer);
            holder.liquidGlassView.setDraggableEnabled(false);
            holder.liquidGlassView.setBlurRadius(10);
            holder.liquidGlassView.setElasticEnabled(false);
            holder.liquidGlassView.setTouchEffectEnabled(false);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LiquidGlassView liquidGlassView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            liquidGlassView = itemView.findViewById(R.id.liquidGlassView);
        }
    }
}

