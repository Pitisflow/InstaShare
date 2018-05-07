package com.app.instashare.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.app.instashare.R;

/**
 * Created by Pitisflow on 5/5/18.
 */

public class AudioBar extends RelativeLayout{

    private ImageButton playStopButton;
    private SeekBar audioBar;
    private ImageView iconAudio;



    public AudioBar(Context context) {
        super(context);
        init(context);
    }

    public AudioBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.view_audio, this);

        playStopButton = findViewById(R.id.playStop);
        audioBar = findViewById(R.id.audio);
        iconAudio = findViewById(R.id.iconAudio);
    }

}
