package com.app.instashare.utils;

import android.content.Context;

/**
 * Created by Pitisflow on 4/5/18.
 */

public class DateUtils {

    private static final long MINUTE = 60000;
    private static final long HOUR = 3600000;
    private static final long DAY = 86400000;




    public static String getPostDateFromTimestamp(long postTimestamp, Context context)
    {
        long currentTimestamp = System.currentTimeMillis();
        long difference = currentTimestamp - postTimestamp;

        if (difference < MINUTE) return "Hace un momento";
        else if ((difference >= MINUTE) && (difference < HOUR)) return "Hace " + difference / MINUTE + " minutos";
        else if ((difference >= HOUR) && (difference < DAY)) return "Hace " + difference / HOUR + " horas";
        else if ((difference >= DAY) && (difference < DAY * 5)) return "Hace " + difference / DAY + " dias";
        else return "mucho";
    }
}
