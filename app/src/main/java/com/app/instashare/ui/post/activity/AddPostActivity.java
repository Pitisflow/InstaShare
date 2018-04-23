package com.app.instashare.ui.post.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.other.fragment.BottomSheetFragment;
import com.app.instashare.ui.post.presenter.AddPostPresenter;
import com.app.instashare.ui.post.view.AddPostView;
import com.app.instashare.utils.CameraUtils;

import java.net.URI;

/**
 * Created by Pitisflow on 22/4/18.
 */

public class AddPostActivity extends AppCompatActivity implements AddPostView, NavigationView.OnNavigationItemSelectedListener{

    private EditText contentET;
    private TextView contentMaxLetters;
    private ImageView contentImage;
    private RadioGroup contentAlign;
    private RadioGroup publicationShareWith;
    private Switch publicationShareAs;
    private Button publishPost;
    private Button previewPost;



    private String contentTextState;
    private String contentImageState;
    private boolean shareAllState = true;
    private boolean isUpState = true;



    private AddPostPresenter presenter;
    private CameraUtils cameraUtils;



    private static final int REQUEST_CAMERA_CODE = 1;
    private static final int REQUEST_GALLERY_CODE = 2;
    private static final int REQUEST_WRITE_PERMISSIONS = 1;
    private static final int REQUEST_READ_PERMISSIONS = 2;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);



        bindContentAlignView();
        bindContentImageView();
        bindContentTextView();
        bindPublishView();
        bindPreviewView();
        bindShareWithView();
        bindShareAsView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter = new AddPostPresenter(getApplicationContext(), this);
        presenter.onInitialize();
    }


    @Override
    protected void onStop() {
        super.onStop();

        presenter = null;
    }




    private void bindContentTextView()
    {
        contentET = findViewById(R.id.postContentText);
        contentMaxLetters = findViewById(R.id.postMaxLetters);

        contentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                contentTextState = charSequence.toString();
                presenter.onContentTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void bindContentImageView()
    {
        contentImage = findViewById(R.id.postContentImage);

        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(new BottomSheetFragment(), "bottom_sheet")
                        .commit();
            }
        });
    }

    private void bindContentAlignView()
    {
        contentAlign = findViewById(R.id.groupAlign);

        contentAlign.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i)
                {
                    case R.id.radioUp:
                        isUpState = true;
                        presenter.onAlignTextChanged(true);
                        break;

                    case R.id.radioDown:
                        isUpState = false;
                        presenter.onAlignTextChanged(false);
                        break;
                }
            }
        });
    }

    private void bindPublishView()
    {
        publishPost = findViewById(R.id.publish);

        publishPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void bindPreviewView()
    {
        previewPost = findViewById(R.id.preview);

        previewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("miau", contentET.getText().toString());


                startActivity(intent, presenter.generateOptions(AddPostActivity.this).toBundle());
            }
        });
    }


    private void bindShareWithView()
    {
        publicationShareWith = findViewById(R.id.groupShareWith);

        publicationShareWith.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i)
                {
                    case R.id.shareWithAll:
                        shareAllState = true;
                        presenter.onShareWithChanged(true);
                        break;

                    case R.id.shareWithFollowers:
                        shareAllState = false;
                        presenter.onShareWithChanged(false);
                        break;
                }
            }
        });
    }


    private void bindShareAsView()
    {
        publicationShareAs = findViewById(R.id.isAnonymous);

        publicationShareAs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                presenter.onShareAsChanged(b);
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA_CODE) {


                URI uri = cameraUtils.moveImageToGallery(cameraUtils.getBitmapFromPhoto(contentImage));
                contentImageState = uri.toString();
                contentImage.setImageURI(Uri.parse(uri.toString()));
            } else if (requestCode == REQUEST_GALLERY_CODE)
            {
                contentImageState = data.getData().toString();
                contentImage.setImageURI(data.getData());
            }
        }
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("ImageState", contentImageState);
        outState.putString("ContentState", contentTextState);
        outState.putBoolean("PublishState", publishPost.isEnabled());
        outState.putBoolean("ShareAsAnonymous", publicationShareAs.isChecked());
        outState.putBoolean("ShareWithAll", shareAllState);
        outState.putBoolean("IsUp", isUpState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        contentImageState = savedInstanceState.getString("ImageState");

        contentET.setText(savedInstanceState.getString("ContentState"));
        publishPost.setEnabled(savedInstanceState.getBoolean("PublishState"));


        if (savedInstanceState.getBoolean("ShareWithAll"))
        {
            publicationShareWith.check(R.id.shareWithAll);
            publicationShareAs.setEnabled(true);
        } else {
            publicationShareWith.check(R.id.shareWithFollowers);
            publicationShareAs.setEnabled(false);
        }

        if (savedInstanceState.getBoolean("IsUp")) contentAlign.check(R.id.radioUp);
        else contentAlign.check(R.id.radioDown);

        publicationShareAs.setChecked(savedInstanceState.getBoolean("ShareAsAnonymous"));



        if (contentImageState != null)
        {
            contentImage.setImageURI(Uri.parse(contentImageState));
        }
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        cameraUtils = new CameraUtils(getApplicationContext());


                        Intent intent = cameraUtils.getCameraIntent();
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
    public void enablePublishButton(boolean enable) {
        publishPost.setEnabled(enable);
    }

    @Override
    public void setMaxLettersText(String text) {
        contentMaxLetters.setText(text);
    }

    @Override
    public View getContentImage() {
        return findViewById(R.id.postContentImage);
    }

    @Override
    public View getContentText() {
        return findViewById(R.id.postContentText);
    }

    @Override
    public void enableShareAs(boolean enable) {
        publicationShareAs.setEnabled(enable);

        if (!enable) publicationShareAs.setChecked(false);
        else publicationShareAs.setChecked(true);

        shareAllState = enable;
    }
}
