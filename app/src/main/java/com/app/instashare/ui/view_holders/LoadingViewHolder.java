package com.app.instashare.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.app.instashare.R;

/**
 * Created by Pitisflow on 28/5/18.
 */

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);

        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
