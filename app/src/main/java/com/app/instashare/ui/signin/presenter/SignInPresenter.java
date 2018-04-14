package com.app.instashare.ui.signin.presenter;

import android.content.Context;
import android.content.Intent;

import com.app.instashare.ui.signin.view.SignInView;
import com.app.instashare.ui.signup.activity.SignUpActivity;

/**
 * Created by Pitisflow on 13/4/18.
 */

public class SignInPresenter {

    private Context context;
    private SignInView view;


    private boolean isEmailEmpty = true;
    private boolean isPasswordEmpty = true;



    public SignInPresenter(Context context, SignInView view) {
        this.context = context;
        this.view = view;
    }




    public void onEmailChanged(String email)
    {
        if (email.trim().length() != 0) isEmailEmpty = false;
        else isEmailEmpty = true;


        if (!isEmailEmpty && !isPasswordEmpty) view.setLogInEnabled(true);
        else view.setLogInEnabled(false);
    }



    public void onPasswordChanged(String password)
    {
        if (password.trim().length() != 0) isPasswordEmpty = false;
        else isPasswordEmpty = true;


        if (!isEmailEmpty && !isPasswordEmpty) view.setLogInEnabled(true);
        else view.setLogInEnabled(false);
    }


    public void openRegisterScreen()
    {
        Intent intent = new Intent(context, SignUpActivity.class);

        context.startActivity(intent);
    }

}