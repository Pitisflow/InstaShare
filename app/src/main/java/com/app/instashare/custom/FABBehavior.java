package com.app.instashare.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class FABBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {


    public FABBehavior(Context context, AttributeSet attrs) {
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionMenu child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionMenu child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if (dyConsumed > 0) {
            child.hideMenu(true);
        } else if (dyConsumed < 0) {
            child.showMenu(true);
        }
    }
}
