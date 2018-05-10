package com.app.instashare.interactor;

import android.util.Patterns;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 8/5/18.
 */

public class PostInteractor {


    public static void publishPost(Post post)
    {
        DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T).add(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (post.getMediaURL() != null) uploadPostImage(post, task.getResult().getId());
                    }
                });
    }


    private static void uploadPostImage(Post post, String postKey)
    {
        String storageRoute = Utils.createChild(Constants.POSTS_T,
                postKey, Constants.GENERAL_IMAGES);

        String databaseRoute = Utils.createChild(Constants.USER_IMAGES_T,
                post.getUser().getUserKey(), String.valueOf(System.currentTimeMillis()),
                Constants.USER_IMAGES_NAME_T);

        ArrayList<String> databaseRoutes = new ArrayList<>();
        if (post.isAnonymous()) databaseRoutes = null;
        else databaseRoutes.add(databaseRoute);

        ImagesInteractor.addImage(post.getMediaURL(), storageRoute, databaseRoutes, postKey);
    }
}
