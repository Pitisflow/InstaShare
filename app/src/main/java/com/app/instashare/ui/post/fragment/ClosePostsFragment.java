package com.app.instashare.ui.post.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.other.activity.WebViewActivity;
import com.app.instashare.ui.other.fragment.BottomSheetFragment;
import com.app.instashare.ui.post.activity.AddPostActivity;
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
        NavigationView.OnNavigationItemSelectedListener{

    private SwipeRefreshLayout refresher;
    private RecyclerView recyclerView;
    private PostRVAdapter adapter;
    private LinearLayout loading;


    private ClosePostsPresenter presenter;


    private Post postOptionsPressed = null;
    private Parcelable recyclerState = null;
    private ArrayList<Parcelable> recyclerItemsState = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_close_posts, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (savedInstanceState != null )
        {
            recyclerItemsState = savedInstanceState.getParcelableArrayList("recyclerItems");
            recyclerState = savedInstanceState.getParcelable("recycler");
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
            for (Object post : recyclerItemsState)
            {
                adapter.addCard(post, Constants.CARD_POST);
            }

            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
            recyclerState = null;
            recyclerItemsState = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        adapter.removePostListener();
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
        adapter.setPostListener(this);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refresher.setOnRefreshListener(() -> presenter.refreshRecycler());
    }


    private void bindLoadingView(View view)
    {
        loading = view.findViewById(R.id.loadingLayout);
        loading.setVisibility(View.GONE);
    }




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
                System.out.println("Location");
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



    //IMPLEMENTS VIEW INTERFACE
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
    public void stopRefreshing() {
        refresher.setRefreshing(false);
    }



    // IMPLEMENTS POST ADAPTER INTERFACE
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
        getChildFragmentManager()
                .beginTransaction()
                .add(BottomSheetFragment.newInstance(getString(R.string.menu_post), post), "bottom_sheet")
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("SEEWFEW");
        return false;
    }
}
