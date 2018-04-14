package com.app.instashare.utils;

import android.util.Patterns;

/**
 * Created by Pitisflow on 13/4/18.
 */

public class Validation {
    public static boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
