package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.other.activity.PhotoViewActivity;
import com.app.instashare.ui.other.activity.WebViewActivity;
import com.app.instashare.ui.post.activity.DetailPostActivity;
import com.app.instashare.ui.post.activity.PreviewPostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.PreviewPostView;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.CameraUtils;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

        if (post != null) {

            //Set current content text
            if (post.getContentText() != null)
                setCurrentContentText(post.getContentText(), post.isAlignUp());
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
            if (post.getTags() != null && post.getTags().size() != 0)
                setCurrentTags(post.getTags());
            else view.enableTagsRecycler(false);


            //Set current Timestamp and Distance
            view.setDate(DateUtils.getPostDateFromTimestamp(post.getTimestamp(), context));
            setDistance(post.getLocationMap());
        }
    }



    private void setCurrentContentText(String text, boolean isUp)
    {
        if (isUp) {
            view.enableTextUp(true);
            view.enableTextDown(false);
            view.setTextUp(getText(text));
        } else {
            view.enableTextUp(false);
            view.enableTextDown(true);
            view.setTextDown(getText(text));
        }
    }

    private CharSequence getText(String string)
    {
        if (post.getType().equals(PostInteractor.POST_TYPE_SHARED))
        {
            String text = context.getString(R.string.post_shared_text);
            String[] words = text.split(" ");

            SpannableString ss = Utils.getSpannableFromString(words[words.length - 1],
                context.getResources().getColor(R.color.colorPrimary), false, true,
                () -> view.openNewPostActivity(post.getContentText()));

            words[words.length - 1] = "";

            return TextUtils.concat(TextUtils.join(" ", words), ss);
        } else return Utils.urlChecker(string, context);
    }


    //Override
    private void setCurrentContentImage(String imageURL)
    {
        view.setContentImage(CameraUtils.imageUriFromString(imageURL));
    }


    private void setCurrentUser(UserBasic user, boolean isAnonymous)
    {
        if (isAnonymous) view.setUserName(context.getString(R.string.post_anonymous));
        else {
            SpannableString username = Utils.getSpannableFromString(user.getUsername(),
                    context.getResources().getColor(R.color.black), false, false, () ->
                            view.openNewUserActivity(post.getUser().getUserKey()));

            view.setUserName(username);
            view.setUserImage(user.getMainImage());
            view.setUserImageClick(true, post.getUser().getUserKey());
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


    private void setDistance(HashMap<String, Double> location)
    {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null && location != null)
        {
            Location location1 = new Location("location1");
            Location location2 = new Location("location2");

            location1.setLongitude(location.get(Constants.USER_LONGITUDE_K));
            location1.setLatitude(location.get(Constants.USER_LATITUDE_K));

            location2.setLongitude((double) UserData.getUser().getLocation().get(Constants.USER_LONGITUDE_K));
            location2.setLatitude((double) UserData.getUser().getLocation().get(Constants.USER_LATITUDE_K));

            view.setDistance(LocationUtils.getDistanceBetween(location1, location2, context));
        } else view.setDistance(context.getString(R.string.distance_less_a_km));
    }



    public void terminate()
    {
        view = null;
    }
}