package com.app.instashare.ui.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.app.instashare.R;
import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.ui.signin.activity.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class UserProfileActivity extends AppCompatActivity {

    private static final String EXTRA_USER_KEY = "userKey";

    public static Intent newInstance(String userKey, Context context)
    {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EXTRA_USER_KEY, userKey);

        return intent;
    }




    private Button button;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        button = findViewById(R.id.log_out);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });



        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    finish();
                    startActivity(intent);
                }
            }
        };

    }



    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }
}
