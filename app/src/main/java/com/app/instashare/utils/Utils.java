package com.app.instashare.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class Utils {



    public static Dialog createProgressDialog(Context context, String msg) {

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);

        return progressDialog;
    }



    public static Drawable changeDrawableSize(Drawable drawable, int size, Context context)
    {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));

        return d;
    }
}
