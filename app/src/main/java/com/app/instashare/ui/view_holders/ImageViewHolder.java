package com.app.instashare.ui.view_holders;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;

    public ImageViewHolder(View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image);
    }


    public void bind(String imageURL, Context context, WindowManager manager, UsersRVAdapter.OnImageClick listener)
    {
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);


        int newHeight;
        int widthPx = point.x;
        int marginPx = context.getResources().getDimensionPixelSize(R.dimen.general_margin_small) * 2;
        int cardsMarginPx = context.getResources().getDimensionPixelOffset(R.dimen.item_margin) * 6;

        newHeight = widthPx - marginPx - cardsMarginPx;
        newHeight = newHeight / 3;


        Picasso.get().load(imageURL)
                .resize(0, newHeight)
                .into(imageView);

        imageView.setOnClickListener(view -> {
            if (listener != null) listener.imageClicked(imageURL);
        });


        itemView.setOnLongClickListener(view -> {
            if (listener != null) listener.imageLongClicked(imageURL);
            return true;
        });
    }
}
