package com.app.instashare.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.ui.other.activity.WebViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class Utils {



    public static Dialog createProgressDialog(Context context, String msg) {

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        return progressDialog;
    }



    public static Drawable changeDrawableSize(Drawable drawable, int size, Context context)
    {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));

        return d;
    }




    public static Drawable changeDrawableColor(Drawable drawable, int color, Context context){

        drawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }




    //Taken from https://stackoverflow.com/questions/1292575/android-textview-justify-text/42991773#42991773
    public static void justify(final TextView textView) {

        final AtomicBoolean isJustify = new AtomicBoolean(false);

        final String textString = textView.getText().toString();

        final TextPaint textPaint = textView.getPaint();

        final SpannableStringBuilder builder = new SpannableStringBuilder();

        textView.post(new Runnable() {
            @Override
            public void run() {

                if (!isJustify.get()) {

                    final int lineCount = textView.getLineCount();
                    final int textViewWidth = textView.getWidth();

                    for (int i = 0; i < lineCount; i++) {

                        int lineStart = textView.getLayout().getLineStart(i);
                        int lineEnd = textView.getLayout().getLineEnd(i);

                        String lineString = textString.substring(lineStart, lineEnd);

                        if (i == lineCount - 1) {
                            builder.append(new SpannableString(lineString));
                            break;
                        }

                        String trimSpaceText = lineString.trim();
                        String removeSpaceText = lineString.replaceAll(" ", "");

                        float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                        float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                        float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                        SpannableString spannableString = new SpannableString(lineString);
                        for (int j = 0; j < trimSpaceText.length(); j++) {
                            char c = trimSpaceText.charAt(j);
                            if (c == ' ') {
                                Drawable drawable = new ColorDrawable(0x00ffffff);
                                drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                                ImageSpan span = new ImageSpan(drawable);
                                spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        builder.append(spannableString);
                    }

                    textView.setText(builder);
                    isJustify.set(true);
                }
            }
        });
    }



    public static CharSequence urlChecker(String contentText, Context context)
    {
        String[] words = contentText.split(" ");
        CharSequence sequence = "";

        for (String word : words)
        {
            if (Patterns.WEB_URL.matcher(word).matches()) {
                SpannableString spannableString =  Utils.getSpannableFromString(word,
                        context.getResources().getColor(R.color.colorPrimary), true, false, () -> {
                            Intent intent = WebViewActivity.newInstance(context, word);
                            context.startActivity(intent);
                        });

                sequence = TextUtils.concat(sequence, " ", spannableString);
                continue;
            }

            sequence = TextUtils.concat(sequence, " ", word);
        }
        return sequence;
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




    public static SpannableString getSpannableFromString(String string, int color,
                                                         boolean isLink, boolean shouldBeBold, SpannableAction action)
    {
        SpannableString ss = new SpannableString(string);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                action.onSpannableClicked();
            }


            @Override
            public void updateDrawState(TextPaint ds) {
                if (isLink) {
                    ds.setUnderlineText(true);
                } else {
                    ds.setUnderlineText(false);
                    if (shouldBeBold) ds.setFakeBoldText(true);
                }

                ds.setColor(color);
            }
        };

        ss.setSpan(clickableSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }





    public static HashMap<String, Boolean> getTagsMapFromStrings(ArrayList<String> tags, Context context)
    {
        HashMap<String, Boolean> tagsMap = new HashMap<>();
        Resources res = context.getResources();

        tagsMap.put(Constants.TAG_TRAVEL_K, tags.contains(res.getString(R.string.tag_travel)));
        tagsMap.put(Constants.TAG_SPORT_K, tags.contains(res.getString(R.string.tag_sport)));
        tagsMap.put(Constants.TAG_ENTERTAIMENT_K, tags.contains(res.getString(R.string.tag_entertaiment)));
        tagsMap.put(Constants.TAG_GROUPS_K, tags.contains(res.getString(R.string.tag_groups)));
        tagsMap.put(Constants.TAG_PHOTO_K, tags.contains(res.getString(R.string.tag_photo)));
        tagsMap.put(Constants.TAG_VIDEO_K, tags.contains(res.getString(R.string.tag_video)));
        tagsMap.put(Constants.TAG_PLACES_K, tags.contains(res.getString(R.string.tag_places)));
        tagsMap.put(Constants.TAG_LIFESTYLE_K, tags.contains(res.getString(R.string.tag_lifestyle)));
        tagsMap.put(Constants.TAG_PARTY_K, tags.contains(res.getString(R.string.tag_party)));
        tagsMap.put(Constants.TAG_MEETINGS_K, tags.contains(res.getString(R.string.tag_meetings)));
        tagsMap.put(Constants.TAG_WORK_K, tags.contains(res.getString(R.string.tag_work)));
        tagsMap.put(Constants.TAG_FOOD_K, tags.contains(res.getString(R.string.tag_food)));
        tagsMap.put(Constants.TAG_POLITIC_K, tags.contains(res.getString(R.string.tag_politic)));
        tagsMap.put(Constants.TAG_SOCIAL_K, tags.contains(res.getString(R.string.tag_social)));
        tagsMap.put(Constants.TAG_CURIOSITIES_K, tags.contains(res.getString(R.string.tag_curiosities)));
        tagsMap.put(Constants.TAG_ANIMALS_K, tags.contains(res.getString(R.string.tag_animals)));

        return tagsMap;
    }


    public static ArrayList<String> getTagStringsFromMap(HashMap<String, Boolean> tags, Context context)
    {
        ArrayList<String> tagsArray = new ArrayList<>();
        Resources res = context.getResources();

        if (tags.containsKey(Constants.TAG_TRAVEL_K) && tags.get(Constants.TAG_TRAVEL_K)) tagsArray.add(res.getString(R.string.tag_travel));
        if (tags.containsKey(Constants.TAG_SPORT_K) && tags.get(Constants.TAG_SPORT_K)) tagsArray.add(res.getString(R.string.tag_sport));
        if (tags.containsKey(Constants.TAG_ENTERTAIMENT_K) && tags.get(Constants.TAG_ENTERTAIMENT_K)) tagsArray.add(res.getString(R.string.tag_entertaiment));
        if (tags.containsKey(Constants.TAG_GROUPS_K) && tags.get(Constants.TAG_GROUPS_K)) tagsArray.add(res.getString(R.string.tag_groups));
        if (tags.containsKey(Constants.TAG_PHOTO_K) && tags.get(Constants.TAG_PHOTO_K)) tagsArray.add(res.getString(R.string.tag_photo));
        if (tags.containsKey(Constants.TAG_VIDEO_K) && tags.get(Constants.TAG_VIDEO_K)) tagsArray.add(res.getString(R.string.tag_video));
        if (tags.containsKey(Constants.TAG_PLACES_K) && tags.get(Constants.TAG_PLACES_K)) tagsArray.add(res.getString(R.string.tag_places));
        if (tags.containsKey(Constants.TAG_LIFESTYLE_K) && tags.get(Constants.TAG_LIFESTYLE_K)) tagsArray.add(res.getString(R.string.tag_lifestyle));
        if (tags.containsKey(Constants.TAG_PARTY_K) && tags.get(Constants.TAG_PARTY_K)) tagsArray.add(res.getString(R.string.tag_party));
        if (tags.containsKey(Constants.TAG_MEETINGS_K) && tags.get(Constants.TAG_MEETINGS_K)) tagsArray.add(res.getString(R.string.tag_meetings));
        if (tags.containsKey(Constants.TAG_WORK_K) && tags.get(Constants.TAG_WORK_K)) tagsArray.add(res.getString(R.string.tag_work));
        if (tags.containsKey(Constants.TAG_FOOD_K) && tags.get(Constants.TAG_FOOD_K)) tagsArray.add(res.getString(R.string.tag_food));
        if (tags.containsKey(Constants.TAG_POLITIC_K) && tags.get(Constants.TAG_POLITIC_K)) tagsArray.add(res.getString(R.string.tag_politic));
        if (tags.containsKey(Constants.TAG_SOCIAL_K) && tags.get(Constants.TAG_SOCIAL_K)) tagsArray.add(res.getString(R.string.tag_social));
        if (tags.containsKey(Constants.TAG_CURIOSITIES_K) && tags.get(Constants.TAG_CURIOSITIES_K)) tagsArray.add(res.getString(R.string.tag_curiosities));
        if (tags.containsKey(Constants.TAG_ANIMALS_K) && tags.get(Constants.TAG_ANIMALS_K)) tagsArray.add(res.getString(R.string.tag_animals));

        return tagsArray;
    }




    public interface SpannableAction {
        void onSpannableClicked();
    }
}
