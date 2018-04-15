package com.app.instashare.ui.base.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.instashare.R;
import com.app.instashare.ui.signin.activity.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button logout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//
//        logout = findViewById(R.id.logout);
//
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//            }
//        });
//
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        listener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() == null)
//                {
//                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//
//                    finish();
//                    startActivity(intent);
//                }
//            }
//        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(listener);
//        firebaseAuth.addAuthStateListener(listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
    //    firebaseAuth.removeAuthStateListener(listener);
    }
}
