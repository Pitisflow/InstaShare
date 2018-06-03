package com.app.instashare.ui.user.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.custom.SquareImagesDecorator;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.post.activity.PreviewPostActivity;
import com.app.instashare.ui.signin.activity.SignInActivity;
import com.app.instashare.ui.user.presenter.UserProfilePresenter;
import com.app.instashare.ui.user.view.UserProfileView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ak.sh.ay.oblique.ObliqueView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class UserProfileActivity extends AppCompatActivity implements
        View.OnClickListener, UserProfileView {

    private static final String EXTRA_USER_KEY = "userKey";

    public static Intent newInstance(String userKey, Context context)
    {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EXTRA_USER_KEY, userKey);

        return intent;
    }




    private CircleImageView profileImage;
    private ImageView backGroundImage;
    private ImageView selfUser;
    private TextView nameTV;
    private TextView numberOfPostsTV;
    private TextView completedUserTV;
    private TextView yearsTV;
    private TextView descriptionTV;
    private TextView emailTV;
    private TextView followingTV;
    private TextView followersTV;
    private TextView seeMoreImagesTV;
    private TextView viewAllPostsTV;
    private TextView noImagesTV;


    private Button follow;
    private Button unFollow;
    private LinearLayout followingContainer;
    private LinearLayout followersContainer;
    private View transparentBackground;


    private RecyclerView recyclerView;
    private UsersRVAdapter adapter;


    private UserProfilePresenter presenter;
    private String userKey;
    private boolean hasImagesCharged = false;


    private Button button;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        listener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                finish();
                startActivity(intent);
            }
        };

        if (getIntent().getExtras() != null && (getIntent().hasExtra(EXTRA_USER_KEY))) {
            userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        }

        presenter = new UserProfilePresenter(getApplicationContext(), this);

        bindUserImagesView();
        bindToolbarView();
        bindTextViews();
        bindFollowButtonsView();
        bindFollowContainersView();
        bindImagesRecyclerView();
    }




    @Override
    protected void onStart() {
        super.onStart();

        presenter.onInitialize(userKey);
        auth.addAuthStateListener(listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.terminate();
        presenter = null;
    }

    //********************************************
    //BINDING VIEWS
    //********************************************


    private void bindUserImagesView()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int screenHeight = point.y;
        int screenWidth = point.x;

        int backImageHeight = (int) (screenHeight * 0.7);
        int obliquesMarginTop = (int) (backImageHeight * 0.63);
        int obliquesWidth = screenWidth / 2;
        int obliquesHeight = backImageHeight - obliquesMarginTop;
        int profileImageMarginTop = obliquesMarginTop - (getResources().getDimensionPixelSize(R.dimen.profile_circle_image) / 2);

        double obliqueHypo = Math.hypot(obliquesHeight, obliquesWidth);
        double angle = Math.toDegrees(Math.asin(obliquesHeight / obliqueHypo));


        RelativeLayout.LayoutParams obliqueParamsStart = new RelativeLayout.LayoutParams(obliquesWidth, obliquesHeight);
        obliqueParamsStart.setMargins(0, obliquesMarginTop, 0, 0);


        RelativeLayout.LayoutParams obliqueParamsEnd = new RelativeLayout.LayoutParams(obliquesWidth, obliquesHeight);
        obliqueParamsEnd.addRule(RelativeLayout.ALIGN_PARENT_END);
        obliqueParamsEnd.setMargins(0, obliquesMarginTop, 0, 0);


        RelativeLayout.LayoutParams profileImageParams = new RelativeLayout.LayoutParams(
                getResources().getDimensionPixelOffset(R.dimen.profile_circle_image),
                getResources().getDimensionPixelOffset(R.dimen.profile_circle_image));

        profileImageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        profileImageParams.setMargins(0, profileImageMarginTop, 0, 0);


        ObliqueView obliqueStart = findViewById(R.id.obliqueStart);
        ObliqueView obliqueEnd = findViewById(R.id.obliqueEnd);
        backGroundImage = findViewById(R.id.backgroundImage);
        profileImage = findViewById(R.id.profileImage);
        transparentBackground = findViewById(R.id.transparentBackground);


        transparentBackground.getLayoutParams().height = backImageHeight;
        backGroundImage.getLayoutParams().height = backImageHeight;
        profileImage.setLayoutParams(profileImageParams);
        profileImage.setCircleBackgroundColor(getResources().getColor(R.color.black));
        profileImage.setOnClickListener(this);


        obliqueStart.setLayoutParams(obliqueParamsStart);
        obliqueEnd.setLayoutParams(obliqueParamsEnd);
        obliqueEnd.setStartAngle((float) angle);
        obliqueStart.setStartAngle(180 - (float) angle);
        obliqueStart.setEndAngle(180);
    }


    private void bindTextViews()
    {
        emailTV = findViewById(R.id.email);
        completedUserTV = findViewById(R.id.userCompleted);
        descriptionTV = findViewById(R.id.descriptionET);
        followersTV = findViewById(R.id.followersNum);
        followingTV = findViewById(R.id.followingNum);
        nameTV = findViewById(R.id.name);
        numberOfPostsTV = findViewById(R.id.numberOfPosts);
        yearsTV = findViewById(R.id.age);
        seeMoreImagesTV = findViewById(R.id.seeMoreImages);
        viewAllPostsTV = findViewById(R.id.seeAllPosts);

        emailTV.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionTV.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void bindFollowButtonsView()
    {
        follow = findViewById(R.id.followButton);
        unFollow = findViewById(R.id.followingButton);
        selfUser = findViewById(R.id.selfUser);

        follow.setOnClickListener(this);
        unFollow.setOnClickListener(this);
    }


    private void bindFollowContainersView()
    {
        followersContainer = findViewById(R.id.followersContainer);
        followingContainer = findViewById(R.id.followingContainer);
    }



    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }


    private void bindImagesRecyclerView()
    {
        noImagesTV = findViewById(R.id.noImages);
        recyclerView = findViewById(R.id.imagesRecycler);
        adapter = new UsersRVAdapter(getApplicationContext(), getWindowManager());

        SquareImagesDecorator decorator = new SquareImagesDecorator(getWindowManager(), getResources());
        recyclerView.addItemDecoration(decorator);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }


    //********************************************
    //TOOLBAR MENUS
    //********************************************



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userKey.equals(UserInteractor.getUserKey())) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_profile_self, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.edit:
                Intent intentInfo = new Intent(getApplicationContext(), UserEditInfoActivity.class);

                ActivityOptionsCompat optionsInfo = ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this,
                        Pair.create(nameTV, nameTV.getTransitionName()),
                        Pair.create(emailTV, emailTV.getTransitionName()),
                        Pair.create(yearsTV, yearsTV.getTransitionName()),
                        Pair.create(descriptionTV, descriptionTV.getTransitionName()));
                ActivityCompat.startActivity(UserProfileActivity.this, intentInfo, optionsInfo.toBundle());
                break;

            case R.id.background:
                Intent intentBackground = new Intent(getApplicationContext(), UserChangeBackgroundActivity.class);

                ActivityOptionsCompat optionsBackground = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intentBackground, optionsBackground.toBundle());
                break;

            case R.id.privacy:
                Intent intentPrivacy = new Intent(getApplicationContext(), UserEditPrivacyActivity.class);

                ActivityOptionsCompat optionsPrivacy = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intentPrivacy, optionsPrivacy.toBundle());
                break;

            case R.id.logOut:
                auth.signOut();
                break;
        }
        return true;
    }





    //********************************************
    //IMPLEMENTING ON CLICK
    //********************************************

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.followersContainer:
                Intent intentFollowers = UserListedActivity.newInstance(Constants.USER_FOLLOWERS_T, userKey, getApplicationContext());
                startActivity(intentFollowers);
                break;

            case R.id.followingContainer:
                Intent intentFollowing = UserListedActivity.newInstance(Constants.USER_FOLLOWING_T, userKey, getApplicationContext());
                startActivity(intentFollowing);
                break;

            case R.id.followButton:
                presenter.onFollowPressed();
                break;

            case R.id.followingButton:
                presenter.onUnFollowPressed();
                break;

            case R.id.seeMoreImages:
                Intent intentImages = UserImagesActivity.newInstance(userKey, getApplicationContext());

                ActivityOptionsCompat optionsBackground = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intentImages, optionsBackground.toBundle());
                break;

            case R.id.seeAllPosts:
                System.out.println("see more posts");

                break;

            case R.id.profileImage:
                presenter.onUserImagePressed();
                break;
        }
    }



    //********************************************
    //IMPLEMENTING VIEW
    //********************************************

    @Override
    public void enableSeeFollowers(boolean enabled) {
        if (enabled) followersContainer.setOnClickListener(this);
    }

    @Override
    public void enableSeeFollowing(boolean enabled) {
        if (enabled) followingContainer.setOnClickListener(this);
    }

    @Override
    public void enableFollowButton(boolean enableButton, boolean isFollowing) {
        if (enableButton) {
            if (isFollowing) {
                unFollow.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
            } else {
                follow.setVisibility(View.VISIBLE);
                unFollow.setVisibility(View.GONE);
            }
        } else selfUser.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableSeeMoreImages(boolean enabled) {
        if (enabled) seeMoreImagesTV.setOnClickListener(this);
        else recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void enableViewAllPosts(boolean enabled) {
        if (enabled) viewAllPostsTV.setOnClickListener(this);
    }

    @Override
    public void enableEmail(boolean enabled) {
        if (enabled) emailTV.setVisibility(View.VISIBLE);
        else emailTV.setVisibility(View.GONE);
    }

    @Override
    public void enableYears(boolean enabled) {
        if (enabled) yearsTV.setVisibility(View.VISIBLE);
        else yearsTV.setVisibility(View.GONE);
    }

    @Override
    public void setUsername(String username) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("@" + username);
        }
    }

    @Override
    public void setName(String name) {
        if (name == null) nameTV.setText(getString(R.string.profile_no_name));
        else nameTV.setText(name);
    }

    @Override
    public void setPostsShared(int postsShared) {
        if (postsShared == 1) numberOfPostsTV.setText(getString(R.string.profile_post_shared_singular));
        else numberOfPostsTV.setText(getString(R.string.profile_post_shared_plural, postsShared));
    }

    @Override
    public void setFollowing(int following) {
        followingTV.setText(String.valueOf(following));
    }

    @Override
    public void setFollowers(int followers) {
        followersTV.setText(String.valueOf(followers));
    }

    @Override
    public void setCompletedUser(String username, String name) {
        if (name == null) {
            completedUserTV.setText(getString(R.string.profile_completed_name, username ,
                    getString(R.string.profile_no_name)));
        } else completedUserTV.setText(getString(R.string.profile_completed_name, username, name));
    }

    @Override
    public void setYears(String years) {
        if (years != null) yearsTV.setText(years);
        else yearsTV.setVisibility(View.GONE);
    }

    @Override
    public void setDescription(String description) {
        if (description != null) descriptionTV.setText(Utils.urlChecker(description, getApplicationContext()));
        else descriptionTV.setText(R.string.profile_no_description);
    }

    @Override
    public void setEmail(String email) {
        if (email != null) emailTV.setText(Html.fromHtml("Email: <a href=\"mailto:" + email + "\">" + email + "</a>"));
    }

    @Override
    public void setUserImageView(String imageURL) {
        Picasso.get().load(imageURL)
                .resize(profileImage.getLayoutParams().width, profileImage.getLayoutParams().height)
                .placeholder(R.mipmap.ic_launcher)
                .into(profileImage);
    }

    @Override
    public void setUserBackgroundImage(String backgroundImage) {
        Picasso.get().load(backgroundImage)
                .resize(0, backGroundImage.getLayoutParams().height)
                .into(backGroundImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap innerBitmap = ((BitmapDrawable) backGroundImage.getDrawable()).getBitmap();
                        Palette.from(innerBitmap).generate(palette -> {
                            int mutedColor = palette.getMutedColor(getResources().getColor(R.color.colorPrimary));

                            String hexColor = "#59" + Integer.toHexString(mutedColor).substring(2);
                            transparentBackground.setBackgroundColor(Color.parseColor(hexColor));
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    @Override
    public void setNoImagesText(String text) {
        noImagesTV.setText(text);
        noImagesTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNoPostsText(String text) {

    }

    @Override
    public void addImages(ArrayList<String> imagesURLs) {
        if (!hasImagesCharged) {
            adapter.addCards(new ArrayList<>(imagesURLs), Constants.CARD_USER_IMAGE);
            hasImagesCharged = true;
        }
    }

    @Override
    public void openPhotoActivity(String imageURL) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                UserProfileActivity.this, profileImage, getString(R.string.transition_image));
        ActivityCompat.startActivity(UserProfileActivity.this, PhotoViewActivity.newInstance(UserProfileActivity.this,
                imageURL, getString(R.string.profile_profile_image), false), options.toBundle());
    }
}
