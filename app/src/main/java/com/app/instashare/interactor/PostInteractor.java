package com.app.instashare.interactor;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.model.Report;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 8/5/18.
 */

public class PostInteractor {


    public static final String POST_TYPE_SHARED = "shared";



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


    public static void getClosestPosts(int kilometers, Map<String, Object> location,
            boolean isRefreshing, OnDowloadingPosts listener)
    {
        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);

        GeoPoint maxPoint = LocationUtils.getMaxGeoPoint(kilometers, location);
        GeoPoint minPoint = LocationUtils.getMinGeoPoint(kilometers, location);

        Query query = reference
                .whereEqualTo(Constants.POST_PUBLIC_K, true)
                .whereLessThan(Constants.GENERAL_LOCATION_K, maxPoint)
                .whereGreaterThan(Constants.GENERAL_LOCATION_K, minPoint);




//        Query query = reference.orderBy(Constants.POST_TIMESTAMP_K, Query.Direction.DESCENDING)
//                .endAt(System.currentTimeMillis());

        listener.downloading(isRefreshing);

        query.get().addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                ArrayList<Post> posts = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    Post post = documentSnapshot.toObject(Post.class);

                    if (post != null) {
                        post.setPostKey(documentSnapshot.getId());
                        post.setLocationMap(new HashMap<>(LocationUtils.getMapFromGeoPoint(post.getLocation())));
                        posts.add(post);
                    }
                }

                listener.downloadCompleted(posts, isRefreshing);
            }
        }).addOnFailureListener(e -> System.out.println(e.getMessage()));
    }





    public static void addPostToList(Post post, String userKey, String tree)
    {
        Map<String, Object> postRed = new HashMap<>();
        postRed.put(Constants.POST_TIMESTAMP_K, post.getTimestamp());
        postRed.put(Constants.POST_KEY_K, post.getPostKey());

        DatabaseSingleton.getDbInstance().child(tree)
                .child(userKey).child(post.getPostKey()).setValue(postRed);
    }


    public static void removePostFromList(Post post, String userKey, String tree)
    {
        DatabaseSingleton.getDbInstance().child(tree).child(userKey).child(post.getPostKey()).removeValue();
    }


    public static void checkPostOnList(Post post, String userKey, String tree, OnCheckedList listener)
    {
        DatabaseSingleton.getDbInstance().child(tree).child(userKey).child(post.getPostKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) listener.isOnList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    public static void modifyLikes(String postKey, boolean plus)
    {
        DocumentReference document = DatabaseSingleton.getFirestoreInstance()
                .collection(Constants.POSTS_T)
                .document(postKey);

        DatabaseSingleton.getFirestoreInstance().runTransaction((Transaction.Function<Void>) transaction -> {
            Post post = transaction.get(document).toObject(Post.class);

            if (post != null) {
                if (plus) post.setNumLikes(post.getNumLikes() + 1);
                else post.setNumLikes(post.getNumLikes() - 1);
                transaction.set(document, post);
            }
            return null;
        });
    }


    public static void modifyShares(String postKey, boolean plus)
    {
        DocumentReference document = DatabaseSingleton.getFirestoreInstance()
                .collection(Constants.POSTS_T)
                .document(postKey);

        DatabaseSingleton.getFirestoreInstance().runTransaction((Transaction.Function<Void>) transaction -> {
            Post post = transaction.get(document).toObject(Post.class);

            if (post != null) {
                if (plus) post.setNumShares(post.getNumShares() + 1);
                else post.setNumShares(post.getNumShares() - 1);
                transaction.set(document, post);
            }
            return null;
        });
    }



    public static Post createSharedPost(Post postToShare, UserBasic user, GeoPoint point)
    {
        Post post = new Post();

        post.setType(POST_TYPE_SHARED);
        post.setLocation(point);
        post.setContentText(postToShare.getPostKey());
        post.setTimestamp(System.currentTimeMillis());
        post.setUser(user);
        post.setAlignUp(true);
        post.setAnonymous(false);
        post.setForAll(false);
        post.setNumLikes(0L);
        post.setNumShares(0L);
        post.setNumComments(0L);

        return post;
    }


    public static void removePost(Post post, OnDeletePost listener)
    {
        DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T).document(post.getPostKey())
                .delete().addOnCompleteListener(task -> listener.deletedSuccessfull());
    }


    public static void reportPost(Post post, String reportText, UserBasic user)
    {
        String path = Utils.createChild(Constants.POSTS_REPORTED_T, post.getPostKey());

        Report report = new Report();
        report.setUser(user);
        report.setPostKey(post.getPostKey());
        report.setReport(reportText);

        DatabaseSingleton.getDbInstance().child(path).push().setValue(report);
    }


    public static void setPostAsHided(Post post, String userKey)
    {
        String path = Utils.createChild(Constants.POSTS_HIDED_T, userKey, post.getPostKey());
        DatabaseSingleton.getDbInstance().child(path).setValue(true);
    }

    public static void removePostAsHided(Post post, String userKey)
    {
        String path = Utils.createChild(Constants.POSTS_HIDED_T, userKey, post.getPostKey());
        DatabaseSingleton.getDbInstance().child(path).removeValue();
    }






    public interface OnUploadingPost
    {
        void preparingUpload();

        void uploadDone();
    }


    public interface OnDeletePost
    {
        void deletedSuccessfull();
    }


    public interface OnDowloadingPosts
    {
        void downloading(boolean isRefreshing);

        void downloadCompleted(ArrayList<Post> posts, boolean isRefreshing);
    }

    public interface OnCheckedList
    {
        void isOnList();
    }
}
