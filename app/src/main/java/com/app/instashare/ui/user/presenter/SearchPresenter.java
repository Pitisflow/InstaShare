package com.app.instashare.ui.user.presenter;

import android.content.Context;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.user.view.SearchView;

/**
 * Created by MIMO on 19/4/18.
 */

public class SearchPresenter {

    private Context context;
    private SearchView view;


    public SearchPresenter(Context context, SearchView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize()
    {

    }


    public void onSearchChanged(String search)
    {
        if (search.trim().length() != 0) {
            UserInteractor.searchUsersByUsername(search.trim(), new UserInteractor.OnBasicInfoFetched() {
                @Override
                public void onSearchRefreshed() {
                    view.refreshRecycler();
                }

                @Override
                public void onUserBasicFetched(UserBasic user) {
                    view.addCard(user);
                }
            });
        } else view.refreshRecycler();
    }
}
