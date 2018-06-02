package com.app.instashare.ui.user.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.app.instashare.R;
import com.app.instashare.ui.user.presenter.UserEditPrivacyPresenter;
import com.app.instashare.ui.user.view.UserEditPrivacyView;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserEditPrivacyActivity extends AppCompatActivity implements UserEditPrivacyView {


    private Switch showFollowers;
    private Switch showFollowing;
    private Switch showPosts;
    private Switch showImages;
    private Switch showEmail;



    private UserEditPrivacyPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_privacy);


        presenter = new UserEditPrivacyPresenter(this);

        bindSwitchViews();
        bindToolbarView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onInitialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.updateUserPrivacy();
        presenter.terminate();
        presenter = null;
    }

    private void bindSwitchViews()
    {
        showFollowers = findViewById(R.id.showFollowers);
        showFollowing = findViewById(R.id.showFollowing);
        showPosts = findViewById(R.id.showPosts);
        showEmail = findViewById(R.id.showEmail);
        showImages = findViewById(R.id.showImages);


        showFollowers.setOnCheckedChangeListener((compoundButton, b) -> presenter.onFollowersStateChanged(b));
        showFollowing.setOnCheckedChangeListener((compoundButton, b) -> presenter.onFollowingStateChanged(b));
        showPosts.setOnCheckedChangeListener((compoundButton, b) -> presenter.onPostsStatesChanged(b));
        showEmail.setOnCheckedChangeListener((compoundButton, b) -> presenter.onEmailStateChanged(b));
        showImages.setOnCheckedChangeListener((compoundButton, b) -> presenter.onImagesStateChanged(b));
    }




    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setTitle(getString(R.string.profile_menu_background));
        }
    }



    @Override
    public void currentShowFollowingState(boolean state) {
        showFollowing.setChecked(state);
    }

    @Override
    public void currentShowFollowersState(boolean state) {
        showFollowers.setChecked(state);
    }

    @Override
    public void currentShowEmailState(boolean state) {
        showEmail.setChecked(state);
    }

    @Override
    public void currentShowImagesState(boolean state) {
        showImages.setChecked(state);
    }

    @Override
    public void currentShowPostsState(boolean state) {
        showPosts.setChecked(state);
    }
}
