package com.app.instashare.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

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
}
