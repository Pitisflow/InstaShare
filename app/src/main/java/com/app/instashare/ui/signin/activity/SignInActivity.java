package com.app.instashare.ui.signin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.R;
import com.app.instashare.ui.signin.presenter.SignInPresenter;
import com.app.instashare.ui.signin.view.SignInView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Pitisflow on 13/4/18.
 */

public class SignInActivity extends AppCompatActivity implements SignInView{


    private Button login;
    private EditText email;
    private EditText password;
    private TextView register;


    private boolean loginState;
    private String emailState;
    private String passwordState;


    private SignInPresenter presenter;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        bindEmailView();
        bindPasswordView();
        bindLogInView();
        bindRegisterView();


        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) startMainActivity();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) startMainActivity();
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);

        presenter = new SignInPresenter(getApplicationContext(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();


        firebaseAuth.removeAuthStateListener(listener);
        presenter = null;
    }





    private void bindEmailView()
    {
        email = findViewById(R.id.email);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailState = charSequence.toString();
                presenter.onEmailChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    private void bindPasswordView()
    {
        password = findViewById(R.id.password);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordState = charSequence.toString();
                presenter.onPasswordChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    private void bindLogInView()
    {
        login = findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                firebaseAuth.signInWithEmailAndPassword(emailState.trim(), passwordState.trim())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(view, getResources().getString(R.string.login_wrong_email_pw)
                                        , Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private void bindRegisterView()
    {
        String text = getResources().getString(R.string.login_register_text);
        String[] splitted = text.split(" ");

        SpannableString ss = new SpannableString(splitted[splitted.length - 1]);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                presenter.openRegisterScreen();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.white));
            }
        };
        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        splitted[splitted.length - 1] = "";



        register = findViewById(R.id.register);

        register.setMovementMethod(LinkMovementMethod.getInstance());
        register.setText(TextUtils.concat(TextUtils.join(" ", splitted), ss));

    }



    private void startMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);


        startActivity(intent);
        finish();
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("email", emailState);
        outState.putString("password", passwordState);
        outState.putBoolean("login", loginState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        email.setText(savedInstanceState.getString("email"));
        password.setText(savedInstanceState.getString("password"));
        login.setEnabled(savedInstanceState.getBoolean("login"));
    }




    @Override
    public void setLogInEnabled(boolean enabled) {
        login.setEnabled(enabled);
        loginState = enabled;
    }
}
