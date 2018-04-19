package com.app.instashare.ui.base.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.base.fragment.MainFragment;
import com.app.instashare.ui.notification.fragment.NotificationsFragment;
import com.app.instashare.ui.signin.activity.SignInActivity;
import com.app.instashare.ui.user.activity.UserProfileActivity;
import com.app.instashare.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            Fragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("InstaShare");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        bindDrawerView();
        bindNavigationView();
    }




    private void bindDrawerView()
    {
        drawerLayout = findViewById(R.id.drawer_layout);
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

        //getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
