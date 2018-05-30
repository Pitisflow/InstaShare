package com.app.instashare.ui.other.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.instashare.R;
import com.app.instashare.utils.CameraUtils;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 21/5/18.
 */

public class PhotoViewActivity extends AppCompatActivity{

    private static final String EXTRA_IMAGE_URL = "imageURL";
    private static final String EXTRA_LOCAL = "isLocal";
    private static final String EXTRA_TITLE = "title";



    public static Intent newInstance(Context context, String imageURL, String title, boolean isLocal)
    {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, imageURL);
        intent.putExtra(EXTRA_LOCAL, isLocal);
        intent.putExtra(EXTRA_TITLE, title);

        return intent;
    }





    private String imageURL;
    private String title;
    private boolean isLocal = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        if (getIntent() != null)
        {
            if (getIntent().hasExtra(EXTRA_IMAGE_URL)) imageURL = getIntent().getStringExtra(EXTRA_IMAGE_URL);
            if (getIntent().hasExtra(EXTRA_TITLE)) title = getIntent().getStringExtra(EXTRA_TITLE);
            if (getIntent().hasExtra(EXTRA_LOCAL)) isLocal = getIntent().getBooleanExtra(EXTRA_LOCAL, false);
        }

        if (title == null) title = getString(R.string.photoview_post_image);


        bindImageView();
        bindToolbarView();
    }


    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }


    private void bindImageView()
    {
        ZoomageView zoomageView = findViewById(R.id.myZoomageView);
        if (!isLocal) Picasso.get().load(imageURL).into(zoomageView);
        else zoomageView.setImageURI(CameraUtils.imageUriFromString(imageURL));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
