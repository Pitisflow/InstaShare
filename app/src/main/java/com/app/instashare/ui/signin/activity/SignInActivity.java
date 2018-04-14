package com.app.instashare.ui.signin.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.ui.signin.presenter.SignInPresenter;
import com.app.instashare.ui.signin.view.SignInView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        presenter = new SignInPresenter(getApplicationContext(), this);

        bindEmailView();
        bindPasswordView();
        bindLogInView();
        bindRegisterView();
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
            public void onClick(View view) {

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
                System.out.println("CLICK!");
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




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        email.setText(emailState);
        password.setText(passwordState);
        login.setEnabled(loginState);
    }




    @Override
    public void setLogInEnabled(boolean enabled) {
        login.setEnabled(enabled);
        loginState = enabled;
    }
}
