package com.app.instashare.ui.signup.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Pitisflow on 14/4/18.
 */

public class SignUpActivity extends AppCompatActivity {


    private TextView usernameRequired;
    private TextView emailRequired;
    private TextView passwordRequired;
    private TextView repeatPasswordRequired;

    private EditText usernameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;



    private String usernameState;
    private String emailState;
    private String passwordState;
    private String repeatPasswordState;

    private String usernameRequiredState;
    private String emailRequiredState;
    private String passwordRequiredState;
    private String repeatPasswordRequiredState;



    private FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        System.out.println("CREATE");

//        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
//        register = findViewById(R.id.register);
//
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//
//
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                System.out.println("COMPLETED");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                System.out.println(e.toString());
//                            }
//                        });
//            }
//        });


        bindUsernameView();
        bindEmailView();
        bindPasswordView();
        bindRepeatPasswordView();

    }




    private void bindUsernameView()
    {
        View usernameContainer = findViewById(R.id.username);

        ImageView icon = usernameContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_person_outline_black_24dp);

        usernameRequired = usernameContainer.findViewById(R.id.textView);

        usernameET = usernameContainer.findViewById(R.id.editText);

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameState = charSequence.toString();
                System.out.println(usernameState);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindEmailView()
    {
        View emailContainer = findViewById(R.id.email);

        ImageView icon = emailContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_mail_outline_black_24dp);

        emailRequired = emailContainer.findViewById(R.id.textView);

        emailET = emailContainer.findViewById(R.id.editText);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailState = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindPasswordView()
    {
        View passwordContainer = findViewById(R.id.password);

        ImageView icon = passwordContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_lock_outline_black_24dp);

        passwordRequired = passwordContainer.findViewById(R.id.textView);

        passwordET = passwordContainer.findViewById(R.id.editText);
        passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordState = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindRepeatPasswordView()
    {
        View repeatPasswordContainer = findViewById(R.id.repeat_password);

        ImageView icon = repeatPasswordContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_lock_black_24dp);

        repeatPasswordRequired = repeatPasswordContainer.findViewById(R.id.textView);

        repeatPasswordET = repeatPasswordContainer.findViewById(R.id.editText);
        repeatPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        repeatPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repeatPasswordState = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("username", usernameState);
        outState.putString("email", emailState);
        outState.putString("password", passwordState);
        outState.putString("repeatPassword", repeatPasswordState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        usernameET.setText(savedInstanceState.getString("username"));
        emailET.setText(savedInstanceState.getString("email"));
        passwordET.setText(savedInstanceState.getString("password"));
        repeatPasswordET.setText(savedInstanceState.getString("repeatPassword"));
    }
}