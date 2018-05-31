package com.app.instashare.custom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pitisflow on 5/5/18.
 */

public class AudioBar extends RelativeLayout implements MediaPlayer.OnPreparedListener{

    private ImageButton playPauseButton;
    private SeekBar audioBar;
    private ImageView iconAudio;
    private TextView duration;


    private MediaPlayer player;
    private Handler handler;
    private Runnable runnable;
    private String file;


    private int currentIconsColor;
    private boolean isAsync = false;
    private boolean isLoaded = false;
    private boolean isStopped = false;


    private static final int pauseDrawable = R.drawable.ic_pause_black_24;
    private static final int playDrawable = R.drawable.ic_play_arrow_black_24;


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
        currentIconsColor = R.color.black;
        handler = new Handler();


        bindAudioView();
        bindPlayPauseView();
        bindSeekbarView();
        bindDurationView();
    }




    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);

        if (!isStopped && player != null) {
            player.stop();
            player.release();
        }
        player = null;
    }

    private void bindPlayPauseView()
    {
        playPauseButton = findViewById(R.id.playPause);
        playPauseButton.setImageDrawable(getContext().getDrawable(playDrawable));

        playPauseButton.setOnClickListener(view ->{
            if (player != null && player.isPlaying()) {
                pause();
            } else if (player != null){
                if (isAsync && !isLoaded && player.getCurrentPosition() == 0) {
                    playPauseButton.setEnabled(false);
                    player.setOnPreparedListener(AudioBar.this);
                    player.prepareAsync();
                } else if (isAsync){
                    setPauseIcon();
                    player.start();
                    playCycle();
                }

                if (!isAsync && !isLoaded && player.getCurrentPosition() == 0)
                {
                    playPauseButton.setEnabled(false);
                    player.setOnPreparedListener(AudioBar.this);
                } else if (!isAsync) {
                    setPauseIcon();
                    player.start();
                    playCycle();
                }
            }
        });
    }



    private void bindAudioView()
    {
        iconAudio = findViewById(R.id.iconAudio);
        iconAudio.setImageDrawable(getContext().getDrawable(R.drawable.ic_keyboard_voice_black_24));
    }


    private void bindSeekbarView()
    {
        audioBar = findViewById(R.id.audio);
        audioBar.setEnabled(false);

        audioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setPlayIcon();
                player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
    }


    private void bindDurationView()
    {
        duration = findViewById(R.id.duration);
        duration.setText("00:00");
    }





    private void setPauseIcon()
    {
        Drawable drawable = Utils.changeDrawableColor(getContext().
                getDrawable(pauseDrawable), currentIconsColor, getContext());

        playPauseButton.setImageDrawable(drawable);
    }


    private void setPlayIcon()
    {
        Drawable drawable = Utils.changeDrawableColor(getContext().
                getDrawable(playDrawable), currentIconsColor, getContext());

        playPauseButton.setImageDrawable(drawable);
    }


    private void setDuration(String duration)
    {
        this.duration.setText(duration);
    }


    public void setIconsColor(int color)
    {
        currentIconsColor = color;

        if (playPauseButton.getDrawable() != null)
        {
            Drawable drawable = playPauseButton.getDrawable();
            playPauseButton.setImageDrawable(Utils.changeDrawableColor(drawable, color, getContext()));
        }

        if (iconAudio.getDrawable() != null)
        {
            Drawable drawable = iconAudio.getDrawable();
            iconAudio.setImageDrawable(Utils.changeDrawableColor(drawable, color, getContext()));
        }
    }


    public void setSeekBarColor(int color)
    {
        audioBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.MULTIPLY);
        audioBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_ATOP);
    }

    public void isCancellable(boolean cancellable)
    {
        if (cancellable)
        {
            Drawable drawable = getContext().getDrawable(R.drawable.ic_close_black_36);
            iconAudio.setImageDrawable(Utils.changeDrawableColor(drawable, currentIconsColor, getContext()));
            iconAudio.setOnClickListener(view -> {
                this.setVisibility(View.GONE);
                handler.removeCallbacks(runnable);
                isStopped = true;
                isLoaded = false;

                File file = new File(this.file);
                file.delete();

                if (player != null) {
                    player.stop();
                    player.release();
                }
            });
        }
    }

    public void reset()
    {
        this.setVisibility(View.GONE);
        handler.removeCallbacks(runnable);
        isStopped = true;
        isLoaded = false;

        if (player != null) {
            player.stop();
            player.release();
        }
    }


    public void setAsyncFile(String file)
    {
        if (file != null) {
            this.file = file;
            isAsync = true;
            player = new MediaPlayer();

            try {
                player.setDataSource(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setLocalFile(String file)
    {
        isAsync = false;
        player = new MediaPlayer();

        try {
            player.setDataSource(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pause()
    {
        setPlayIcon();
        player.pause();
    }



    private void playCycle()
    {
        audioBar.setProgress(player.getCurrentPosition());
        setDuration(DateUtils.getAudioDurationFromMillis(player.getCurrentPosition()));

        if (player.isPlaying())
        {
            runnable = this::playCycle;
            handler.postDelayed(runnable, 30);
        }
    }



    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        audioBar.setMax(mediaPlayer.getDuration());
        setDuration(DateUtils.getAudioDurationFromMillis(mediaPlayer.getDuration()));
        playPauseButton.setEnabled(true);
        audioBar.setEnabled(true);
        player = mediaPlayer;
        isLoaded = true;
        setPauseIcon();

        player.setOnCompletionListener(mp -> setPlayIcon());
        player.start();
        playCycle();
    }
}