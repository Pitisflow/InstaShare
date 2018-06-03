package com.app.instashare.interactor;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class UserInteractor {

    private static final int MAX_USERS_PER_PAGE = 20;


    public static String getUserKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return (user != null) ? user.getUid() : null;
    }




    //********************************************
    //DOWNLOADING USER/S OR FROM USER
    //********************************************

    public static void searchUsersByUsername(String username, final OnBasicInfoFetched onBasicInfoFetched)
    {
        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .orderByChild(Constants.USERNAME_K)
                .startAt(username.trim().toLowerCase())
                .endAt(username.trim().toLowerCase() + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onBasicInfoFetched.onSearchRefreshed();

                if (dataSnapshot.exists())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>(){};
                        HashMap<String, Object> map = child.getValue(t);

                        fetchUserBasicInfo((String) map.get(Constants.USERNAMES_USERKEY_K), onBasicInfoFetched);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public static void fetchUserBasicInfo(final String userKey, final OnBasicInfoFetched onBasicInfoFetched)
    {
        String route = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_BASIC_INFO_T);

        DatabaseSingleton.getDbInstance().child(route).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    UserBasic user = dataSnapshot.getValue(UserBasic.class);
                    user.setUserKey(userKey);

                    onBasicInfoFetched.onUserBasicFetched(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void updateUserLocation(Location location)
    {
        HashMap<String, Object> mLocation = new HashMap<>();
        mLocation.put(Constants.USER_LATITUDE_K, location.getLatitude());
        mLocation.put(Constants.USER_LONGITUDE_K, location.getLongitude());

        String route = Utils.createChild(Constants.USERS_T, getUserKey(), Constants.USERS_LOCACATION_T);
        DatabaseSingleton.getDbInstance().child(route).updateChildren(mLocation);
    }




    public static void downloadCompleteUser(String userKey, OnCompleteUserDownload userDownload)
    {
        userDownload.donwloading();

        DatabaseSingleton.getDbInstance().child(Constants.USERS_T).child(userKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);

                            if (user != null)
                            {
                                user.getBasicInfo().setUserKey(userKey);
                                userDownload.downloadSuccessful(user);
                            }
                        } else userDownload.downloadFailed();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    public static void donwloadFirstUserImages(String userKey, OnUserImagesDownload download)
    {
        String path = Utils.createChild(Constants.USER_IMAGES_T, userKey);

        DatabaseSingleton.getDbInstance().child(path).orderByKey().limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    ArrayList<String> imagesURL = new ArrayList<>();

                    for (DataSnapshot image : dataSnapshot.getChildren()) {
                        GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>(){};
                        HashMap<String, Object> map = image.getValue(t);

                        if (map != null && map.containsKey(Constants.USER_IMAGES_NAME_K)) {
                            imagesURL.add((String) map.get(Constants.USER_IMAGES_NAME_K));
                        }
                    }

                    download.downloadCompleted(imagesURL);
                } else download.downloadEmpty();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void downloadUsersFromList(String list, String userKey, OnUserListFetched listFetched)
    {
        String path = Utils.createChild(list, userKey);
        listFetched.onSearchRefreshed();

        DatabaseSingleton.getDbInstance().child(path).orderByKey().limitToFirst(MAX_USERS_PER_PAGE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            listFetched.numOfUsers(((Long) dataSnapshot.getChildrenCount()).intValue());

                            for (DataSnapshot user : dataSnapshot.getChildren())
                            {
                                String userKey = (String) user.getKey();
                                fetchUserBasicInfo(userKey, listFetched);
                            }
                        } else listFetched.numOfUsers(0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public static void downloadMoreUsersFromList(String list, String userKey, String startAt, OnUserListFetched listFetched)
    {
        String path = Utils.createChild(list, userKey);
        listFetched.onSearchRefreshed();

        DatabaseSingleton.getDbInstance().child(path).orderByKey().limitToFirst(MAX_USERS_PER_PAGE).startAt(startAt)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            listFetched.numOfUsers(((Long) dataSnapshot.getChildrenCount()).intValue());

                            for (DataSnapshot user : dataSnapshot.getChildren())
                            {
                                String userKey = (String) user.getKey();
                                fetchUserBasicInfo(userKey, listFetched);
                            }
                        } else listFetched.numOfUsers(0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    public static void downloadUserImages(String userKey, OnUserImagesDownload onUserImagesDownload)
    {
        String path = Utils.createChild(Constants.USER_IMAGES_T, userKey);

        DatabaseSingleton.getDbInstance().child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            ArrayList<String> imagesURLs = new ArrayList<>();

                            for (DataSnapshot imageURL : dataSnapshot.getChildren()) {
                                GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>(){};
                                HashMap<String, Object> map = imageURL.getValue(t);

                                imagesURLs.add((String) map.get(Constants.USER_IMAGES_NAME_K));
                            }
                            onUserImagesDownload.downloadCompleted(imagesURLs);
                        } else onUserImagesDownload.downloadEmpty();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //********************************************
    //REGISTERING USER
    //********************************************


    public static void registerUser(FirebaseAuth auth, Map<String, String> information,
                                    OnRegistrationProcess process)
    {
        process.registering();
        checkValidUsername(auth, information, process);
    }


    private static void checkValidUsername(final FirebaseAuth auth,
                                           final Map<String, String> information,
                                           final OnRegistrationProcess process)
    {
        String username = information.get("username");

        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .orderByChild(Constants.USERNAME_K).startAt(username.toLowerCase()).endAt(username.toLowerCase() + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            process.usernameInUse();
                        } else{
                            startRegistration(auth, information, process);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private static void startRegistration(final FirebaseAuth auth,
                                          final Map<String, String> information,
                                          final OnRegistrationProcess process)
    {
        String email = information.get("email");
        String password = information.get("password");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            process.registerSuccesfull();
                            createUserData(information);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try
                        {
                            throw e;
                        } catch(FirebaseAuthUserCollisionException e1)
                        {
                            process.emailInUse();
                        } catch (Exception e2) {
                            e.printStackTrace();
                        }
                    }

                });
    }


    private static void createUserData(final Map<String, String> information)
    {
        User user = new User();
        UserBasic userBasic = new UserBasic();


        Map<String, Object> info = new HashMap<>();
        info.put(Constants.USER_EMAIL_K, information.get(Constants.USER_EMAIL_K));
        info.put(Constants.USER_NAME_K, information.get(Constants.USER_NAME_K));
        info.put(Constants.USER_LAST_NAME_K, information.get(Constants.USER_LAST_NAME_K));
        info.put(Constants.USER_BIRTHDATE_K, information.get(Constants.USER_BIRTHDATE_K));
        info.put(Constants.USER_FIRST_TIMESTAMP_K, System.currentTimeMillis());


        userBasic.setName(information.get(Constants.USER_NAME_K));
        userBasic.setUsername(information.get(Constants.USERNAME_K));


        user.setInformation(info);
        user.setBasicInfo(userBasic);


        DatabaseSingleton.getDbInstance().child(Constants.USERS_T).child(getUserKey()).setValue(user);

        String pushKey = DatabaseSingleton.getDbInstance().push().getKey();
        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .child(pushKey).child(Constants.USERNAME_K).setValue(information.get(Constants.USERNAME_K).toLowerCase());

        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .child(pushKey).child(Constants.USERNAMES_USERKEY_K).setValue(getUserKey());




        if (information.containsKey(Constants.USER_MAIN_IMAGE_K)
                && information.get((Constants.USER_MAIN_IMAGE_K)) != null)
        {
            String storageRoute = Utils.createChild(Constants.USERS_T, getUserKey(), Constants.GENERAL_IMAGES);
            String imagesRoute = Utils.createChild(Constants.USER_IMAGES_T,
                    getUserKey(), String.valueOf(System.currentTimeMillis()), Constants.USER_IMAGES_NAME_K);

            String mainImageRoute = Utils.createChild(Constants.USERS_T, getUserKey(), Constants.USERS_BASIC_INFO_T, Constants.USER_MAIN_IMAGE_K);

            ArrayList<String> routes = new ArrayList<>();
            routes.add(imagesRoute);
            routes.add(mainImageRoute);


            ImagesInteractor.addImage(information.get(Constants.USER_MAIN_IMAGE_K),
                    storageRoute, routes, null);
        }
    }






    //********************************************
    //SIMPLE OPERATIONS
    //********************************************




    public static void checkIsFollowed(String userKey, String myUserkey, OnCheckedFollowedUser following)
    {
        String path = Utils.createChild(Constants.USER_FOLLOWING_T, myUserkey, userKey);

        DatabaseSingleton.getDbInstance().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) following.isFollowed();
                else following.isNotFollowed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void followUser(String userKey, String myUserKey, boolean unFollow)
    {
        String pathFollowing = Utils.createChild(Constants.USER_FOLLOWING_T, myUserKey, userKey);
        String pathFollowers = Utils.createChild(Constants.USER_FOLLOWERS_T, userKey, myUserKey);
        String pathUserFollowing = Utils.createChild(Constants.USERS_T, myUserKey, Constants.USERS_SOCIAL_T, Constants.USER_FOLLOWING_K);
        String pathUserFollowers = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_SOCIAL_T, Constants.USER_FOLLOWERS_K);
        String pathMyUserInfo = Utils.createChild(Constants.USERS_T, myUserKey, Constants.USERS_SOCIAL_T);
        String pathHisUserInfo = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_SOCIAL_T);


        if (!unFollow) {
            DatabaseSingleton.getDbInstance().child(pathFollowing).setValue(true);
            DatabaseSingleton.getDbInstance().child(pathFollowers).setValue(true);
        } else {
            DatabaseSingleton.getDbInstance().child(pathFollowing).removeValue();
            DatabaseSingleton.getDbInstance().child(pathFollowers).removeValue();
        }

        DatabaseSingleton.getDbInstance().child(pathUserFollowing).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    int value = ((Long) dataSnapshot.getValue()).intValue();

                    Map<String, Object> update = new HashMap<>();
                    if (!unFollow) update.put(Constants.USER_FOLLOWING_K, value + 1);
                    else update.put(Constants.USER_FOLLOWING_K, value - 1);
                    DatabaseSingleton.getDbInstance().child(pathMyUserInfo).updateChildren(update);
                } else {
                    Map<String, Object> update = new HashMap<>();
                    update.put(Constants.USER_FOLLOWING_K, 1);
                    DatabaseSingleton.getDbInstance().child(pathMyUserInfo).updateChildren(update);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseSingleton.getDbInstance().child(pathUserFollowers).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    int value = ((Long) dataSnapshot.getValue()).intValue();

                    Map<String, Object> update = new HashMap<>();
                    if (!unFollow) update.put(Constants.USER_FOLLOWERS_K, value + 1);
                    else update.put(Constants.USER_FOLLOWERS_K, value - 1);
                    DatabaseSingleton.getDbInstance().child(pathHisUserInfo).updateChildren(update);
                } else {
                    Map<String, Object> update = new HashMap<>();
                    update.put(Constants.USER_FOLLOWERS_K, 1);
                    DatabaseSingleton.getDbInstance().child(pathHisUserInfo).updateChildren(update);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void setUserImage(String userKey, String imageURL)
    {
        String path = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_BASIC_INFO_T, Constants.USER_MAIN_IMAGE_K);
        DatabaseSingleton.getDbInstance().child(path).setValue(imageURL);
    }

    public static void setBackgroundImage(String userKey, String imageURL)
    {
        String path = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_INFO_T, Constants.USER_BACKGROUND_K);
        DatabaseSingleton.getDbInstance().child(path).setValue(imageURL);
    }


    public static void updateUserInfo(String userKey, Map<String, Object> newInfo)
    {
        String path = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_INFO_T);
        String pathName = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_BASIC_INFO_T, Constants.USER_NAME_K);

        String name = "";

        if (newInfo.containsKey(Constants.USER_NAME_K) && newInfo.get(Constants.USER_NAME_K) != null) {
            name += (String) newInfo.get(Constants.USER_NAME_K);
        }

        if (newInfo.containsKey(Constants.USER_LAST_NAME_K) && newInfo.get(Constants.USER_LAST_NAME_K) != null) {
            if (name.length() != 0) name += " ";
            name += (String) newInfo.get(Constants.USER_LAST_NAME_K);
        }

        DatabaseSingleton.getDbInstance().child(path).updateChildren(newInfo);
        DatabaseSingleton.getDbInstance().child(pathName).setValue(name);
    }


    public static void updateUserPrivacy(String userKey, Map<String, Object> newPrivacy)
    {
        String path = Utils.createChild(Constants.USERS_T, userKey, Constants.USERS_PRIVACY_T);
        DatabaseSingleton.getDbInstance().child(path).updateChildren(newPrivacy);
    }



    //********************************************
    //USER INTERACTOR INTERFACES
    //********************************************

    public interface OnCompleteUserDownload
    {
        void donwloading();

        void downloadSuccessful(User user);

        void downloadFailed();
    }



    public interface OnBasicInfoFetched {

        void onSearchRefreshed();

        void onUserBasicFetched(UserBasic user);
    }


    public interface OnUserListFetched extends OnBasicInfoFetched {
        void numOfUsers(int num);
    }


    public interface OnRegistrationProcess{

        void registering();

        void registerSuccesfull();

        void emailInUse();

        void usernameInUse();
    }


    public interface OnCheckedFollowedUser
    {
        void isFollowed();

        void isNotFollowed();
    }


    public interface OnUserImagesDownload
    {
        void downloading();

        void downloadCompleted(ArrayList<String> images);

        void downloadEmpty();
    }
}
