package com.app.instashare.ui.post.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.custom.MyScrollListener;
import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.post.activity.AddPostActivity;
import com.app.instashare.ui.post.activity.PublicPostsSettings;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.ClosePostsPresenter;
import com.app.instashare.ui.post.view.ClosePostsView;
import com.app.instashare.utils.Constants;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class ClosePostsFragment extends Fragment implements ClosePostsView,
        PostRVAdapter.OnPostInteraction,
        PopupMenu.OnMenuItemClickListener,
        MyScrollListener.OnScrollChanged {

    private SwipeRefreshLayout refresher;
    private RecyclerView recyclerView;
    private PostRVAdapter adapter;
    private MyScrollListener scrollListener;
    private LinearLayout loading;
    private ProgressBar loadingBar;
    private TextView loadingText;


    private ClosePostsPresenter presenter;


    private Post postOptionsPressed = null;
    private Parcelable recyclerState = null;
    private ArrayList<Parcelable> recyclerItemsState = null;
    private int postsShowingState = SHOWING_PUBLIC;
    private boolean isLoading = false;
    private boolean downloadCompleted = false;



    public static final int SHOWING_PUBLIC = 0;
    public static final int SHOWING_FAVORITES = 1;
    public static final int SHOWING_SAVED = 2;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_close_posts, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
        {
            recyclerItemsState = savedInstanceState.getParcelableArrayList("recyclerItems");
            recyclerState = savedInstanceState.getParcelable("recycler");
            postsShowingState = savedInstanceState.getInt("showingState");
            isLoading = savedInstanceState.getBoolean("isLoading", false);
            downloadCompleted = savedInstanceState.getBoolean("downloadCompleted", false);
        }

        presenter = new ClosePostsPresenter(getContext(), this);

        bindFabButtons(view);
        bindRecyclerView(view);
        bindLoadingView(view);

        presenter.onInitialize(recyclerItemsState);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (recyclerState != null && recyclerItemsState != null && adapter != null)
        {
            ArrayList<Post> tmp = new ArrayList<>();
            for (Object post : recyclerItemsState)
            {
                adapter.addCard(post, Constants.CARD_POST);
                tmp.add((Post) post);
            }

            if (postsShowingState != SHOWING_PUBLIC) {
                if (recyclerItemsState.size() != 0) {
                    refresher.setEnabled(false);
                    recyclerView.addOnScrollListener(scrollListener);
                    presenter.setPostShowing(postsShowingState);
                    presenter.setLoadingMore(isLoading);

                    if (postsShowingState == SHOWING_FAVORITES) {
                        presenter.setFavoritedPosts(new ArrayList<>(tmp));
                        setToolbarTitle(getString(R.string.post_title_favorites));
                    }
                    else if (postsShowingState == SHOWING_SAVED) {
                        presenter.setSavedPosts(new ArrayList<>(tmp));
                        setToolbarTitle(getString(R.string.post_title_saved));
                    }

                }
                else {
                    postsShowingState = SHOWING_PUBLIC;
                    setToolbarTitle(getString(R.string.post_title_public));
                }

                if (isLoading && !downloadCompleted) adapter.addCard(null, Constants.CARD_LOADING);
            } else setToolbarTitle(getString(R.string.post_title_public));
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
            recyclerState = null;
            recyclerItemsState = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        adapter.removePostListener();
        scrollListener.removeListener();
        presenter.terminate();
        presenter = null;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapter != null)
        {
            ArrayList<Post> posts = new ArrayList<>();
            for (Object post : adapter.getItemList())
            {
                if (post instanceof Post) posts.add((Post) post);
            }

            outState.putParcelableArrayList("recyclerItems", posts);
            outState.putParcelable("recycler", recyclerView.getLayoutManager().onSaveInstanceState());
        }

        outState.putInt("showingState", postsShowingState);
        outState.putBoolean("isLoading", isLoading);
        outState.putBoolean("downloadCompleted", downloadCompleted);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.getExtras() != null)
        {
            System.out.println(data.getExtras().getBoolean(PublicPostsSettings.NEED_RELOAD));
        }
    }

    private void bindFabButtons(View view)
    {
        FloatingActionButton fabAdd = view.findViewById(R.id.add);
        FloatingActionButton fabFavorites = view.findViewById(R.id.favorites);
        FloatingActionButton fabSaved = view.findViewById(R.id.saved);
        FloatingActionButton fabPublic = view.findViewById(R.id.allPosts);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            startActivity(intent);
        });
        fabFavorites.setOnClickListener(v -> {
            if (postsShowingState != SHOWING_FAVORITES) presenter.showFavoritedPosts();

            setToolbarTitle(getString(R.string.post_title_favorites));
            postsShowingState = SHOWING_FAVORITES;
            refresher.setEnabled(false);
            recyclerView.addOnScrollListener(scrollListener);
        });

        fabSaved.setOnClickListener(v -> {
            if (postsShowingState != SHOWING_SAVED) presenter.showSavedPosts();

            setToolbarTitle(getString(R.string.post_title_saved));
            postsShowingState = SHOWING_SAVED;
            refresher.setEnabled(false);
            recyclerView.addOnScrollListener(scrollListener);
        });

        fabPublic.setOnClickListener(v -> {
            if (postsShowingState != SHOWING_PUBLIC) presenter.showPublicPosts();

            setToolbarTitle(getString(R.string.post_title_public));
            postsShowingState = SHOWING_PUBLIC;
            refresher.setEnabled(true);
            recyclerView.removeOnScrollListener(scrollListener);
        });
    }


    private void bindRecyclerView(View view)
    {
        refresher = view.findViewById(R.id.refresh);

        scrollListener = new MyScrollListener();
        scrollListener.addListener(this);
        adapter = new PostRVAdapter(getContext());
        adapter.setPostListener(this);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refresher.setOnRefreshListener(() -> presenter.refreshRecycler());
    }


    private void bindLoadingView(View view)
    {
        loading = view.findViewById(R.id.loadingLayout);
        loadingBar = view.findViewById(R.id.loading);
        loadingText = view.findViewById(R.id.textLoading);
        loading.setVisibility(View.GONE);
    }


    private void setToolbarTitle(String title)
    {
        ActionBar actionBar;
        if (getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
        {
            actionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(getString(R.string.app_name) + " " + title);
        }
    }



    //********************************************
    //ON MENU OPTIONS SELECT
    //********************************************
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.location:
                Intent intent = new Intent(getContext(), PublicPostsSettings.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.filter:
                System.out.println("Filter");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.favorite:
                presenter.onFavoritePressed(postOptionsPressed);
                break;

            case R.id.save:
                presenter.onSavePressed(postOptionsPressed);
                break;

            case R.id.hide:
                presenter.onHidePressed(postOptionsPressed);
                break;

            case R.id.report:
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setHint(getString(R.string.post_report));


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setView(input);
                builder.setMessage(getString(R.string.post_report_text));
                builder.setNegativeButton(getString(R.string.post_report_cancel), null);
                builder.setPositiveButton(getString(R.string.post_report_send),
                        (dialogInterface, i) -> presenter.onReportPressed(postOptionsPressed, input.getText().toString()));
                builder.show();
                break;
        }
        return false;
    }



    //********************************************
    //IMPLEMENTING VIEW INTERFACE
    //********************************************
    @Override
    public void enableLoadingView(boolean enable, boolean loading, String message) {
        if (enable) this.loading.setVisibility(View.VISIBLE);
        else this.loading.setVisibility(View.GONE);

        if (loading) loadingBar.setVisibility(View.VISIBLE);
        else loadingBar.setVisibility(View.GONE);

        loadingText.setText(message);
    }

    @Override
    public void addPost(Object post) {
        adapter.addCard(post, Constants.CARD_POST);
    }

    @Override
    public void enableLoading(boolean enable) {
        if (enable) adapter.addCard(null, Constants.CARD_LOADING);
        else if (adapter.getItemList() != null && adapter.getItemList().size() != 0) adapter.removeLastCard();
    }

    @Override
    public void removePost(Object post) {
        adapter.removeCard(post);
    }

    @Override
    public void addPostAtStart(Object post) {
        adapter.addCardAtIndex(post, Constants.CARD_POST, 0);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void changePosts(ArrayList<Object> posts) {
        adapter.removeAllCards();
        adapter.addCards(posts, Constants.CARD_POST);
    }

    @Override
    public void setIsLoading(boolean loading) {
        this.isLoading = loading;
        scrollListener.setLoading(loading);
    }

    @Override
    public void setDownloadCompleted(boolean completed) {
        downloadCompleted = completed;
    }

    @Override
    public void stopRefreshing() {
        refresher.setRefreshing(false);
    }



    //********************************************
    //IMPLEMENTING POST ADAPTER INTERFACE
    //********************************************
    @Override
    public void onLikeClicked(Post post, boolean liked) {
        presenter.onLikePressed(post, liked);
    }

    @Override
    public void onCommentClicked(Post post) {
        System.out.println("COMMENT: " + post.getPostKey());
    }

    @Override
    public void onShareClicked(Post post, boolean shared) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setMessage(getString(R.string.post_share_message));
        builder.setNegativeButton(getString(R.string.post_share_option_cancel), null);
        builder.setPositiveButton(getString(R.string.post_share_option_ok),
                (dialogInterface, i) -> presenter.onSharePressed(post));

        if (!shared) builder.show();
        else Toast.makeText(getContext(), getString(R.string.post_shared_already), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPostClicked(Post post) {
        System.out.println("CLICKED");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onImageClicked(String imageUrl, ImageView imageView) {
        if (getActivity() != null && getContext() != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(), imageView, getString(R.string.image_transition));
            ActivityCompat.startActivity(getContext(), PhotoViewActivity.newInstance(getContext(),
                    imageUrl, getString(R.string.photoview_post_image), false), options.toBundle());
        }
    }

    @Override
    public void onUserClicked(String userKey) {
        System.out.println(userKey);
    }

    @Override
    public void onOptionsClicked(Post post, View v) {
        if (getContext() != null) {
            postOptionsPressed = post;

            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.setOnMenuItemClickListener(this);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_post_options, popup.getMenu());
            popup.show();
        }
    }

    @Override
    public void onPostLongClicked(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.post_menu_favorite_remove), (dialogInterface, i) -> presenter.onRemoveFromFavorites(post));
        builder.setNeutralButton(getString(R.string.post_menu_save_remove), (dialogInterface, i) -> presenter.onRemoveFromSaved(post));
        builder.setPositiveButton(getString(R.string.post_menu_show), (dialogInterface, i) -> presenter.onRemoveFromHidden(post));
        builder.show();
    }


    //********************************************
    //IMPLEMENTING SCROLL LISTENER INTERFACE
    //********************************************
    @Override
    public void loadMoreCards() {
        presenter.loadMoreToList();
    }
}
