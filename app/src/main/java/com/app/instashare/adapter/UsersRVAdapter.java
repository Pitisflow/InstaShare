package com.app.instashare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.view_holders.BasicUserViewHolder;
import com.app.instashare.ui.view_holders.ImageViewHolder;
import com.app.instashare.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 18/4/18.
 */

public class UsersRVAdapter extends BaseRVAdapter {

    private Context context;
    private WindowManager windowManager;
    private OnImageClick imageClickListener;
    private OnUserClick userClickListener;



    public UsersRVAdapter() {
        super();
    }

    public UsersRVAdapter(Context context, WindowManager windowManager) {
        this.context = context;
        this.windowManager = windowManager;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType)
        {
            case Constants.CARD_USER_BASIC:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_user_basic_card, parent, false);

                return new BasicUserViewHolder(itemView);

            case Constants.CARD_USER_IMAGE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_image, parent, false);

                return new ImageViewHolder(itemView);

            default:
                return null;
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case Constants.CARD_USER_BASIC:
                ((BasicUserViewHolder) holder).bind((UserBasic) getItemList().get(position));
                break;

            case Constants.CARD_USER_IMAGE:
                ((ImageViewHolder) holder).bind((String) getItemList().get(position), context, windowManager, imageClickListener);
                break;

        }
    }


    public void setImageClickListener(OnImageClick imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public void setUserClickListener(OnUserClick userClickListener) {
        this.userClickListener = userClickListener;
    }

    public void removeImageListener()
    {
        this.imageClickListener = null;
    }

    public void removeUserListener()
    {
        this.userClickListener = null;
    }



    public interface OnUserClick
    {
        void userClicked(String userKey);
    }

    public interface OnImageClick
    {
        void imageClicked(String imageURL);

        void imageLongClicked(String imageURL);
    }
}
