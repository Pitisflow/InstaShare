package com.app.instashare.interactor;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;
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


    public static void getNearestPosts(int kilometers, Map<String, Object> location)
    {
        double userLatitude = (double) location.get(Constants.USER_LATITUDE_K);
        double userLongitude = (double) location.get(Constants.USER_LONGITUDE_K);

        double maxLatitude = LocationUtils.getMaxLatitudeCoordinate(kilometers);
        double maxLongitude = LocationUtils.getMaxLongitudeCoordinate(kilometers);

        double maxLatitudeOffset = userLatitude + maxLatitude;
        double minLatitudeOffset = userLatitude - maxLatitude;
        double maxLongitudeOffset = userLongitude + maxLongitude;
        double minLongitudeOffset = userLongitude - maxLongitude;

        String latitudeField = Constants.GENERAL_LOCATION_K + "." + Constants.USER_LATITUDE_K;
        String longitudeField = Constants.GENERAL_LOCATION_K + "." + Constants.USER_LONGITUDE_K;


        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);


        Query query = reference.whereLessThanOrEqualTo(longitudeField, 80);
//        query.whereLessThanOrEqualTo(latitudeField, 80);
//        query.whereGreaterThanOrEqualTo(longitudeField, 70);
//        query.whereGreaterThanOrEqualTo(latitudeField, 70);


        GeoPoint point = new GeoPoint(40.9686266, -5.6491449);

        Map<String, Object> map = new HashMap<>();
        map.put("location", point);

        reference.document("Ls5EmHX3VaOzQkUxrYpp").update(map);
        reference.document("tX8Ngm1TvCCeDO4mobnX").update(map);

        reference.get().addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    Post post = documentSnapshot.toObject(Post.class);

                    //System.out.println(post.getContentText());
                }
            }
        });
    }
}
