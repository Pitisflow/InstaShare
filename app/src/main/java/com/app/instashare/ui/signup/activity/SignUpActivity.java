package com.app.instashare.ui.signup.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.ui.other.fragment.BottomSheetFragment;
import com.app.instashare.ui.signup.presenter.SignUpPresenter;
import com.app.instashare.ui.signup.view.SignUpView;
import com.app.instashare.utils.CameraUtils;
import com.app.instashare.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Pitisflow on 14/4/18.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpView,
        DatePickerDialog.OnDateSetListener, NavigationView.OnNavigationItemSelectedListener{


    private TextView usernameRequired;
    private TextView emailRequired;
    private TextView passwordRequired;
    private TextView repeatPasswordRequired;

    private EditText usernameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;
    private EditText nameET;
    private EditText lastNameET;
    private EditText birthdateET;


    private Button register;
    private ImageButton takePhoto;
    private ImageView userImage;
    private BottomSheetBehavior bottomSheetBehavior;




    private String usernameState;
    private String emailState;
    private String passwordState;
    private String repeatPasswordState;
    private String nameState;
    private String lastNameState;
    private String birthdateState;
    private String imagePathState;



    private SignUpPresenter presenter;
    private CameraUtils cameraUtils;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;


    private static final int REQUEST_CAMERA_CODE = 1;
    private static final int REQUEST_GALLERY_CODE = 2;
    private static final int REQUEST_WRITE_PERMISSIONS = 1;
    private static final int REQUEST_READ_PERMISSIONS = 2;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        listener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                startActivity(intent);
                finish();
            }
        };



        presenter = new SignUpPresenter(getApplicationContext(), this);

        bindUsernameView();
        bindEmailView();
        bindPasswordView();
        bindRepeatPasswordView();

        bindNameView();
        bindLastNameView();
        bindBirthdateView();

        bindRegisterView();
        bindUserImageView();
        bindActionSheetView();

        presenter.onInitialize();
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
        presenter.onPhotoChanged(imagePathState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(listener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter = null;
    }


    private void bindUsernameView()
    {
        View usernameContainer = findViewById(R.id.username);

        ImageView icon = usernameContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_person_outline_black_24dp);

        usernameRequired = usernameContainer.findViewById(R.id.textView);

        usernameET = usernameContainer.findViewById(R.id.editText);
        usernameET.setHint(R.string.user_info_username);

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameState = charSequence.toString();
                presenter.onUsernameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindEmailView()
    {
        View emailContainer = findViewById(R.id.email);

        ImageView icon = emailContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_mail_outline_black_24dp);

        emailRequired = emailContainer.findViewById(R.id.textView);

        emailET = emailContainer.findViewById(R.id.editText);
        emailET.setHint(R.string.user_info_email);

        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailState = charSequence.toString();
                presenter.onEmailChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindPasswordView()
    {
        View passwordContainer = findViewById(R.id.password);

        ImageView icon = passwordContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_lock_outline_black_24dp);

        passwordRequired = passwordContainer.findViewById(R.id.textView);

        passwordET = passwordContainer.findViewById(R.id.editText);
        passwordET.setHint(R.string.user_info_password);

        passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordState = charSequence.toString();
                presenter.onPasswordChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void bindRepeatPasswordView()
    {
        View repeatPasswordContainer = findViewById(R.id.repeat_password);

        ImageView icon = repeatPasswordContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_lock_black_24dp);

        repeatPasswordRequired = repeatPasswordContainer.findViewById(R.id.textView);

        repeatPasswordET = repeatPasswordContainer.findViewById(R.id.editText);
        repeatPasswordET.setHint(R.string.signup_repeat_password);

        repeatPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        repeatPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repeatPasswordState = charSequence.toString();
                presenter.onRepeatPasswordChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




    private void bindNameView()
    {
        View nameContainer = findViewById(R.id.name);

        ImageView icon = nameContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_account_circle_black_24dp);

        nameET = nameContainer.findViewById(R.id.editText);
        nameET.setHint(R.string.user_info_name);

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameState = charSequence.toString();
                presenter.onNameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }




    private void bindLastNameView()
    {
        View lastNameContainer = findViewById(R.id.last_name);

        ImageView icon = lastNameContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_account_circle_black_24dp);

        lastNameET = lastNameContainer.findViewById(R.id.editText);
        lastNameET.setHint(R.string.user_info_lastName);

        lastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastNameState = charSequence.toString();
                presenter.onLastNameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




    private void bindBirthdateView()
    {
        View birthdateContainer = findViewById(R.id.birthdate);

        ImageView icon = birthdateContainer.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_today_black_24dp);

        birthdateET = birthdateContainer.findViewById(R.id.editText);
        birthdateET.setHint(R.string.user_info_birthdate);

        birthdateET.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_date_range_black_24dp), null);

        birthdateET.setInputType(InputType.TYPE_NULL);
        birthdateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                birthdateState = charSequence.toString();
                presenter.onBirthdateChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        birthdateET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (birthdateET.getRight() - birthdateET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                        DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this, SignUpActivity.this,
                                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                        dialog.show();


                        return false;
                    }
                }
                return false;
            }
        });
    }




    private void bindRegisterView()
    {
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePathState != null) presenter.onPhotoChanged(imagePathState);


                final Dialog progressDialog = Utils.createProgressDialog(SignUpActivity.this,
                        getString(R.string.signup_checking_infomartion));

                UserInteractor.registerUser(firebaseAuth, presenter.generateInformationMap(),
                        new UserInteractor.OnRegistrationProcess() {
                            @Override
                            public void registering() {
                                progressDialog.show();
                            }

                            @Override
                            public void registerSuccesfull() {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void emailInUse() {
                                presenter.emailInUse();
                                emailET.setFocusableInTouchMode(true);
                                emailET.requestFocus();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void usernameInUse() {
                                presenter.usernameInUse();
                                usernameET.setFocusableInTouchMode(true);
                                usernameET.requestFocus();
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }


    private void bindUserImageView()
    {
        userImage = findViewById(R.id.userImage);
        takePhoto = findViewById(R.id.takePhoto);

        takePhoto.setOnClickListener(view -> getSupportFragmentManager()
            .beginTransaction()
            .add(new BottomSheetFragment(), "bottom_sheet")
            .commit());
    }


    private void bindActionSheetView()
    {
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        TextView camera = findViewById(R.id.camera);
        TextView gallery = findViewById(R.id.gallery);



        camera.setClickable(true);
        camera.setCompoundDrawablesWithIntrinsicBounds(
                Utils.changeDrawableSize(getDrawable(R.drawable.ic_camera_picture),
                        getResources().getDimensionPixelSize(R.dimen.signup_drawable_size), this), null, null, null);



        gallery.setClickable(true);
        gallery.setCompoundDrawablesWithIntrinsicBounds(
                Utils.changeDrawableSize(getDrawable(R.drawable.ic_gallery_picture),
                        getResources().getDimensionPixelSize(R.dimen.signup_drawable_size), this), null, null, null);



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        cameraUtils = new CameraUtils(getApplicationContext());

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Intent intent = cameraUtils.getCameraIntent(null, null);
                        startActivityForResult(intent, REQUEST_CAMERA_CODE);
                    } else {
                        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSIONS);
                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_GALLERY_CODE);
                    } else {
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_CODE);
                    }
                }
            }
        });
    }







    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = CameraUtils.getCameraIntent(getApplicationContext(), path -> imagePathState = path);
                        startActivityForResult(intent, REQUEST_CAMERA_CODE);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSIONS);
                    }
                }
                return true;

            case R.id.gallery:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_GALLERY_CODE);
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_CODE);
                    }
                }
                return true;
        }

        return false;
    }














    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA_CODE) {


                CameraUtils.moveImageToGallery(imagePathState, getApplicationContext());
                CameraUtils.compressImage(imagePathState);
                userImage.setImageURI(Uri.parse(imagePathState));
            } else if (requestCode == REQUEST_GALLERY_CODE)
            {
                imagePathState = data.getData().toString();
                userImage.setImageURI(data.getData());
            }
        }
    }









    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = CameraUtils.getCameraIntent(getApplicationContext(), path -> imagePathState = path);
            startActivityForResult(intent, REQUEST_CAMERA_CODE);
        }


        if (requestCode == REQUEST_READ_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY_CODE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("username", usernameState);
        outState.putString("email", emailState);
        outState.putString("password", passwordState);
        outState.putString("repeatPassword", repeatPasswordState);
        outState.putString("name", nameState);
        outState.putString("lastName", lastNameState);
        outState.putString("birthdate", birthdateState);
        outState.putString("imagePath", imagePathState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imagePathState = savedInstanceState.getString("imagePath");
        presenter.onPhotoChanged(imagePathState);

        usernameET.setText(savedInstanceState.getString("username"));
        emailET.setText(savedInstanceState.getString("email"));
        passwordET.setText(savedInstanceState.getString("password"));
        repeatPasswordET.setText(savedInstanceState.getString("repeatPassword"));

        nameET.setText(savedInstanceState.getString("name"));
        lastNameET.setText(savedInstanceState.getString("lastName"));
        birthdateET.setText(savedInstanceState.getString("birthdate"));


        if (imagePathState != null) {
            userImage.setImageURI(Uri.parse(imagePathState));
        }
    }




    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        presenter.onBirthdateSelected(year, month, dayOfMonth);
    }



    @Override
    public void showUsernameAdvice(boolean advice) {
        if (advice) usernameRequired.setVisibility(View.VISIBLE);
        else usernameRequired.setVisibility(View.GONE);
    }

    @Override
    public void showEmailAdvice(boolean advice) {
        if (advice) emailRequired.setVisibility(View.VISIBLE);
        else emailRequired.setVisibility(View.GONE);
    }

    @Override
    public void showPasswordAdvice(boolean advice) {
        if (advice) passwordRequired.setVisibility(View.VISIBLE);
        else passwordRequired.setVisibility(View.GONE);
    }

    @Override
    public void showRepeatPasswordAdvice(boolean advice) {
        if (advice) repeatPasswordRequired.setVisibility(View.VISIBLE);
        else repeatPasswordRequired.setVisibility(View.GONE);
    }

    @Override
    public void setUsernameAdvice(int advice) {
        usernameRequired.setText(advice);
    }

    @Override
    public void setEmailAdvice(int advice) {
        emailRequired.setText(advice);
    }

    @Override
    public void setPasswordAdvice(int advice) {
        passwordRequired.setText(advice);
    }

    @Override
    public void setRepeatPasswordAdvice(int advice) {
        repeatPasswordRequired.setText(advice);
    }

    @Override
    public void enableRegisterButton(boolean enabled) {
        register.setEnabled(enabled);
    }

    @Override
    public void showCurrentBirthdate(String birthdate) {
        this.birthdateET.setText(birthdate);
    }
}