package com.app.instashare.ui.view_holders;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private TextView username;
    private TextView date;
    private TextView contentText;
    private ImageView contentImage;


    public PostViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.userImage);
        username = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        contentText = itemView.findViewById(R.id.contentText);
        contentImage = itemView.findViewById(R.id.contentImage);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }



    public void bind(Post post)
    {
        Picasso.get().load(post.getUser().getMainImage()).into(userImage);
        username.setText(post.getUser().getMainImage());
        date.setText("14:23");

        contentText.setText(post.getContentText());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) Utils.justify(contentText);

        Picasso.get().load(post.getMediaURL()).into(contentImage);
    }

}
