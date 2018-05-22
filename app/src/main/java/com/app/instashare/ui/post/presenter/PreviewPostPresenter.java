package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.other.activity.WebViewActivity;
import com.app.instashare.ui.post.activity.PreviewPostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.PreviewPostView;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Pitisflow on 1/5/18.
 */

public class PreviewPostPresenter {

    private Context context;
    private PreviewPostView view;
    private Post post;


    public PreviewPostPresenter(Context context, PreviewPostView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize(Post post)
    {
        this.post = post;

        //Set current content text
        if (post.getContentText() != null) setCurrentContentText(post.getContentText(), post.isAlignUp());
        else {
            view.enableTextDown(false);
            view.enableTextUp(false);
        }

        //Set current Image
        if (post.getMediaURL() != null) setCurrentContentImage(post.getMediaURL());
        else view.enableContentImage(false);

        //Set current User
        if (post.getUser() != null) setCurrentUser(post.getUser(), post.isAnonymous());

        //Set current tags
        if (post.getTags() != null && post.getTags().size() != 0) setCurrentTags(post.getTags());
        else view.enableTagsRecycler(false);


        //Set current Timestamp and Distance
        view.setDate(DateUtils.getPostDateFromTimestamp(post.getTimestamp(), context));
        view.setDistance(context.getString(R.string.distance_less_a_km));
    }



    public void onImageClicked()
    {
        Intent intent = PhotoViewActivity.newInstance(context, post.getMediaURL(), null, true);
        context.startActivity(intent);
    }



    private void setCurrentContentText(String text, boolean isUp)
    {
        if (isUp) {
            view.enableTextUp(true);
            view.enableTextDown(false);
            view.setTextUp(urlChecker(text));
        } else {
            view.enableTextUp(false);
            view.enableTextDown(true);
            view.setTextDown(urlChecker(text));
        }
    }

    //Override
    private void setCurrentContentImage(String imageURL)
    {
        view.setContentImage(Uri.parse(imageURL));
    }


    private void setCurrentUser(UserBasic user, boolean isAnonymous)
    {
        if (isAnonymous) view.setUserName("Anonymous");
        else {
            view.setUserName(user.getUsername());
            view.setUserImage(user.getMainImage());
        }
    }

    private void setCurrentTags(HashMap<String, Boolean> postTags)
    {
        PostRVAdapter adapter = new PostRVAdapter();
        adapter.setDeletableTag(false);

        ArrayList<String> tags = Utils.getTagStringsFromMap(postTags, context);
        ArrayList<Object> objectsTag = new ArrayList<>(tags);

        adapter.addCards(objectsTag, Constants.CARD_POST_TAG);

        view.setTagsRecyclerAdapter(adapter);
    }


    private CharSequence urlChecker(String contentText)
    {
        String[] words = contentText.split(" ");
        CharSequence sequence = "";

        for (String word : words)
        {
            if (Patterns.WEB_URL.matcher(word).matches()) {
                SpannableString spannableString =  Utils.getSpannableFromString(word,
                    context.getResources().getColor(R.color.colorPrimary), true, () -> {
                        Intent intent = WebViewActivity.newInstance(context, word);
                        context.startActivity(intent);
                    });

                sequence = TextUtils.concat(sequence, " ", spannableString);
                continue;
            }

            sequence = TextUtils.concat(sequence, " ", word);
        }

        sequence = TextUtils.concat(sequence, "\n");
        return sequence;
    }
}