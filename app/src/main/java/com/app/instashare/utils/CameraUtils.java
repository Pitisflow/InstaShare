package com.app.instashare.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class CameraUtils {


    private String mCurrentPhotoPath;
    private String imageFileName;

    private Bitmap bitmap;



    private Context context;


    public CameraUtils(Context context) {
        this.context = context;
    }



    public Intent getCameraIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;


        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context,
                    "com.app.instashare.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            return takePictureIntent;
        } else return null;
    }










    public Bitmap getBitmapFromPhoto(ImageView imageContainer)
    {
        int targetW = imageContainer.getWidth();
        int targetH = imageContainer.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (mCurrentPhotoPath != null) return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        else return null;
    }



    public void moveImageToGallery(Bitmap bitmap)
    {
        String appDirectoryName = "InstaShare";
        File fileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);



        fileDir.mkdir();
        File newFile = new File(fileDir, imageFileName);
        File oldFile = new File(mCurrentPhotoPath);

        oldFile.delete();


        try {
            FileOutputStream out = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context, new String[]{newFile.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }







    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + ".jpg";

        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/data/"
                + context.getPackageName()
                + "/files/Pictures");



        File mediaFile = new File(fileDir.getPath() + File.separator + imageFileName);
        mCurrentPhotoPath = mediaFile.getAbsolutePath();

        return mediaFile;
    }
}
