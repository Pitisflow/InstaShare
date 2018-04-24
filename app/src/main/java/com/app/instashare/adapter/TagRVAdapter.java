package com.app.instashare.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.ui.view_holders.TagViewHolder;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class TagRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> tags;

    public TagRVAdapter() {
        tags = new ArrayList<>();
    }


    public void addTag(String name)
    {
        tags.add(name);
        notifyItemInserted(tags.size() == 0 ? 0 : tags.size() - 1);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TagViewHolder) holder).bind(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
