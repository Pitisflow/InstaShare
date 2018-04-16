package com.app.instashare.ui.base.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.signin.activity.SignInActivity;
import com.app.instashare.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private Button logout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        logout = findViewById(R.id.logout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
            }
        });


        Button prueba = findViewById(R.id.prueba);

        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("file:///storage/emulated/0/Pictures/InstaShare/JPEG_20180416_215632.jpg");
                String[] splitted = "/storage/emulated/0/Pictures/InstaShare/JPEG_20180416_215632.jpg".split("/");
                String photoName = splitted[splitted.length - 1];


                UploadTask task = DatabaseSingleton.getStorageInstance()
                        .child(UserInteractor.getUserKey()).child("images/" + photoName).putFile(uri);

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);

                    finish();
                    startActivity(intent);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(listener);
        firebaseAuth.addAuthStateListener(listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(listener);
    }
}
