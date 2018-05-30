package com.app.instashare.ui.post.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.PreviewPostPresenter;
import com.app.instashare.ui.post.view.PreviewPostView;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.CameraUtils;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.common.primitives.Chars;
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
    private TextView distance;
    private TextView contentTextUp;
    private TextView contentTextDown;

    private RecyclerView tagsRecycler;

    private Post post = null;
    private PreviewPostPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (getIntent().getExtras() != null && getIntent().hasExtra(EXTRA_POST))
        {
            post = getIntent().getParcelableExtra(EXTRA_POST);
        }


        bindImageViews();
        bindTextViews();
        bindToolbarView();
        bindRecyclerView();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (presenter != null)
        {
            presenter.terminate();
            presenter = null;
        }

        presenter = new PreviewPostPresenter(getApplicationContext(), this);
        presenter.onInitialize(post);
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.terminate();
        presenter = null;
    }


    private void bindTextViews()
    {
        userName = findViewById(R.id.username);
        date = findViewById(R.id.date);
        distance = findViewById(R.id.distance);

        contentTextDown = findViewById(R.id.contentTextDown);
        contentTextUp = findViewById(R.id.contentTextUp);

        contentTextDown.setMovementMethod(LinkMovementMethod.getInstance());
        contentTextUp.setMovementMethod(LinkMovementMethod.getInstance());
        userName.setMovementMethod(LinkMovementMethod.getInstance());


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

        contentImage.setOnClickListener(view -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    PreviewPostActivity.this, contentImage, getString(R.string.image_transition));
            ActivityCompat.startActivity(PreviewPostActivity.this, PhotoViewActivity.newInstance(getApplicationContext(),
                    post.getMediaURL(), getString(R.string.photoview_post_image), false), options.toBundle());
        });
    }


    private void bindRecyclerView()
    {
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 1 : 3;

        tagsRecycler = findViewById(R.id.tagsRecycler);
        tagsRecycler.setLayoutManager(new StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.HORIZONTAL));
    }


    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.post_post));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
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
    public void setTextUp(CharSequence text) {
        contentTextUp.setText(text);
    }

    @Override
    public void setTextDown(CharSequence text) {
        contentTextDown.setText(text);
    }

    @Override
    public void setContentImage(Uri uri) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels > metrics.heightPixels ?
                metrics.heightPixels : metrics.widthPixels;

        Picasso.get()
                .load(uri)
                .resize(width, 0)
                .into(contentImage);
    }

    @Override
    public void setUserImage(String url) {
        Picasso.get()
                .load(CameraUtils.imageUriFromString(url))
                .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                .into(userImage);

        userImage.setOnClickListener((view) -> {
            //OPEN PROFILE ACTIVITY
        });
    }

    @Override
    public void setUserName(CharSequence userName) {
        this.userName.setText(userName);
    }

    @Override
    public void setDate(String date) {
        this.date.setText(date);
    }

    @Override
    public void setDistance(String distance) {
        this.distance.setText(distance);
    }

    @Override
    public void setTagsRecyclerAdapter(PostRVAdapter tagsRecyclerAdapter) {
        tagsRecycler.setAdapter(tagsRecyclerAdapter);
    }


    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}