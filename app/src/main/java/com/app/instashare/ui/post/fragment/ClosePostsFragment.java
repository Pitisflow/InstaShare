package com.app.instashare.ui.post.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.other.activity.WebViewActivity;
import com.app.instashare.ui.post.activity.AddPostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.ClosePostsPresenter;
import com.app.instashare.ui.post.view.ClosePostsView;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.github.clans.fab.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class ClosePostsFragment extends Fragment implements ClosePostsView {

    private SwipeRefreshLayout refresher;
    private RecyclerView recyclerView;
    private PostRVAdapter adapter;
    private LinearLayout loading;


    private ClosePostsPresenter presenter;


    private boolean loadedState = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_close_posts, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new ClosePostsPresenter(getContext(), this);

        bindFabButtons(view);
        bindRecyclerView(view);
        bindLoadingView(view);

        presenter.onInitialize();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.terminate();
        presenter = null;
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
            Intent intent = PhotoViewActivity.newInstance(getContext(), "https://i.ytimg.com/vi/6VLxwPs-bpw/maxresdefault.jpg", null, false);
            startActivity(intent);
        });
    }


    private void bindRecyclerView(View view)
    {
        refresher = view.findViewById(R.id.refresh);

        adapter = new PostRVAdapter(getContext());
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refresher.setOnRefreshListener(() -> {
            System.out.println(refresher.isRefreshing());
        });

    }


    private void bindLoadingView(View view)
    {
        loading = view.findViewById(R.id.loadingLayout);
        loading.setVisibility(View.GONE);
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_sample, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void enableLoadingView(boolean enable) {
        if (enable) loading.setVisibility(View.VISIBLE);
        else loading.setVisibility(View.GONE);
    }

    @Override
    public void refreshRecycler() {

    }

    @Override
    public void addPost(Object post) {
        adapter.addCard(post, Constants.CARD_POST);
    }
}
