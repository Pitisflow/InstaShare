package com.app.instashare.ui.signin.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app.instashare.ui.signin.view.SignInView;

/**
 * Created by Pitisflow on 13/4/18.
 */

public class SignInPresenter {

    private Context context;
    private SignInView view;


    private String email;
    private String password;


    private boolean isEmailEmpty = true;
    private boolean isPasswordEmpty = true;



    public SignInPresenter(Context context, SignInView view) {
        this.context = context;
        this.view = view;
    }




    public void onInitialize()
    {

    }



    public void onEmailChanged(String email)
    {
        if (email.trim().length() != 0)
        {
            this.email = email.trim();
            isEmailEmpty = false;
        } else {
            this.email = email.trim();
            isEmailEmpty = true;
        }



        if (!isEmailEmpty && !isPasswordEmpty) view.setLogInEnabled(true);
        else view.setLogInEnabled(false);
    }



    public void onPasswordChanged(String password)
    {
        if (password.trim().length() != 0)
        {
            this.password = password.trim();
            isPasswordEmpty = false;
        } else {
            this.password = password.trim();
            isPasswordEmpty = true;
        }



        if (!isEmailEmpty && !isPasswordEmpty) view.setLogInEnabled(true);
        else view.setLogInEnabled(false);
    }


}
