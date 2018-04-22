package com.app.instashare.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.view_holders.PostViewHolder;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class PostRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> itemList;



    public PostRVAdapter() {
        itemList = new ArrayList<>();
    }


    public void addCard(Object card)
    {
        itemList.add(card);
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PostViewHolder) holder).bind((Post) itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
