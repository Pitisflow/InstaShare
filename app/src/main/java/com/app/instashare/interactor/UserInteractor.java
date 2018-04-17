package com.app.instashare.interactor;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
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























    public static void registerUser(FirebaseAuth auth, String email, String password, final OnRegistrationProcess process)
    {
        process.registering();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            process.registerSuccesfull();
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
        userBasic.setName((String) information.get(Constants.USERNAME_K));


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
                    .child(getUserKey()).child("images/" + photoName).putFile((Uri) information.get(Constants.USER_MAIN_IMAGE_K));

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getDownloadUrl() != null) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        DatabaseSingleton.getDbInstance().child(Constants.USERS_T)
                                .child(getUserKey()).child(Constants.USERS_BASIC_INFO_T)
                                .child(Constants.USER_MAIN_IMAGE_K).setValue(downloadUrl.getPath());
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




    public interface OnRegistrationProcess{

        void registering();

        void registerSuccesfull();

        void emailInUse();

        void usernameInUse();
    }
}
