package com.app.instashare.ui.post.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        System.out.println("efewfwef");

        ImageView imageView = findViewById(R.id.postImage);
        Picasso.get().load("https://images-production.global.ssl.fastly.net/uploads/posts/teaser_image/150328/instagram-models-january-2018-teaser.png").into(imageView);

        TextView textView = findViewById(R.id.postContent);
        textView.setText(getIntent().getStringExtra("miau"));
    }
}
