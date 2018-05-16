package com.app.instashare.utils;

import android.content.Context;

import com.app.instashare.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        Date date = new Date(postTimestamp);
        DateFormat format = new SimpleDateFormat("dd MMMM yyyy', 'HH:mm", Locale.getDefault());



        if (difference < MINUTE) return context.getString(R.string.dateutils_while_ago);
        else if ((difference >= MINUTE) && (difference < HOUR)) {
            if (difference >= MINUTE && difference <= MINUTE * 2) return context.getString(R.string.dateutils_minute_ago);
            else return context.getString(R.string.dateutils_minutes_ago, difference / MINUTE);
        }
        else if ((difference >= HOUR) && (difference < DAY)) {
            if (difference >= HOUR && difference <= HOUR * 2) return context.getString(R.string.dateutils_hour_ago);
            else return context.getString(R.string.dateutils_hours_ago, difference / HOUR);
        }
        else if ((difference >= DAY) && (difference < DAY * 6)) {
            if (difference >= DAY && difference <= DAY * 2) return context.getString(R.string.dateutils_day_ago);
            else return context.getString(R.string.dateutils_days_ago, difference / DAY);
        }
        else return format.format(date);
    }


    public static String getAudioDurationFromMillis(int milliseconds)
    {
        Date date = new Date(milliseconds);
        DateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());

        return format.format(date);
    }
}
