package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
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

    public PreviewPostPresenter(Context context, PreviewPostView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize(Post post)
    {
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



    private void setCurrentContentText(String text, boolean isUp)
    {
        if (isUp) {
            view.enableTextUp(true);
            view.enableTextDown(false);
            view.setTextUp(text + "\n");
        } else {
            view.enableTextUp(false);
            view.enableTextDown(true);
            view.setTextDown(text + "\n");
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


    private void urlChecker(String contentText)
    {
        String[] words = contentText.split(" ");

        for (String word : words)
        {
            System.out.println(word + " " + Patterns.WEB_URL.matcher(word).matches());
        }
    }
}