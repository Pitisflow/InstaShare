package com.app.instashare.interactor;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class UserInteractor {

    public static String getUserKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return (user != null) ? user.getUid() : null;
    }






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








    public static void registerUser(FirebaseAuth auth, Map<String,
            Object> information, OnRegistrationProcess process)
    {
        process.registering();

        checkValidUsername(auth, information, process);
    }


    private static void checkValidUsername(final FirebaseAuth auth,
                                           final Map<String, Object> information,
                                           final OnRegistrationProcess process)
    {
        String username = (String) information.get("username");

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
                                          final Map<String, Object> information,
                                          final OnRegistrationProcess process)
    {
        String email = (String) information.get("email");
        String password = (String) information.get("password");

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


    private static void createUserData(final Map<String, Object> information)
    {
        User user = new User();
        UserBasic userBasic = new UserBasic();


        Map<String, Object> info = new HashMap<>();
        info.put(Constants.USER_EMAIL_K, information.get(Constants.USER_EMAIL_K));
        info.put(Constants.USER_NAME_K, information.get(Constants.USER_NAME_K));
        info.put(Constants.USER_LAST_NAME_K, information.get(Constants.USER_LAST_NAME_K));
        info.put(Constants.USER_BIRTHDATE_K, information.get(Constants.USER_BIRTHDATE_K));
        info.put(Constants.USER_FIRST_TIMESTAMP_K, System.currentTimeMillis());


        userBasic.setName((String) information.get(Constants.USER_NAME_K));
        userBasic.setUsername((String) information.get(Constants.USERNAME_K));


        user.setInformation(info);
        user.setBasicInfo(userBasic);


        DatabaseSingleton.getDbInstance().child(Constants.USERS_T).child(getUserKey()).setValue(user);

        String pushKey = DatabaseSingleton.getDbInstance().push().getKey();
        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .child(pushKey).child(Constants.USERNAME_K).setValue(((String) information.get(Constants.USERNAME_K)).toLowerCase());

        DatabaseSingleton.getDbInstance().child(Constants.USERNAMES_T)
                .child(pushKey).child(Constants.USERNAMES_USERKEY_K).setValue(getUserKey());





        if (information.containsKey(Constants.USER_MAIN_IMAGE_K)
                && information.get((Constants.USER_MAIN_IMAGE_K)) != null)
        {
            String[] splitted = ((Uri) information.get(Constants.USER_MAIN_IMAGE_K)).getPath().split("/");
            String photoName = splitted[splitted.length - 1];

            UploadTask task = DatabaseSingleton.getStorageInstance()
                    .child(Constants.USERS_T)
                    .child(getUserKey())
                    .child("images/" + photoName).putFile((Uri) information.get(Constants.USER_MAIN_IMAGE_K));

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getDownloadUrl() != null) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        DatabaseSingleton.getDbInstance().child(Constants.USERS_T)
                                .child(getUserKey()).child(Constants.USERS_BASIC_INFO_T)
                                .child(Constants.USER_MAIN_IMAGE_K).setValue(downloadUrl.toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }
    }





    public interface OnBasicInfoFetched {

        void onSearchRefreshed();

        void onUserBasicFetched(UserBasic user);
    }




    public interface OnRegistrationProcess{

        void registering();

        void registerSuccesfull();

        void emailInUse();

        void usernameInUse();
    }
}
