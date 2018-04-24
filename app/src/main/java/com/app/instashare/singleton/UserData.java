package com.app.instashare.singleton;

import com.app.instashare.ui.user.model.User;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class UserData {


    private static User user;




    public static User getUser() {
        return user;
    }
}
