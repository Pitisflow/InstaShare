package com.app.instashare.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.widget.ImageView;

import com.app.instashare.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class CameraUtils {

    private Context context;


    public CameraUtils(Context context) {
        this.context = context;
    }



    public static Intent getCameraIntent(Context context, OnImagePathCreated listener)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        photoFile = createImageFile(context, listener);


        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context,
                    context.getString(R.string.file_provider),
                    photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


            MediaScannerConnection.scanFile(context, new String[]{photoFile.toString()}, null,
                    (path1, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + path1 + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    });



            return takePictureIntent;
        } else return null;
    }





    public static void compressImage(String path)
    {
        Bitmap bmp = BitmapFactory.decodeFile(path);

        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 18, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public Bitmap getBitmapFromPhoto(String path)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / 600, photoH / 600);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (path != null) return BitmapFactory.decodeFile(path, bmOptions);
        else return null;
    }



    public static void moveImageToGallery(String path, Context context)
    {
        File file = new File(path);

        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                (path1, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path1 + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
    }



    public static Uri imageUriFromString(String url)
    {
        Uri uri;

        if (!url.contains(":"))
        {
            File file = new File(url);
            uri = Uri.parse(file.toURI().toString());
        } else uri = Uri.parse(url);

        return uri;
    }





    private static File createImageFile(Context context, OnImagePathCreated listener) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";


        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + context.getString(R.string.app_name)
                + File.separator
                + context.getString(R.string.file_images_sent));



        if (fileDir.mkdirs()) Log.d("FileMkdirs", "Succesfull");
        else Log.d("FileMkdirs", "Already exists");


        File mediaFile = new File(fileDir.getPath() + File.separator + imageFileName);
        listener.pathCreated(mediaFile.getAbsolutePath());


        return mediaFile;
    }


    public interface OnImagePathCreated{
        void pathCreated(String path);
    }
}
