package com.app.instashare.ui.post.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.ui.other.activity.WebViewActivity;
import com.app.instashare.ui.post.activity.AddPostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.github.clans.fab.FloatingActionButton;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class ClosePostsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_close_posts, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        PostRVAdapter adapter = new PostRVAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);
        adapter.addCard("Miau", Constants.CARD_POST_TAG);



        bindFabButtons(view);
    }


    private void bindFabButtons(View view)
    {
        FloatingActionButton fabAdd = view.findViewById(R.id.add);
        FloatingActionButton fabFavorites = view.findViewById(R.id.favorites);
        FloatingActionButton fabSaved = view.findViewById(R.id.saved);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            startActivity(intent);
        });

        fabFavorites.setOnClickListener(v -> {
            Intent intent = WebViewActivity.newInstance(getContext(), "https://stackoverflow.com/questions/7746409/android-webview-launches-browser-when-calling-loadurl");
            startActivity(intent);
        });

        fabSaved.setOnClickListener(v -> {

        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
