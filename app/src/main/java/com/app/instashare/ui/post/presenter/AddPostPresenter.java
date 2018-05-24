package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.AddPostView;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.utils.Utils;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class AddPostPresenter implements PostRVAdapter.OnDeleteTagListener, UserData.OnUserDataFetched{

    private Context context;
    private AddPostView view;


    private boolean isContentTextEmpty = true;
    private boolean isContentImageEmpty = true;


    private String contentText;
    private String contentImage;
    private boolean isUpSelected = true;
    private boolean isShareWithAll = true;
    private boolean isAnonymous = true;
    private ArrayList<String> tags;
    private GeoPoint location;


    private PostRVAdapter tagAdapter;
    private OnRequestPost listener;


    private static final int ET_MAX_LENGHT = 5000;
    private static ArrayList<String> tagNames;


    public AddPostPresenter(Context context, AddPostView view) {
        this.context = context;
        this.view = view;
        tags = new ArrayList<>();

        tagNames = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.tags)));
    }




    public void onInitialize()
    {
        view.setMaxLettersText("0/" + ET_MAX_LENGHT);

        isUpSelected = true;
        isShareWithAll = true;
        isAnonymous = true;


        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, tagNames);
        view.setAutoCompleteAdapter(adapter);


        tagAdapter = new PostRVAdapter();
        tagAdapter.setDeleteTagListener(this);
        tagAdapter.setDeletableTag(true);

        view.setTagRecyclerAdapter(tagAdapter);
    }




    public void onContentTextChanged(String content)
    {
        contentText = content.trim();

        if (contentText.length() != 0) isContentTextEmpty = false;
        else {
            isContentTextEmpty = true;
            contentText = null;
        }

        view.setMaxLettersText(content.length() + "/" + ET_MAX_LENGHT);

        checkPostInformation();
    }


    public void onContentImageChanged(String url)
    {
        contentImage = url;
        isContentImageEmpty = false;

        checkPostInformation();
    }


    public void onAlignTextChanged(boolean isUp)
    {
        isUpSelected = isUp;
    }


    public void onShareWithChanged(boolean isAll)
    {
        isShareWithAll = isAll;

        if (isShareWithAll) view.enableShareAs(true);
        else view.enableShareAs(false);
    }


    public void onShareAsChanged(boolean isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }





    public void addTagToRecycler(String tag)
    {
        String tagTrimmed = tag.trim();

        if (tagTrimmed.length() != 0) {
            String tagCapitalized = Utils.capitalize(tagTrimmed);

            if (!tags.contains(tagTrimmed) && tagNames.contains(tagCapitalized)) {
                tags.add(tagCapitalized);
                view.addTagToAdapter(tagCapitalized);
            }
        }

        checkPostInformation();
    }


    private void checkPostInformation()
    {
        if ((!isContentImageEmpty || !isContentTextEmpty) && tags.size() != 0) view.enablePublishButton(true);
        else view.enablePublishButton(false);
    }


    public void generatePost(GeoPoint location, OnRequestPost listener)
    {
        this.listener = listener;
        this.location = location;
        Post post = new Post();

        if (UserData.getUser() != null) {
            post.setUser(UserData.getUser().getBasicInfo());
            post.setLocation(location);
            setPostInfo(post);

            if (location != null) PostInteractor.publishPost(post);
            else listener.getPost(post);
        } else {
            UserData.getInstance(this);
        }
    }


    private void setPostInfo(Post post)
    {
        post.setTimestamp(System.currentTimeMillis());
        post.setContentText(contentText);
        post.setMediaURL(contentImage);
        post.setType("example");


        post.setAlignUp(isUpSelected);
        post.setAnonymous(isAnonymous);
        post.setForAll(isShareWithAll);

        post.setNumComments(0L);
        post.setNumLikes(0L);
        post.setNumShares(0L);

        post.setTags(Utils.getTagsMapFromStrings(tags, context));
    }





    public void terminate()
    {
        UserData.removeListener(this);
        tagAdapter.removeTagListener();
        view = null;
        listener = null;
    }


    @Override
    public void deleteTag(String tagName) {
        tags.remove(tagName);
        view.deleteTagFromAdapter(tagName);

        checkPostInformation();
    }

    @Override
    public void updateUserInfo() {
        User user = UserData.getUser();

        if (user != null)
        {
            Post post = new Post();
            post.setUser(user.getBasicInfo());
            post.setLocation(location);
            setPostInfo(post);

            if (location != null) PostInteractor.publishPost(post);
            else listener.getPost(post);
        }
    }


    public interface OnRequestPost{
        void getPost(Post post);
    }
}
