package com.app.instashare.ui.user.presenter;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.user.view.UserEditInfoView;
import com.app.instashare.utils.Constants;

import java.util.Map;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserEditInfoPresenter {

    private UserEditInfoView view;

    private String birthdate;
    private String name;
    private String lastName;
    private String email;
    private String description;


    private Map<String, Object> information;



    public UserEditInfoPresenter(UserEditInfoView view) {
        this.view = view;
    }


    public void onInitialize()
    {
        view.setCurrentLetters("0/500");

        if (UserData.getUser() != null && UserData.getUser().getInformation() != null)
        {
            information = UserData.getUser().getInformation();

            if (information.containsKey(Constants.USER_BIRTHDATE_K)) {
                view.setCurrentBirthDate((String) information.get(Constants.USER_BIRTHDATE_K));
            }

            if (information.containsKey(Constants.USER_EMAIL_K)) {
                view.setCurrentEmail((String) information.get(Constants.USER_EMAIL_K));
            }

            if (information.containsKey(Constants.USER_NAME_K)) {
                view.setCurrentName((String) information.get(Constants.USER_NAME_K));
            }

            if (information.containsKey(Constants.USER_LAST_NAME_K)) {
                view.setCurrentLastName((String) information.get(Constants.USER_LAST_NAME_K));
            }

            if (information.containsKey(Constants.USER_DESCRIPTION_K)) {
                view.setCurrentDescription((String) information.get(Constants.USER_DESCRIPTION_K));
            }
        }
    }

    public void terminate()
    {
        view = null;
    }



    public void updateInformation()
    {
        if (name != null && name.length() > 0) information.put(Constants.USER_NAME_K, name);
        if (lastName != null && lastName.length() > 0) information.put(Constants.USER_LAST_NAME_K, lastName);
        if (birthdate != null && birthdate.length() > 0) information.put(Constants.USER_BIRTHDATE_K, birthdate);
        if (email != null && email.length() > 0) information.put(Constants.USER_EMAIL_K, email);
        if (description != null && description.length() > 0) information.put(Constants.USER_DESCRIPTION_K, description);

        if (UserInteractor.getUserKey() != null) {
            UserInteractor.updateUserInfo(UserInteractor.getUserKey(), information);
        }
    }


    public void onNameChanged(String name) {
        this.name = name.trim();
    }

    public void onLastNameChanged(String lastName) {
        this.lastName = lastName.trim();
    }

    public void onDescriptionChanged(String description)
    {
        this.description = description.trim();
        view.setCurrentLetters(description.length() + "/500");
    }


    public void onBirthdateSelected(int year, int month, int dayOfMonth){
        birthdate = dayOfMonth + "/" + (month+1) + "/" + year;
        view.setCurrentBirthDate(birthdate);
    }
}
