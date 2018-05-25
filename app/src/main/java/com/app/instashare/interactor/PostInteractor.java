package com.app.instashare.interactor;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 8/5/18.
 */

public class PostInteractor {


    public static void publishPost(Post post, OnUploadingPost uploadingPost)
    {
        uploadingPost.preparingUpload();

        DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T).add(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uploadingPost.uploadDone();
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


    public static void getClosestPosts(int kilometers, Map<String, Object> location, OnDowloadingPosts listener)
    {
        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);

        GeoPoint maxPoint = LocationUtils.getMaxGeoPoint(kilometers, location);
        GeoPoint minPoint = LocationUtils.getMinGeoPoint(kilometers, location);

        Query query = reference
                .whereLessThan(Constants.GENERAL_LOCATION_K, maxPoint)
                .whereGreaterThan(Constants.GENERAL_LOCATION_K, minPoint);




//        Query query = reference.orderBy(Constants.POST_TIMESTAMP_K, Query.Direction.DESCENDING)
//                .endAt(System.currentTimeMillis());

        listener.downloading();

        query.get().addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                ArrayList<Post> posts = new ArrayList<>();

                System.out.println(task.getResult().size());

                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    Post post = documentSnapshot.toObject(Post.class);

                    if (post != null) {
                        post.setLocationMap(new HashMap<>(LocationUtils.getMapFromGeoPoint(post.getLocation())));
                        posts.add(post);
                    }
                }

                listener.downloadCompleted(posts);
            }
        }).addOnFailureListener(e -> System.out.println(e.getMessage()));
    }



    public interface OnUploadingPost
    {
        void preparingUpload();

        void uploadDone();
    }


    public interface OnDowloadingPosts
    {
        void downloading();

        void downloadCompleted(ArrayList<Post> posts);
    }
}
