package com.app.instashare.ui.post.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.post.model.Post;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        Post post = getIntent().getParcelableExtra("miau");

        ImageView imageView = findViewById(R.id.postImage);
        imageView.setImageURI(Uri.parse(post.getMediaURL()));

        TextView textView = findViewById(R.id.postContent);
        textView.setText(post.getContentText());

    }
}
