package com.app.instashare.ui.base.presenter;

import android.content.Context;
import android.location.Location;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.base.view.MainView;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class MainPresenter implements UserData.OnUserDataFetched{

    private Context context;
    private MainView view;


    public MainPresenter(Context context, MainView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize()
    {
        UserData.getInstance(this);
    }

    public void setCurrentLocation(Location location)
    {
        UserInteractor.updateUserLocation(location);
    }

    public void terminate()
    {
        this.view = null;
        UserData.removeListener(this);
    }


    @Override
    public void updateUserInfo() {
        //DO SOMETHING
    }
}
