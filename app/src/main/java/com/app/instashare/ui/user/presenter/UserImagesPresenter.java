package com.app.instashare.ui.user.presenter;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.user.view.UserImagesView;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class UserImagesPresenter implements UserInteractor.OnUserImagesDownload{

    private UserImagesView view;


    public UserImagesPresenter(UserImagesView view) {
        this.view = view;
    }


    public void onInitialize(String userKey)
    {
        UserInteractor.downloadUserImages(userKey, this);
    }


    public void terminate()
    {
        view = null;
    }


    public void onNewMainImageSelected(String imageURL, String userKey) {
        UserInteractor.setUserImage(userKey, imageURL);
    }


    @Override
    public void downloading() {

    }

    @Override
    public void downloadCompleted(ArrayList<String> images) {
        if (images != null && view != null)
        {
            for (String image : images) {
                view.addImage(image);
            }
        }
    }

    @Override
    public void downloadEmpty() {
        if (view != null) view.enableNoImages(true);
    }
}
