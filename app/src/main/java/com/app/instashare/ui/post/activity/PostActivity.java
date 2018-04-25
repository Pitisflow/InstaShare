package com.app.instashare.ui.post.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class PostActivity extends AppCompatActivity {

    private static final String EXTRA_POST = "post";
    private static final String EXTRA_PREVIEW = "preview";
    private static final String EXTRA_KEY = "postKey";


    public static Intent newInstance(Context context, Post post, boolean isPreview){
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra(EXTRA_POST, post);
        intent.putExtra(EXTRA_PREVIEW, isPreview);

        return intent;
    }


    public static Intent newInstance(Context context, String postKey)
    {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra(EXTRA_KEY, postKey);

        return intent;
    }













    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        Post post = getIntent().getParcelableExtra("post");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        System.out.println("PIXELS: X: " + width + " Y: " + height);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        System.out.println("METRICS X: " + metrics.widthPixels + " Y: " + metrics.heightPixels);


        Double value = metrics.heightPixels *0.8;

        System.out.println(value.intValue());

        int miau  = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        System.out.println(miau);


//        ImageView imageView = findViewById(R.id.postImage);
//        imageView.getLayoutParams().width = miau;
//        imageView.setImageURI(Uri.parse(post.getMediaURL()));
//        imageView.setBackgroundColor(getResources().getColor(R.color.black));



        TextView textView = findViewById(R.id.postContentUp);
        textView.setText(post.getContentText());


//        Utils.justify(textView);
    }
}
