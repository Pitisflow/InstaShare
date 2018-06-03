package com.app.instashare.ui.base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.base.fragment.MainFragment;
import com.app.instashare.ui.base.presenter.MainPresenter;
import com.app.instashare.ui.base.view.MainView;
import com.app.instashare.ui.notification.fragment.NotificationsFragment;
import com.app.instashare.ui.user.activity.UserProfileActivity;
import com.app.instashare.utils.LocationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainView,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LocationUtils.LocationStatus {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private TextView username;
    private TextView name;
    private CircleImageView userImage;
    private ImageView backgroundImage;
    private View transparentBackground;



    private GoogleApiClient apiClient;
    private Location myLocation;
    private MainPresenter presenter;

    private static final int PERMISSION_LOCATION_CODE = 1;
    private static final int REFRESH_LOCATION_INTERVAL = 300000;
    private boolean fragmentTransactionDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        if (savedInstanceState != null && savedInstanceState.containsKey("fragmentTransactionDone")) {
            fragmentTransactionDone = savedInstanceState.getBoolean("fragmentTransactionDone");
        }




        bindToolbarView();
        bindDrawerView();
        bindNavigationView();
        bindNavigationHeaderView();
    }




    @Override
    protected void onStart() {
        super.onStart();

        presenter = new MainPresenter(getApplicationContext(), this);
        presenter.onInitialize();
        apiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (apiClient.isConnected()) apiClient.disconnect();
        presenter.terminate();
        presenter = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("fragmentTransactionDone", fragmentTransactionDone);
    }






    private void bindDrawerView()
    {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }



    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }



    private void bindNavigationView()
    {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onItemSelected);
    }


    private void bindNavigationHeaderView()
    {
        View navHeader = navigationView.getHeaderView(0);

        userImage = navHeader.findViewById(R.id.userImage);
        backgroundImage = navHeader.findViewById(R.id.backgroundImage);
        username = navHeader.findViewById(R.id.username);
        name = navHeader.findViewById(R.id.name);
        transparentBackground = navHeader.findViewById(R.id.transparentBackground);

        userImage.setOnClickListener(view -> {
            if (UserInteractor.getUserKey() != null) {
                Intent intent = UserProfileActivity.newInstance(UserInteractor.getUserKey(), getApplicationContext());
                startActivity(intent);
            }
        });
    }




    private boolean onItemSelected(MenuItem item)
    {
        Fragment fragment = null;
        boolean isChecked = false;

        switch (item.getItemId())
        {
            case R.id.posts:
                isChecked = getCheckedItem(R.id.posts);
                fragment = new MainFragment();
                break;

            case R.id.notifications:
                isChecked = getCheckedItem(R.id.notifications);
                fragment = new NotificationsFragment();
                break;

            case R.id.chats:
                isChecked = getCheckedItem(R.id.chats);
                return false;

            case R.id.profile:
                if (UserInteractor.getUserKey() != null) {
                    Intent intent = UserProfileActivity.newInstance(UserInteractor.getUserKey(), getApplicationContext());
                    startActivity(intent);
                }
                break;
        }


        if (fragment != null && !isChecked)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }


        drawerLayout.closeDrawers();
        return true;
    }


    private boolean getCheckedItem(int id)
    {
        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++)
        {
            MenuItem item = menu.getItem(i);
            if (item.isChecked() && id == item.getItemId()) return true;
        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationUtils.REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        LocationUtils.getMyLocation(apiClient, REFRESH_LOCATION_INTERVAL,
                                MainActivity.this, MainActivity.this, this);
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), getString(R.string.error_disabled_location), Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_LOCATION_CODE && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            LocationUtils.getMyLocation(apiClient, REFRESH_LOCATION_INTERVAL,
                    MainActivity.this, MainActivity.this, this);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_refused_afinelocation), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationUtils.getMyLocation(apiClient, REFRESH_LOCATION_INTERVAL,
                        MainActivity.this, MainActivity.this, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_CODE);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if (myLocation != null) presenter.setCurrentLocation(location);
    }




    @Override
    public void stateSucces() {
        if (!fragmentTransactionDone) {
            Fragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            fragmentTransactionDone = true;
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void statusUnavailable() {
        finish();
    }



    @Override
    public void setUserImage(String imageURL) {
        Picasso.get().load(imageURL)
                .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                .into(userImage);
    }

    @Override
    public void setBackgroundImage(String imageURL) {
        Picasso.get().load(imageURL)
                .into(backgroundImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap innerBitmap = ((BitmapDrawable) backgroundImage.getDrawable()).getBitmap();
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
    public void setUsername(String username) {
        this.username.setText(username);
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }
}
