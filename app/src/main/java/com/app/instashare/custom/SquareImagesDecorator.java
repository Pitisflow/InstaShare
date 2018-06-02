package com.app.instashare.custom;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.app.instashare.R;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class SquareImagesDecorator extends RecyclerView.ItemDecoration {

    WindowManager windowManager;
    Resources resources;

    public SquareImagesDecorator(WindowManager windowManager, Resources resources) {
        this.windowManager = windowManager;
        this.resources = resources;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){

        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);


        int newHeight;
        int widthPx = point.x;
        int marginPx = resources.getDimensionPixelSize(R.dimen.general_margin_small) * 2;
        int cardsMarginPx = resources.getDimensionPixelOffset(R.dimen.item_margin) * 6;

        newHeight = widthPx - marginPx - cardsMarginPx;
        newHeight = newHeight / 3;


        view.getLayoutParams().height = newHeight;
    }
}
