package com.app.instashare.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.user.model.UserBasic;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 19/4/18.
 */

public class BasicUserViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private TextView username;
    private TextView name;


    public BasicUserViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.userImage);
        username = itemView.findViewById(R.id.username);
        name = itemView.findViewById(R.id.name);
    }


    public void bind(UserBasic user)
    {
        Picasso.get()
                .load(user.getMainImage())
                .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                .into(userImage);

        username.setText(user.getUsername());
        name.setText(user.getName());
    }

}
