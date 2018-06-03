package com.app.instashare.ui.user.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.custom.MyScrollListener;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.user.presenter.UserListedPresenter;
import com.app.instashare.ui.user.view.UserListedView;
import com.app.instashare.utils.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserListedActivity extends AppCompatActivity implements
        UserListedView,
        UsersRVAdapter.OnUserClick,
        MyScrollListener.OnScrollChanged {

    public static final String EXTRA_LIST_TYPE = "list_type";
    public static final String EXTRA_USER_KEY = "userKey";

    public static Intent newInstance(String listType, String userKey, Context context)
    {
        Intent intent = new Intent(context, UserListedActivity.class);
        intent.putExtra(EXTRA_LIST_TYPE, listType);
        intent.putExtra(EXTRA_USER_KEY, userKey);

        return intent;
    }



    private RecyclerView recyclerView;
    private UsersRVAdapter adapter;
    private MyScrollListener scrollListener;


    private UserListedPresenter presenter;
    private Parcelable recyclerState = null;
    private ArrayList<Parcelable> recyclerItemsState = null;
    private String typeList = null;
    private String userKey = null;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listed);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(EXTRA_LIST_TYPE)) typeList = getIntent().getStringExtra(EXTRA_LIST_TYPE);
            if (getIntent().hasExtra(EXTRA_USER_KEY)) userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        }

        if (savedInstanceState != null)
        {
            recyclerItemsState = savedInstanceState.getParcelableArrayList("recyclerItems");
            recyclerState = savedInstanceState.getParcelable("recycler");
        }


        bindRecyclerView();
        bindToolbarView();



        if (recyclerItemsState != null && recyclerState != null) {
            for (Object user : recyclerItemsState) {
                ((UsersRVAdapter) recyclerView.getAdapter()).addCard(user, Constants.CARD_USER_BASIC);
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
        }

        presenter = new UserListedPresenter(getApplicationContext(), this);
        presenter.onInitialize(recyclerItemsState, typeList, userKey);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.removeUserListener();

        if (presenter != null) {
            presenter.terminate();
            presenter = null;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapter != null)
        {
            ArrayList<UserBasic> users = new ArrayList<>();
            for (Object user : adapter.getItemList())
            {
                if (user instanceof UserBasic) users.add((UserBasic) user);
            }

            outState.putParcelableArrayList("recyclerItems", users);
            outState.putParcelable("recycler", recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }




    //********************************************
    //IMPLEMENTING BINDING VIEWS
    //********************************************

    private void bindRecyclerView()
    {
        adapter = new UsersRVAdapter();
        adapter.setUserClickListener(this);

        scrollListener = new MyScrollListener();
        scrollListener.addListener(this);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(scrollListener);
    }



    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

            if (typeList.equals(Constants.USER_FOLLOWERS_T)) getSupportActionBar().setTitle(getString(R.string.profile_followers));
            else getSupportActionBar().setTitle(getString(R.string.profile_following));
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }



    //********************************************
    //IMPLEMENTING VIEW
    //********************************************

    @Override
    public void addUser(UserBasic user) {
        adapter.addCard(user, Constants.CARD_USER_BASIC);
    }

    @Override
    public void setIsLoading(boolean isLoading) {
        scrollListener.setLoading(isLoading);

        if (isLoading) adapter.addCard(null, Constants.CARD_LOADING);
        else adapter.removeLastCard();
    }

    //********************************************
    //IMPLEMENTING USER ADAPTER
    //********************************************


    @Override
    public void userClicked(String userKey, CircleImageView image, TextView username, TextView name) {
        Intent intentProfile = UserProfileActivity.newInstance(userKey, getApplicationContext());

        ActivityOptionsCompat optionsInfo = ActivityOptionsCompat.makeSceneTransitionAnimation(UserListedActivity.this,
                Pair.create(image, image.getTransitionName()),
                Pair.create(username, username.getTransitionName()),
                Pair.create(name, name.getTransitionName()));

        ActivityCompat.startActivity(UserListedActivity.this, intentProfile, optionsInfo.toBundle());
    }

    //********************************************
    //IMPLEMENTING MYSCROLLLISTENER
    //********************************************

    @Override
    public void loadMoreCards() {
        if (presenter != null) presenter.onDownloadMoreUsers();
    }

}
