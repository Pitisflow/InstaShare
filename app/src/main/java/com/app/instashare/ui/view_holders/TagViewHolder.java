package com.app.instashare.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.instashare.R;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TextView tagName;

    public TagViewHolder(View itemView) {
        super(itemView);

        tagName = itemView.findViewById(R.id.tag);
    }


    public void bind(String text)
    {
        tagName.setText(text);
    }
}
