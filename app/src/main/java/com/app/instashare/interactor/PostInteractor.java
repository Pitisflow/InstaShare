package com.app.instashare.interactor;

import android.util.Patterns;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 8/5/18.
 */

public class PostInteractor {


    public static void publishPost(Post post)
    {
        String imageURL = post.getMediaURL();
        post.setMediaURL(null);


        DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T).add(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) System.out.println(task.getResult().getId());
                });
    }
}
