package com.app.instashare.ui.base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.app.instashare.R;
import com.app.instashare.ui.base.fragment.MainFragment;
import com.app.instashare.ui.base.presenter.MainPresenter;
import com.app.instashare.ui.base.view.MainView;
import com.app.instashare.ui.notification.fragment.NotificationsFragment;
import com.app.instashare.ui.user.activity.UserProfileActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements MainView, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    private GoogleApiClient apiClient;
    private MainPresenter presenter;

    private static final int PERMISSION_LOCATION_CODE = 1;
    private int isConnected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        if (savedInstanceState != null && savedInstanceState.containsKey("isConnected")) {
            isConnected = savedInstanceState.getInt("isConnected");
        }
        if (isConnected == 0) apiClient.connect();


        presenter = new MainPresenter(getApplicationContext(), this);


        bindToolbarView();
        bindDrawerView();
        bindNavigationView();
    }




    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }




    private void bindDrawerView()
    {
        drawerLayout = findViewById(R.id.drawer_layout);
    }



    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("InstaShare");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }



    private void bindNavigationView()
    {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onItemSelected(item);
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
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("isConnected", isConnected);
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);

            Fragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            isConnected = 1;

            presenter.setCurrentLocation(location);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_CODE);
        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                location = LocationServices.FusedLocationApi.getLastLocation(apiClient);


                Fragment fragment = new MainFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                isConnected = 1;

                presenter.setCurrentLocation(location);
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
}
