package com.app.instashare.ui.user.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.signup.activity.SignUpActivity;
import com.app.instashare.ui.user.presenter.UserEditInfoPresenter;
import com.app.instashare.ui.user.view.UserEditInfoView;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserEditInfoActivity extends AppCompatActivity implements UserEditInfoView,
        DatePickerDialog.OnDateSetListener {


    private EditText description;
    private EditText name;
    private EditText lastName;
    private EditText birthDate;
    private EditText email;

    private TextView currentLenght;


    private UserEditInfoPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        presenter = new UserEditInfoPresenter(this);
        bindEditTextViews();

        presenter.onInitialize();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.updateInformation();
    }

    private void bindEditTextViews()
    {
        description = findViewById(R.id.descriptionET);
        name = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        birthDate = findViewById(R.id.birthdate);
        email = findViewById(R.id.email);
        currentLenght = findViewById(R.id.currentLength);



        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.onDescriptionChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.onNameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.onLastNameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        birthDate.setOnClickListener((view) -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog(UserEditInfoActivity.this, UserEditInfoActivity.this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }




    //********************************************
    //IMPLEMENTING DATE PICKER DIALOG INTERFACE
    //********************************************

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        presenter.onBirthdateSelected(year, month, dayOfMonth);
    }



    //********************************************
    //IMPLEMENTING VIEW
    //********************************************

    @Override
    public void setCurrentName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setCurrentLastName(String lastName) {
        this.lastName.setText(lastName);
    }

    @Override
    public void setCurrentEmail(String email) {
        this.email.setText(email);
    }

    @Override
    public void setCurrentBirthDate(String birthDate) {
        this.birthDate.setText(birthDate);
    }

    @Override
    public void setCurrentDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void setCurrentLetters(String text) {
        this.currentLenght.setText(text);
    }
}
