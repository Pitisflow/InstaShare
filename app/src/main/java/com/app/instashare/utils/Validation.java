package com.app.instashare.utils;

import android.util.Patterns;

/**
 * Created by Pitisflow on 13/4/18.
 */

public class Validation {
    public static boolean validateEmail(String email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean validateUsername(String username)
    {
        return (username.length() >= 6 && username.length() <= 12 && username.matches("[a-zA-Z0-9]+"));
    }


    public static boolean validatePassword(String password)
    {
        return (password.length() >= 6 && password.matches(".*\\d+.*"));
    }


    public static boolean validatePasswordsMatch(String pw1, String pw2)
    {
        return pw1.equals(pw2);
    }
}
