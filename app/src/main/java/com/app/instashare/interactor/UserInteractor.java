package com.app.instashare.interactor;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by Pitisflow on 15/4/18.
 */

public class UserInteractor {






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
                            process.registerFailure();
                        } catch (Exception e2) {
                            e.printStackTrace();
                        }
                    }

                });

    }





    public interface OnRegistrationProcess{

        void registering();

        void registerSuccesfull();

        void registerFailure();
    }
}
