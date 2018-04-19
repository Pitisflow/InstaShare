package com.app.instashare.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.view_holders.BasicUserViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 18/4/18.
 */

public class UsersRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> itemList;


    public UsersRVAdapter() {
        this.itemList = new ArrayList<>();
    }



    public void addCard(Object card)
    {
        itemList.add(card);
        notifyItemInserted(itemList.size() - 1);
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user_basic_card, parent, false);


        return new BasicUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BasicUserViewHolder) holder).bind((UserBasic) itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
