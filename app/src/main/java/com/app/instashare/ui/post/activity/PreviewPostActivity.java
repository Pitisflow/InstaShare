package com.app.instashare.ui.post.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.PreviewPostPresenter;
import com.app.instashare.ui.post.view.PreviewPostView;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class PreviewPostActivity extends AppCompatActivity implements PreviewPostView {

    private static final String EXTRA_POST = "post";


    public static Intent newInstance(Context context, Post post){
        Intent intent = new Intent(context, PreviewPostActivity.class);
        intent.putExtra(EXTRA_POST, post);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }



    private ImageView userImage;
    private ImageView contentImage;

    private TextView userName;
    private TextView date;
    private TextView contentTextUp;
    private TextView contentTextDown;

    private RecyclerView tagsRecycler;



    private UsersRVAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Post post = null;
        PreviewPostPresenter presenter = new PreviewPostPresenter(getApplicationContext(), this);


        if (getIntent().getExtras() != null && getIntent().hasExtra("post"))
        {
            post = getIntent().getParcelableExtra("post");
        }


        bindImageViews();
        bindTextViews();
        bindToolbarView();
        bindRecyclerView();

        presenter.onInitialize(post);
    }




    private void bindTextViews()
    {
        userName = findViewById(R.id.username);
        date = findViewById(R.id.date);

        contentTextDown = findViewById(R.id.contentTextDown);
        contentTextUp = findViewById(R.id.contentTextUp);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentTextDown.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            contentTextUp.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }


    private void bindImageViews()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        userImage = findViewById(R.id.userImage);
        contentImage = findViewById(R.id.contentImage);
        contentImage.getLayoutParams().width = metrics.widthPixels > metrics.heightPixels ?
                metrics.heightPixels : metrics.widthPixels;
    }


    private void bindRecyclerView()
    {
        tagsRecycler = findViewById(R.id.tagsRecycler);

        tagsRecycler.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.HORIZONTAL));
    }


    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }





    @Override
    public void enableTextUp(boolean enable) {
        if (enable) contentTextUp.setVisibility(View.VISIBLE);
        else contentTextUp.setVisibility(View.GONE);
    }

    @Override
    public void enableTextDown(boolean enable) {
        if (enable) contentTextDown.setVisibility(View.VISIBLE);
        else contentTextDown.setVisibility(View.GONE);
    }

    @Override
    public void enableContentImage(boolean enable) {
        if (enable) contentImage.setVisibility(View.VISIBLE);
        else contentImage.setVisibility(View.GONE);
    }

    @Override
    public void enableTagsRecycler(boolean enable) {
        if (enable) tagsRecycler.setVisibility(View.VISIBLE);
        else tagsRecycler.setVisibility(View.GONE);

    }

    @Override
    public void setTextUp(String text) {
        contentTextUp.setText(text);
    }

    @Override
    public void setTextDown(String text) {
        contentTextDown.setText(text);
    }

    @Override
    public void setContentImage(Uri uri) {
        contentImage.setImageURI(uri);
    }

    @Override
    public void setUserImage(String url) {
        Picasso.get().load(url).into(userImage);
    }

    @Override
    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    @Override
    public void setDate(String date) {
        this.date.setText(date);
    }

    @Override
    public void setTagsRecyclerAdapter(PostRVAdapter tagsRecyclerAdapter) {
        tagsRecycler.setAdapter(tagsRecyclerAdapter);
    }
}