package com.app.instashare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.view_holders.PostViewHolder;
import com.app.instashare.ui.view_holders.TagViewHolder;
import com.app.instashare.utils.Constants;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class PostRVAdapter extends BaseRVAdapter {

    private Context context;

    private boolean isDeletableTag;
    private OnDeleteTagListener deleteTagListener;


    public PostRVAdapter() {
        super();
    }

    public PostRVAdapter(Context context) {
        super();
        this.context = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType)
        {
            case Constants.CARD_POST:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_post, parent, false);

                return new PostViewHolder(itemView);


            case Constants.CARD_POST_TAG:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_tag, parent, false);

                return new TagViewHolder(itemView);


            default:
                return null;
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case Constants.CARD_POST:
                ((PostViewHolder) holder).bind((Post) getItemList().get(position), context);
                break;

            case Constants.CARD_POST_TAG:
                ((TagViewHolder) holder).bind((String) getItemList().get(position), deleteTagListener, isDeletableTag);
                break;
        }
    }




    public void setDeletableTag(boolean deletableTag) {
        isDeletableTag = deletableTag;
    }

    public void setDeleteTagListener(OnDeleteTagListener deleteTagListener) {
        this.deleteTagListener = deleteTagListener;
    }

    public void removeTagListener() {
        deleteTagListener = null;
    }


    public interface OnDeleteTagListener {
        void deleteTag(String tagName);
    }
}
