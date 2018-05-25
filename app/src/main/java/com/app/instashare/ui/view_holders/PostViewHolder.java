package com.app.instashare.ui.view_holders;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.DateUtils;
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



    public void bind(Post post, Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        contentImage.getLayoutParams().width = metrics.widthPixels > metrics.heightPixels ?
                metrics.heightPixels : metrics.widthPixels;


        userImage.setCircleBackgroundColor(context.getResources().getColor(R.color.black));
        if (post.isAnonymous()) userImage.setImageResource(R.mipmap.ic_launcher);
        else {
            Picasso.get()
                    .load(post.getUser().getMainImage())
                    .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                    .into(userImage);
        }


        if (post.getMediaURL() != null) {
            contentImage.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(post.getMediaURL())
                    .resize(contentImage.getLayoutParams().width, 0)
                    .into(contentImage);
        } else contentImage.setVisibility(View.GONE);


        if (post.isAnonymous()) username.setText(context.getString(R.string.post_anonymous));
        else username.setText(post.getUser().getUsername());


        date.setText(DateUtils.getPostDateFromTimestamp(post.getTimestamp(), context));
        contentText.setText(post.getContentText());
    }

}
