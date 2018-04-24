package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.ui.post.activity.PostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.AddPostView;
import com.app.instashare.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Pitisflow on 23/4/18.
 */

public class AddPostPresenter implements PostRVAdapter.OnDeleteTagListener{

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



    private static final int ET_MAX_LENGHT = 5000;
    private static ArrayList<String> tagNames;


    public AddPostPresenter(Context context, AddPostView view) {
        this.context = context;
        this.view = view;
        tags = new ArrayList<>();

        tagNames = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.post_tags)));
    }


    public void onInitialize()
    {
        view.setMaxLettersText("0/" + ET_MAX_LENGHT);

        isUpSelected = true;
        isShareWithAll = true;
        isAnonymous = true;


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, tagNames);
        view.setAutoCompleteAdapter(adapter);


        PostRVAdapter tagAapter = new PostRVAdapter();
        tagAapter.setDeleteTagListener(this);
        tagAapter.setDeletableTag(true);

        view.setTagRecyclerAdapter(tagAapter);
    }



    public void onContentTextChanged(String content)
    {
        contentText = content.trim();

        if (contentText.length() != 0) isContentTextEmpty = false;
        else isContentTextEmpty = true;

        view.setMaxLettersText(content.length() + "/" + ET_MAX_LENGHT);

        checkPostInformation();
    }


    public void onContentImageChanged()
    {


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
    }



    private void checkPostInformation()
    {
        if (!isContentImageEmpty || !isContentTextEmpty) view.enablePublishButton(true);
        else view.enablePublishButton(false);
    }




    public ActivityOptionsCompat generateOptions(AppCompatActivity activity)
    {
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<>(view.getContentImage(), context.getString(R.string.image_transition));
        pairs[1] = new Pair<>(view.getContentText(), context.getString(R.string.text_transition));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);

        return options;
    }


    public Post generatePost()
    {

        return null;
    }



    @Override
    public void deleteTag(String tagName) {
        tags.remove(tagName);
        view.deleteTagFromAdapter(tagName);
    }
}
