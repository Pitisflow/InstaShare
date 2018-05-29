package com.app.instashare.ui.post.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;
import com.xw.repo.BubbleSeekBar;

/**
 * Created by Pitisflow on 29/5/18.
 */

public class PublicPostsSettings extends AppCompatActivity {

    private BubbleSeekBar seekBar;
    private Switch showHidden;

    private boolean showHiddenChecked;
    private int radius;
    private boolean currentShowHidden;
    private int currentRadius;

    private SharedPreferences preferences;
    public static final String NEED_RELOAD = "reload";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bindShowHiddenView();
        bindSeekBarView();
        bindToolbarView();
        bindApplyView();


        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        seekBar.setProgress(preferences.getInt(Constants.PREFERENCES_RADIUS, 1));
        showHidden.setChecked(preferences.getBoolean(Constants.PREFERENCES_SHOW_HIDDEN, false));
    }

    private void bindShowHiddenView()
    {
        showHidden = findViewById(R.id.showHidden);
        showHidden.setOnCheckedChangeListener((compoundButton, b) -> showHiddenChecked = b);
    }

    private void bindSeekBarView()
    {
        TextView approximately = findViewById(R.id.approximately);
        seekBar = findViewById(R.id.kilometers);
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                int hyopotenuse = LocationUtils.getDistanceHypotenuse(progress);
                radius = progress;
                approximately.setText(getString(R.string.post_setting_radius_approximate, progress, hyopotenuse));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
    }

    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.post_settings));
        }
    }

    private void bindApplyView()
    {
        Button button = findViewById(R.id.apply);
        button.setOnClickListener((view) -> {
            if (currentShowHidden != showHiddenChecked || currentRadius != radius)
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(Constants.PREFERENCES_RADIUS, radius);
                editor.putBoolean(Constants.PREFERENCES_SHOW_HIDDEN, showHiddenChecked);
                editor.apply();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(NEED_RELOAD, true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                finish();
            }
        });
    }
}
