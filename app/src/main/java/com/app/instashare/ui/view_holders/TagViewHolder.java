package com.app.instashare.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TextView tagName;
    private ImageButton deleteTag;

    public TagViewHolder(View itemView) {
        super(itemView);

        tagName = itemView.findViewById(R.id.tag);
        deleteTag = itemView.findViewById(R.id.delete);
    }


    public void bind(final String text, final PostRVAdapter.OnDeleteTagListener listener, boolean isDeletable)
    {
        tagName.setText(text);

        if (!isDeletable) deleteTag.setVisibility(View.GONE);
        deleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.deleteTag(text);
            }
        });
    }
}
