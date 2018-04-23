package com.app.instashare.ui.other.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private NavigationView.OnNavigationItemSelectedListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationView.OnNavigationItemSelectedListener)
        {
            listener = (NavigationView.OnNavigationItemSelectedListener) context;
        } else{
            throw new IllegalStateException("The context should implement NavigationView.OnNavigationItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NavigationView navigationView = (NavigationView) inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                dismiss();
                return listener.onNavigationItemSelected(item);
            }
        });

        return navigationView;
    }
}
