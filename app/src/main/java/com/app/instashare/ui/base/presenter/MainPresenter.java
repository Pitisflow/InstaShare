package com.app.instashare.ui.base.presenter;

import android.content.Context;
import android.location.Location;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.base.view.MainView;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class MainPresenter {

    private Context context;
    private MainView view;


    public MainPresenter(Context context, MainView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize()
    {

    }

    public void setCurrentLocation(Location location)
    {
        UserInteractor.updateUserLocation(location);
    }
}
