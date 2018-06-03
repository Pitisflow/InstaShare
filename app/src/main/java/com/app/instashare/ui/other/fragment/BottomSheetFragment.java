package com.app.instashare.ui.other.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private static final String EXTRA_MENU = "menu";

    public static BottomSheetFragment newInstance(int idMenu, Context context)
    {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_MENU, idMenu);

        fragment.setArguments(args);
        return fragment;
    }


    private NavigationView.OnNavigationItemSelectedListener listener;
    private int idMenu;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationView.OnNavigationItemSelectedListener)
        {
            listener = (NavigationView.OnNavigationItemSelectedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NavigationView navigationView = (NavigationView) inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        if (getArguments() != null && getArguments().containsKey(EXTRA_MENU)) {
            idMenu = getArguments().getInt(EXTRA_MENU);
            navigationView.inflateMenu(idMenu);
        } else navigationView.inflateMenu(R.menu.menu_camera_gallery);
        navigationView.setNavigationItemSelectedListener(item -> {
            dismiss();
            return listener.onNavigationItemSelected(item);
        });

        return navigationView;
    }
}
