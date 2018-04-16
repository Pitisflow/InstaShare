package com.app.instashare.ui.signup.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.ui.signup.presenter.SignUpPresenter;
import com.app.instashare.ui.signup.view.SignUpView;
import com.app.instashare.utils.CameraUtils;
import com.app.instashare.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pitisflow on 14/4/18.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpView{


    private TextView usernameRequired;
    private TextView emailRequired;
    private TextView passwordRequired;
    private TextView repeatPasswordRequired;

    private EditText usernameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;


    private Button register;
    private ImageButton takePhoto;
    private ImageView userImage;
    private BottomSheetBehavior bottomSheetBehavior;




    private String usernameState;
    private String emailState;
    private String passwordState;
    private String repeatPasswordState;
    private Uri userImageState;



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


        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                    finish();
                    startActivity(intent);
                }
            }
        };



        bindUsernameView();
        bindEmailView();
        bindPasswordView();
        bindRepeatPasswordView();
        bindRegisterView();
        bindUserImageView();
        bindActionSheetView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);

        presenter = new SignUpPresenter(getApplicationContext(), this);
        presenter.onInitialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(listener);

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



    private void bindRegisterView()
    {
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog progressDialog = Utils.createProgressDialog(SignUpActivity.this,
                        getString(R.string.signup_checking_infomartion));

                UserInteractor.registerUser(firebaseAuth, emailState.trim(), passwordState,
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
                            public void registerFailure() {
                                presenter.emailInUse();
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

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
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
                        Intent intent = cameraUtils.getCameraIntent();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA_CODE) {


                URI uri = cameraUtils.moveImageToGallery(cameraUtils.getBitmapFromPhoto(userImage));
                userImageState = Uri.parse(uri.toString());

                userImage.setImageURI(Uri.parse(uri.toString()));
            } else if (requestCode == REQUEST_GALLERY_CODE)
            {
                userImageState = data.getData();
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
            cameraUtils = new CameraUtils(getApplicationContext());


            Intent intent = cameraUtils.getCameraIntent();
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
        outState.putParcelable("userImage", userImageState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        userImageState = savedInstanceState.getParcelable("userImage");

        usernameET.setText(savedInstanceState.getString("username"));
        emailET.setText(savedInstanceState.getString("email"));
        passwordET.setText(savedInstanceState.getString("password"));
        repeatPasswordET.setText(savedInstanceState.getString("repeatPassword"));


        if (savedInstanceState.getParcelable("userImage") != null) {
            userImage.setImageURI((Uri) savedInstanceState.getParcelable("userImage"));
        }
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
}