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


    public static BottomSheetFragment newInstance(String string, Post post){
        Bundle bundle = new Bundle();
        bundle.putBoolean(string, true);
        bundle.putParcelable("post", post);

        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }



    private NavigationView.OnNavigationItemSelectedListener listener;
    private Post post;


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

        if (getArguments() != null && getArguments().containsKey(getString(R.string.menu_post))){
            navigationView.inflateMenu(R.menu.menu_post_options_delete);
        } else navigationView.inflateMenu(R.menu.menu_camera_gallery);

        if (getArguments() != null && getArguments().containsKey("post")){
            post = getArguments().getParcelable("post");
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            dismiss();
            if (listener != null) {
                return listener.onNavigationItemSelected(item);
            } else {                        //Case opened from fragment (PostsFragment)
                switch (item.getItemId())
                {
                    case R.id.favoriteRemove:
                        PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T);
                        break;

                    case R.id.saveRemove:
                        PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_SAVED_T);
                        break;

                    case R.id.show:
                        PostInteractor.removePostAsHided(post, UserInteractor.getUserKey());
                        break;
                }

                return true;
            }
        });

        return navigationView;
    }
}
