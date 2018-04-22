package com.app.instashare.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class FABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {


    public FABBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child,
            @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child,
            @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
            int type) {
        if (dyConsumed > 0) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        } else if (dyConsumed < 0) {
            child.show();
        }
    }
}
