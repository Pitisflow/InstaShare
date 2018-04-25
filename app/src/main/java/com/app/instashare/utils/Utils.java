package com.app.instashare.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.app.instashare.R;

import java.util.ArrayList;
import java.util.HashMap;

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


    public static String createChild(String... tree)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tree.length; i++)
        {
            sb.append(tree[i]);
            sb.append("/");
        }

        return sb.toString();
    }


    public static String capitalize(String string)
    {
        String lowerCase = string.toLowerCase();
        return Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
    }



    public static HashMap<String, Boolean> getTagsMapFromStrings(ArrayList<String> tags, Context context)
    {
        HashMap<String, Boolean> tagsMap = new HashMap<>();
        Resources res = context.getResources();

        if (tags.contains(res.getString(R.string.tag_travel))) tagsMap.put(Constants.TAG_TRAVEL_K, true);
        if (tags.contains(res.getString(R.string.tag_sport))) tagsMap.put(Constants.TAG_SPORT_K, true);
        if (tags.contains(res.getString(R.string.tag_entertaiment))) tagsMap.put(Constants.TAG_ENTERTAIMENT_K, true);
        if (tags.contains(res.getString(R.string.tag_groups))) tagsMap.put(Constants.TAG_GROUPS_K, true);
        if (tags.contains(res.getString(R.string.tag_photo))) tagsMap.put(Constants.TAG_PHOTO_K, true);
        if (tags.contains(res.getString(R.string.tag_video))) tagsMap.put(Constants.TAG_VIDEO_K, true);
        if (tags.contains(res.getString(R.string.tag_places))) tagsMap.put(Constants.TAG_PLACES_K, true);
        if (tags.contains(res.getString(R.string.tag_lifestyle))) tagsMap.put(Constants.TAG_LIFESTYLE_K, true);
        if (tags.contains(res.getString(R.string.tag_party))) tagsMap.put(Constants.TAG_PARTY_K, true);
        if (tags.contains(res.getString(R.string.tag_meetings))) tagsMap.put(Constants.TAG_MEETINGS_K, true);
        if (tags.contains(res.getString(R.string.tag_work))) tagsMap.put(Constants.TAG_WORK_K, true);
        if (tags.contains(res.getString(R.string.tag_food))) tagsMap.put(Constants.TAG_FOOD_K, true);
        if (tags.contains(res.getString(R.string.tag_politic))) tagsMap.put(Constants.TAG_POLITIC_K, true);
        if (tags.contains(res.getString(R.string.tag_social))) tagsMap.put(Constants.TAG_SOCIAL_K, true);
        if (tags.contains(res.getString(R.string.tag_curiosities))) tagsMap.put(Constants.TAG_CURIOSITIES_K, true);

        return tagsMap;
    }



    public static ArrayList<String> getTagStringsFromMap(HashMap<String, Boolean> tags, Context context)
    {
        ArrayList<String> tagsArray = new ArrayList<>();
        Resources res = context.getResources();

        if (tags.containsKey(Constants.TAG_TRAVEL_K)) tagsArray.add(res.getString(R.string.tag_travel));
        if (tags.containsKey(Constants.TAG_SPORT_K)) tagsArray.add(res.getString(R.string.tag_sport));
        if (tags.containsKey(Constants.TAG_ENTERTAIMENT_K)) tagsArray.add(res.getString(R.string.tag_entertaiment));
        if (tags.containsKey(Constants.TAG_GROUPS_K)) tagsArray.add(res.getString(R.string.tag_groups));
        if (tags.containsKey(Constants.TAG_PHOTO_K)) tagsArray.add(res.getString(R.string.tag_photo));
        if (tags.containsKey(Constants.TAG_VIDEO_K)) tagsArray.add(res.getString(R.string.tag_video));
        if (tags.containsKey(Constants.TAG_PLACES_K)) tagsArray.add(res.getString(R.string.tag_places));
        if (tags.containsKey(Constants.TAG_LIFESTYLE_K)) tagsArray.add(res.getString(R.string.tag_lifestyle));
        if (tags.containsKey(Constants.TAG_PARTY_K)) tagsArray.add(res.getString(R.string.tag_party));
        if (tags.containsKey(Constants.TAG_MEETINGS_K)) tagsArray.add(res.getString(R.string.tag_meetings));
        if (tags.containsKey(Constants.TAG_WORK_K)) tagsArray.add(res.getString(R.string.tag_work));
        if (tags.containsKey(Constants.TAG_FOOD_K)) tagsArray.add(res.getString(R.string.tag_food));
        if (tags.containsKey(Constants.TAG_POLITIC_K)) tagsArray.add(res.getString(R.string.tag_politic));
        if (tags.containsKey(Constants.TAG_SOCIAL_K)) tagsArray.add(res.getString(R.string.tag_social));
        if (tags.containsKey(Constants.TAG_CURIOSITIES_K)) tagsArray.add(res.getString(R.string.tag_curiosities));

        return tagsArray;
    }
}
