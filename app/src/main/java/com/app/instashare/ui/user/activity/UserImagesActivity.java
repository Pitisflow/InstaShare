package com.app.instashare.ui.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.custom.SquareImagesDecorator;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.other.fragment.BottomSheetFragment;
import com.app.instashare.ui.user.presenter.UserImagesPresenter;
import com.app.instashare.ui.user.view.UserImagesView;
import com.app.instashare.utils.Constants;

import org.w3c.dom.Text;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class UserImagesActivity extends AppCompatActivity implements UserImagesView,
        UsersRVAdapter.OnImageClick, NavigationView.OnNavigationItemSelectedListener{


    private static final String EXTRA_USER_KEY = "userKey";

    public static Intent newInstance(String userKey, Context context)
    {
        Intent intent = new Intent(context, UserImagesActivity.class);
        intent.putExtra(EXTRA_USER_KEY, userKey);

        return intent;
    }


    private TextView noImages;
    private RecyclerView recyclerView;
    private UsersRVAdapter adapter;


    private UserImagesPresenter presenter;
    private String userKey;
    private String selectedImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_backgound);


        if (getIntent().getExtras() != null && (getIntent().hasExtra(EXTRA_USER_KEY))) {
            userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        }



        presenter = new UserImagesPresenter(this);

        bindToolbarView();
        bindRecyclerView();

        presenter.onInitialize(userKey);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) {
            presenter.terminate();
            presenter = null;
        }
    }




    private void bindRecyclerView()
    {
        noImages = findViewById(R.id.noImages);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UsersRVAdapter(getApplicationContext(), getWindowManager());
        adapter.setImageClickListener(this);
        SquareImagesDecorator decorator = new SquareImagesDecorator(getWindowManager(), getResources());

        recyclerView.addItemDecoration(decorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }


    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setTitle(getString(R.string.profile_user_images));
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }





    @Override
    public void imageClicked(String imageURL, ImageView imageView) {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                UserImagesActivity.this, imageView, getString(R.string.transition_image));
        ActivityCompat.startActivity(UserImagesActivity.this, PhotoViewActivity.newInstance(UserImagesActivity.this,
                imageURL, getString(R.string.profile_image), false), options.toBundle());
    }

    @Override
    public void imageLongClicked(String imageURL) {
        selectedImage = imageURL;
        if (UserInteractor.getUserKey() != null && UserInteractor.getUserKey().equals(userKey)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(BottomSheetFragment.newInstance(R.menu.menu_set_profile_image, getApplicationContext()), "bottom_sheet")
                    .commit();
        }
    }

    @Override
    public void addImage(String imageURL) {
        adapter.addCard(imageURL, Constants.CARD_USER_IMAGE);
    }

    @Override
    public void enableNoImages(boolean enabled) {
        if (enabled) noImages.setVisibility(View.VISIBLE);
        else  noImages.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        presenter.onNewMainImageSelected(selectedImage, userKey);
        return true;
    }
}
