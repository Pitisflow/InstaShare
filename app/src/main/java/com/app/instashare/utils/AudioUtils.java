package com.app.instashare.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.app.instashare.R;
import com.app.instashare.singleton.UserData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class AudioUtils {

    public static File makeAudioFile(Context context)
    {
        if (UserData.getUser() != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String audioFileName = "AUDIO_" + timeStamp + "_" + UserData.getUser().getBasicInfo().getUsername() + ".3gp";


            File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + context.getString(R.string.app_name)
                    + File.separator
                    + context.getString(R.string.file_audios_sent));


            if (fileDir.mkdirs()) Log.d("FileMkdirs", "Succesfull");
            else Log.d("FileMkdirs", "Already exists");

            File fileMedia = new File(fileDir.getPath() + File.separator + audioFileName);

            return fileMedia;
        } else return null;
    }
}
