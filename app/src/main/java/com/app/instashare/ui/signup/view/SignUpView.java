package com.app.instashare.ui.signup.view;

/**
 * Created by Pitisflow on 15/4/18.
 */

public interface SignUpView {

    void showUsernameAdvice(boolean advice);

    void showEmailAdvice(boolean advice);

    void showPasswordAdvice(boolean advice);

    void showRepeatPasswordAdvice(boolean advice);

    void setUsernameAdvice(int advice);

    void setEmailAdvice(int advice);

    void setPasswordAdvice(int advice);

    void setRepeatPasswordAdvice(int advice);

    void enableRegisterButton(boolean enabled);
}
