package com.app.instashare.ui.signup.presenter;

import android.content.Context;
import android.icu.text.SymbolTable;
import android.net.Uri;

import com.app.instashare.R;
import com.app.instashare.ui.signup.view.SignUpView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class SignUpPresenter {

    private Context context;
    private SignUpView view;


    private String username;
    private String email;
    private String password;
    private String repeatPassword;
    private String name;
    private String lastName;
    private String birthdate;
    private String photoURL;


    private boolean usernameOk = false;
    private boolean emailOk = false;
    private boolean passwordOk = false;
    private boolean repeatPasswordOk = false;


    public SignUpPresenter(Context context, SignUpView view) {
        this.context = context;
        this.view = view;
    }





    public void onInitialize()
    {
        view.showUsernameAdvice(true);
        view.setUsernameAdvice(R.string.signup_username_required);

        view.showEmailAdvice(true);
        view.setEmailAdvice(R.string.signup_email_required);

        view.showPasswordAdvice(true);
        view.setPasswordAdvice(R.string.signup_password_required);

        view.showRepeatPasswordAdvice(true);
        view.setRepeatPasswordAdvice(R.string.signup_repeat_password_required);

    }




    public void onUsernameChanged(String username)
    {
        this.username = username.trim();


        if (username.trim().length() == 0)
        {
            usernameOk = false;
            view.showUsernameAdvice(true);
            view.setUsernameAdvice(R.string.signup_username_required);
        } else if (!Validation.validateUsername(username.trim()))
        {
            usernameOk = false;
            view.showUsernameAdvice(true);
            view.setUsernameAdvice(R.string.signup_username_not_valid);
        } else {
            usernameOk = true;
            view.showUsernameAdvice(false);
        }

        checkInformationOk();
    }


    public void onEmailChanged(String email)
    {
        this.email = email.trim();

        if (email.trim().length() == 0)
        {
            emailOk = false;
            view.showEmailAdvice(true);
            view.setEmailAdvice(R.string.signup_email_required);
        } else if (!Validation.validateEmail(email.trim()))
        {
            emailOk = false;
            view.showEmailAdvice(true);
            view.setEmailAdvice(R.string.signup_email_not_valid);
        } else {
            emailOk = true;
            view.showEmailAdvice(false);
        }

        checkInformationOk();
    }


    public void onPasswordChanged(String password)
    {
        this.password = password;

        if (password.length() == 0)
        {
            passwordOk = false;
            view.showPasswordAdvice(true);
            view.setPasswordAdvice(R.string.signup_password_required);
        } else if (!Validation.validatePassword(password))
        {
            passwordOk = false;
            view.showPasswordAdvice(true);
            view.setRepeatPasswordAdvice(R.string.signup_password_not_valid);
        } else {
            passwordOk = true;
            view.showPasswordAdvice(false);
        }

        checkInformationOk();
    }

    public void onRepeatPasswordChanged(String password)
    {
        this.repeatPassword = password;

        if (password.length() == 0)
        {
            repeatPasswordOk = false;
            view.showRepeatPasswordAdvice(true);
            view.setRepeatPasswordAdvice(R.string.signup_repeat_password_required);
        } else if (!Validation.validatePasswordsMatch(this.password, password))
        {
            repeatPasswordOk = false;
            view.showRepeatPasswordAdvice(true);
            view.setRepeatPasswordAdvice(R.string.signup_passwords_not_matching);
        } else {
            repeatPasswordOk = true;
            view.showRepeatPasswordAdvice(false);
        }

        checkInformationOk();
    }



    public void onNameChanged(String name)
    {
        this.name = name.trim();
    }



    public void onLastNameChanged(String lastName)
    {
        this.lastName = lastName.trim();
    }


    public void onBirthdateChanged(String birthdate)
    {
        this.birthdate = birthdate.trim();
    }


    public void onPhotoChanged(Uri uri)
    {
        photoURL = uri.getPath();
    }


    public void onBirthdateSelected(int year, int month, int dayOfMonth){
        birthdate = dayOfMonth + "/" + (month+1) + "/" + year;
        view.showCurrentBirthdate(birthdate);
    }



    public Map<String, String> generateInformationMap()
    {
        Map<String, String> information = new HashMap<>();

        information.put(Constants.USER_NAME_K, name);
        information.put(Constants.USER_LAST_NAME_K, lastName);
        information.put(Constants.USER_BIRTHDATE_K, birthdate);
        information.put(Constants.USERNAME_K, username);
        information.put(Constants.USER_EMAIL_K, email);
        information.put("password", password);



        return information;
    }




    public void emailInUse()
    {
        view.showEmailAdvice(true);
        view.setEmailAdvice(R.string.signup_email_repeated);

        emailOk = false;
        checkInformationOk();
    }


    public void usernameInUse()
    {
        view.showUsernameAdvice(true);
        view.setUsernameAdvice(R.string.signup_username_repeated);

        usernameOk = false;
        checkInformationOk();
    }



    private void checkInformationOk()
    {
        if(usernameOk && emailOk && passwordOk && repeatPasswordOk) view.enableRegisterButton(true);
        else view.enableRegisterButton(false);
    }
}
