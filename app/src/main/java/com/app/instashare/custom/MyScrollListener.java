package com.app.instashare.custom;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Pitisflow on 28/5/18.
 */

public class MyScrollListener extends RecyclerView.OnScrollListener {

    private boolean isLoading = false;
    private OnScrollChanged listener;


    public MyScrollListener() {
        super();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (!isLoading && recyclerView.getLayoutManager().getItemCount() < ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() + 2)
        {
            if (listener != null) listener.loadMoreCards();
        }
    }



    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }


    public void addListener(OnScrollChanged listener) {
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }

    public interface OnScrollChanged
    {
        void loadMoreCards();
    }

}
