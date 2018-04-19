package com.app.instashare.ui.user.view;

import com.app.instashare.ui.user.model.UserBasic;

/**
 * Created by MIMO on 19/4/18.
 */

public interface SearchView {

    void refreshRecycler();

    void addCard(UserBasic user);
}
