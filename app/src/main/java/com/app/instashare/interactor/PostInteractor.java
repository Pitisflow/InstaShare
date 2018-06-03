package com.app.instashare.interactor;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.model.Report;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

/**
 * Created by Pitisflow on 8/5/18.
 */

public class PostInteractor {


    public static final String POST_TYPE_SHARED = "shared";
    public static final int LIMIT_POSTS = 5;
    public static final int LIMIT_COMMENTS = 7;


    //********************************************
    //UPLOAD POST AND COMMENTS
    //********************************************


    public static void publishPost(Post post, OnUploadingPost uploadingPost)
    {
        String pathPostNumber = Utils.createChild(Constants.USERS_T, post.getUser().getUserKey(), Constants.USERS_SOCIAL_T, Constants.USER_POSTS_SHARED_K);
        uploadingPost.preparingUpload();



        DatabaseSingleton.getDbInstance().child(pathPostNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if ((dataSnapshot.getValue()) != null) {
                        int posts = ((Long) dataSnapshot.getValue()).intValue();
                        posts++;
                        DatabaseSingleton.getDbInstance().child(pathPostNumber).setValue(posts);
                    }
                } else DatabaseSingleton.getDbInstance().child(pathPostNumber).setValue(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                Constants.USER_IMAGES_NAME_K);

        ArrayList<String> databaseRoutes = new ArrayList<>();
        if (post.isAnonymous()) databaseRoutes = null;
        else databaseRoutes.add(databaseRoute);

        ImagesInteractor.addImage(post.getMediaURL(), storageRoute, databaseRoutes, postKey);
    }


    public static void publishComment(Comment comment, String postKey, String receiverKey, OnUploadingComment uploadingComment)
    {
        uploadingComment.preparingUpload();

        if (UserInteractor.getUserKey() != null) {
            String path = Utils.createChild(Constants.COMMENTS_T, postKey);
            String audioURL = comment.getAudioURL();
            String pushKey = DatabaseSingleton.getDbInstance().push().getKey();

            comment.setAudioURL(null);
            DatabaseSingleton.getDbInstance().child(path).child(pushKey).setValue(comment, (databaseError, databaseReference) -> {
                if (databaseError == null && audioURL != null) uploadAudio(audioURL, postKey, pushKey, uploadingComment);
                else uploadingComment.uploadCompleted(null);

                if (!UserInteractor.getUserKey().equals(receiverKey)){
                    NotificationInteractor.sendNotification(UserData.getUser().getBasicInfo(),
                            receiverKey, postKey, Constants.NOTIFICATION_TYPE_COMMENT);
                }
            });
        }
    }


    private static void uploadAudio(String audioURL, String postKey, String pushKey, OnUploadingComment uploadingComment)
    {
        String path = Utils.createChild(Constants.POSTS_T, postKey, Constants.POST_STORAGE_AUDIO);
        String databasePath = Utils.createChild(Constants.COMMENTS_T, postKey, pushKey, Constants.COMMENT_AUDIO_K);
        String [] splitted = audioURL.split("/");
        String audioName = splitted[splitted.length - 1];

        Uri uri = Uri.fromFile(new File(audioURL));
        UploadTask task = DatabaseSingleton.getStorageInstance()
                .child(path)
                .child(audioName).putFile(uri);

        task.addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getDownloadUrl() != null) {
                String downloadURL = taskSnapshot.getDownloadUrl().toString();

                DatabaseSingleton.getDbInstance().child(databasePath).setValue(downloadURL,
                        (databaseError, databaseReference) -> uploadingComment.uploadCompleted(taskSnapshot.getDownloadUrl().toString()));
            }
        }).addOnFailureListener(e -> uploadingComment.uploadFailed());

    }



    //********************************************
    //DOWNLOAD DATA AND LISTENERS
    //********************************************


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




//        Query query = reference.orderBy(Constants.GENERAL_TIMESTAMP_K, Query.Direction.DESCENDING)
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
        }).addOnFailureListener(e -> Log.d("Error", e.getMessage()));
    }


    public static void getPostsFromList(String userKey, String tree, long endAt, OnDownloadingPostPerPost listener)
    {
        String path = Utils.createChild(tree, userKey);

        listener.downloading();
        DatabaseSingleton.getDbInstance().child(path).orderByChild(Constants.GENERAL_TIMESTAMP_K)
            .endAt(endAt).limitToLast(LIMIT_POSTS)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        listener.downloadNumber((int) dataSnapshot.getChildrenCount());
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>(){};
                            HashMap<String, Object> map = data.getValue(t);

                            if (map != null && map.containsKey(Constants.POST_KEY_K) && map.get(Constants.POST_KEY_K) instanceof String)
                            {
                                downloadPostFromList((String) map.get(Constants.POST_KEY_K), listener);
                            }
                        }
                    } else listener.downloadNumber(0);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    private static void downloadPostFromList(String postKey, OnDownloadingPostPerPost listener)
    {
        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);

        reference.document(postKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Post post = task.getResult().toObject(Post.class);

                if (post != null) {
                    post.setPostKey(postKey);
                    post.setLocationMap(new HashMap<>(LocationUtils.getMapFromGeoPoint(post.getLocation())));
                    listener.downloadCompleted(post);
                }
            }
        });
    }


    public static void downloadSinglePost(String postKey, OnDownloadSinglePost listener)
    {
        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);

        reference.document(postKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Post post = task.getResult().toObject(Post.class);

                if (post != null)
                {
                    post.setPostKey(postKey);
                    post.setLocationMap(new HashMap<>(LocationUtils.getMapFromGeoPoint(post.getLocation())));
                    listener.downloadCompleted(post);
                }
            }
        });
    }


    public static void checkPostOnList(Post post, String userKey, String tree, OnCheckedList listener)
    {
        DatabaseSingleton.getDbInstance().child(tree).child(userKey).child(post.getPostKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) listener.isOnList();
                        else listener.isNotOnlist();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public static void downloadCommentsFromPost(String postKey, long endAt, OnDownloadComments downloadComments)
    {
        String path = Utils.createChild(Constants.COMMENTS_T, postKey);
        downloadComments.preparingDownload();

        DatabaseSingleton.getDbInstance().child(path).orderByChild(Constants.GENERAL_TIMESTAMP_K)
                .endAt(endAt).limitToLast(LIMIT_COMMENTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    ArrayList<Comment> comments = new ArrayList<>();
                    for (DataSnapshot comment : dataSnapshot.getChildren())
                    {
                        Comment comment1 = comment.getValue(Comment.class);

                        if (comment1 != null) {
                            comment1.setCommentKey(comment.getKey());
                            comments.add(comment1);
                        }
                    }

                    downloadComments.downloadCompleted(comments);
                } else downloadComments.downloadCompleted(new ArrayList<>());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public static void downloadFollowingPosts(String userKey, OnDownloadFollowingPosts listener)
    {
        String followersPath = Utils.createChild(Constants.USER_FOLLOWING_T, userKey);
        listener.downloadingFollowing();

        DatabaseSingleton.getDbInstance().child(followersPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> following = new ArrayList<>();


                    for (DataSnapshot userKey : dataSnapshot.getChildren()) {
                        following.add(userKey.getKey());
                    }
                    following.add(userKey);

                    for (String f : following) {
                        downloadUserPosts(f, listener);
                    }
                    listener.downloadFollowingNumber(following.size());
                } else {
                    listener.downloadFollowingNumber(1);
                    downloadUserPosts(userKey, listener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private static void downloadUserPosts(String userKey, OnDownloadFollowingPosts listener)
    {
        CollectionReference reference = DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T);

        reference.whereEqualTo(Constants.POST_USER_K + "." + Constants.USERNAMES_USERKEY_K, userKey)
                .whereEqualTo(Constants.POST_PUBLIC_K, false).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        ArrayList<Post> posts = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Post post = documentSnapshot.toObject(Post.class);

                            if (post != null) {
                                post.setPostKey(documentSnapshot.getId());
                                post.setLocationMap(new HashMap<>(LocationUtils.getMapFromGeoPoint(post.getLocation())));
                                posts.add(post);
                            }
                        }


                        listener.downloadFollowingCompleted(posts);
                        if (posts.size() == 0) listener.noPosts();
                    } else listener.noPosts();
                });
    }


    //********************************************
    //TRANSACTIONS
    //********************************************


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


    public static void modifyComments(String postKey, boolean plus)
    {
        DocumentReference document = DatabaseSingleton.getFirestoreInstance()
                .collection(Constants.POSTS_T)
                .document(postKey);


        DatabaseSingleton.getFirestoreInstance().runTransaction((Transaction.Function<Void>) transaction -> {
            Post post = transaction.get(document).toObject(Post.class);

            if (post != null) {
                if (plus) post.setNumComments(post.getNumComments() + 1);
                else post.setNumComments(post.getNumComments() - 1);
                transaction.set(document, post);
            }
            return null;
        });
    }




    //********************************************
    //SIMPLE OPERATIONS
    //********************************************

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


    public static void setPostAsHidden(Post post, String userKey)
    {
        String path = Utils.createChild(Constants.POSTS_HIDDEN_T, userKey, post.getPostKey());
        DatabaseSingleton.getDbInstance().child(path).setValue(true);
    }

    public static void removePostAsHidden(Post post, String userKey)
    {
        String path = Utils.createChild(Constants.POSTS_HIDDEN_T, userKey, post.getPostKey());
        DatabaseSingleton.getDbInstance().child(path).removeValue();
    }


    public static void addPostToList(Post post, String userKey, String tree)
    {
        Map<String, Object> postRed = new HashMap<>();
        postRed.put(Constants.GENERAL_TIMESTAMP_K, post.getTimestamp());
        postRed.put(Constants.POST_KEY_K, post.getPostKey());

        DatabaseSingleton.getDbInstance().child(tree)
                .child(userKey).child(post.getPostKey()).setValue(postRed);


        if (tree.equals(Constants.POSTS_LIKED_T) && !UserInteractor.getUserKey().equals(post.getUser().getUserKey())){
            NotificationInteractor.sendNotification(UserData.getUser().getBasicInfo(), userKey,
                    post.getPostKey(), Constants.NOTIFICATION_TYPE_LIKE);
        }

        if (tree.equals(Constants.POSTS_SHARED_T) && !UserInteractor.getUserKey().equals(post.getUser().getUserKey())) {
            NotificationInteractor.sendNotification(UserData.getUser().getBasicInfo(), userKey,
                    post.getPostKey(), Constants.NOTIFICATION_TYPE_SHARE);
        }
    }


    public static void removePostFromList(Post post, String userKey, String tree)
    {
        DatabaseSingleton.getDbInstance().child(tree).child(userKey).child(post.getPostKey()).removeValue();
    }

    public static void editComment(Comment comment, String newComment, OnCommentEditted editted)
    {
        String path = Utils.createChild(Constants.COMMENTS_T, comment.getPostKey(),
                comment.getCommentKey(), Constants.COMMENT_TEXT_K);

        DatabaseSingleton.getDbInstance().child(path).setValue(newComment, (databaseError, databaseReference) -> editted.onComplete());
    }


    public static void deleteComment(Comment comment)
    {
        String path = Utils.createChild(Constants.COMMENTS_T, comment.getPostKey(), comment.getCommentKey());
        DatabaseSingleton.getDbInstance().child(path).removeValue();
    }




    //********************************************
    //OTHERS
    //********************************************


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





    //********************************************
    //INTERFACES
    //********************************************

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

    public interface OnDownloadingPostPerPost
    {
        void downloading();

        void downloadNumber(int number);

        void downloadCompleted(Post post);
    }

    public interface OnCheckedList
    {
        void isOnList();

        void isNotOnlist();
    }

    public interface OnDownloadSinglePost
    {
        void downloadCompleted(Post post);
    }

    public interface OnUploadingComment
    {
        void preparingUpload();

        void uploadCompleted(String audioURL);

        void uploadFailed();
    }

    public interface OnDownloadComments
    {
        void preparingDownload();

        void downloadCompleted(ArrayList<Comment> comments);
    }


    public interface OnDownloadFollowingPosts
    {
        void downloadingFollowing();

        void downloadFollowingNumber(int number);

        void downloadFollowingCompleted(ArrayList<Post> posts);

        void noPosts();
    }


    public interface OnCommentEditted
    {
        void onComplete();
    }
}
