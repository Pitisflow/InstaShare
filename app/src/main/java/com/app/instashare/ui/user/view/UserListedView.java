package com.app.instashare.ui.user.view;

import com.app.instashare.ui.user.model.UserBasic;

/**
 * Created by Pitisflow on 2/6/18.
 */

public interface UserListedView {

    void addUser(UserBasic user);

    void setIsLoading(boolean isLoading);
}
