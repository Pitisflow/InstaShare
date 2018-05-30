package com.app.instashare.ui.post.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.custom.AudioBar;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.DetailActivityPresenter;
import com.app.instashare.ui.post.presenter.PreviewPostPresenter;
import com.app.instashare.ui.post.view.DetailPostView;
import com.app.instashare.ui.post.view.PreviewPostView;
import com.app.instashare.utils.CameraUtils;
import com.app.instashare.utils.Utils;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class DetailPostActivity extends PreviewPostActivity implements DetailPostView{

    private static final String EXTRA_POST = "post";
    private static final String EXTRA_POST_KEY = "postKey";


    public static Intent newInstance(Context context, Post post){
        Intent intent = new Intent(context, DetailPostActivity.class);
        intent.putExtra(EXTRA_POST, post);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }


    public static Intent newInstance(Context context, String postKey){
        Intent intent = new Intent(context, DetailPostActivity.class);
        intent.putExtra(EXTRA_POST_KEY, postKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }




    private Button likeButton;
    private Button shareButton;

    private EditText commentET;
    private AudioBar commentAudioBar;
    private ImageButton commentAudioRecord;
    private ImageButton commentSend;

    private Snackbar snackbar;


    private DetailActivityPresenter presenter;
    private String postKey;
    private String audioState;


    private static final int PERMISSION_CODE_RECORD = 2000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().hasExtra(EXTRA_POST_KEY)) {
            postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("post")) {
            setPost(savedInstanceState.getParcelable("post"));
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("audioState")) {
            audioState = savedInstanceState.getString("audioState");
        }


        bindCommentButtonView();
        bindLikeButtonView();
        bindShareButtonView();
        bindCommentView();
    }


    @Override
    protected void onStart() {
        super.onStart();

        presenter = new DetailActivityPresenter(this, this);
        presenter.onInitialize(getPost(), postKey);

        if (audioState != null) {
            commentAudioBar.setVisibility(View.VISIBLE);
            commentAudioBar.setAsyncFile(audioState);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            commentAudioRecord.setEnabled(true);
        }
        presenter.terminate();
        presenter = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getPost() != null) {
            outState.putParcelable("post", getPost());
        }


        if (audioState != null && commentAudioBar.getVisibility() == View.VISIBLE) {
            outState.putString("audioState", audioState);
        } else audioState = null;
    }




    private void bindShareButtonView()
    {
        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener((view) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailPostActivity.this);

            builder.setCancelable(false);
            builder.setMessage(getString(R.string.post_share_message));
            builder.setNegativeButton(getString(R.string.post_share_option_cancel), null);
            builder.setPositiveButton(getString(R.string.post_share_option_ok),
                    (dialogInterface, i) -> presenter.onShareButtonPressed());

            if (!getPost().isShared()) builder.show();
            else Toast.makeText(getApplicationContext(), getString(R.string.post_shared_already), Toast.LENGTH_SHORT).show();
        });
    }


    private void bindLikeButtonView()
    {
        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener((view) -> presenter.onLikeButtonPressed());
    }


    private void bindCommentButtonView()
    {
        Button commentButton = findViewById(R.id.commentButton);
        commentButton.setOnClickListener((view) -> presenter.onCommentButtonPressed());

        Drawable drawable = getDrawable(R.drawable.ic_mode_comment_black_24);
        drawable = Utils.changeDrawableColor(drawable, R.color.grayDark, getApplicationContext());
        commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }


    private void bindCommentView()
    {
        Drawable drawableSend = getDrawable(R.drawable.ic_send_black_24);
        Drawable drawableRecord = getDrawable(R.drawable.ic_keyboard_voice_black_24);
        drawableSend = Utils.changeDrawableColor(drawableSend, R.color.colorPrimary, getApplicationContext());
        drawableRecord = Utils.changeDrawableColor(drawableRecord, R.color.colorPrimary, getApplicationContext());


        commentET = findViewById(R.id.commentET);
        commentAudioBar = findViewById(R.id.audioBar);
        commentAudioRecord = findViewById(R.id.recordAudio);
        commentSend = findViewById(R.id.sendComment);



        commentAudioBar.setSeekBarColor(R.color.colorPrimary);
        commentAudioBar.setIconsColor(R.color.colorPrimary);
        commentAudioBar.isCancellable(true);

        commentAudioRecord.setImageDrawable(drawableRecord);
        commentSend.setImageDrawable(drawableSend);

        commentAudioRecord.setOnClickListener((view) -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    startRecording();
                } else {
                    ActivityCompat.requestPermissions(DetailPostActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_RECORD);
                }
            }


        });
    }


    private void startRecording()
    {
        commentAudioRecord.setEnabled(false);
        presenter.onStartRecordingPressed();
        Toast.makeText(getApplicationContext(), getString(R.string.post_comment_recording_toast), Toast.LENGTH_SHORT).show();

        snackbar = Snackbar.make(findViewById(R.id.content), getString(R.string.post_comment_recording_snackbar), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.post_comment_recording_snackbar_stop), view -> {

                    commentAudioRecord.setEnabled(true);
                    presenter.onStopRecordingPressed();
                });

        snackbar.show();
    }








    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE_RECORD && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            startRecording();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.post_comment_recording_permissions), Toast.LENGTH_SHORT).show();
        }

    }


    //********************************************
    //IMPLEMENTS VIEW
    //********************************************

    @Override
    public void onPostDownloaded(Post post) {
        setPost(post);
        super.onStart();
    }

    @Override
    public void enableLikeButton(boolean enabled) {
        likeButton.setEnabled(enabled);
    }

    @Override
    public void enableShareButton(boolean enabled) {
        shareButton.setEnabled(enabled);
    }

    @Override
    public void enableAudioView(boolean enabled) {
        if (enabled) commentAudioBar.setVisibility(View.VISIBLE);
        else commentAudioBar.setVisibility(View.GONE);
    }

    @Override
    public void setLikeButton(int color, Drawable drawable) {
        likeButton.setTextColor(color);
        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public void setShareButton(int color, Drawable drawable) {
        shareButton.setTextColor(color);
        shareButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public void setLocalAudioFile(String file) {
        audioState = file;
        commentAudioBar.setVisibility(View.VISIBLE);
        commentAudioBar.setAsyncFile(file);
    }

    @Override
    public void focusCommentET() {
        commentET.requestFocus();
        commentAudioBar.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
