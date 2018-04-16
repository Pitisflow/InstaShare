package com.app.instashare.singleton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class DatabaseSingleton {

    private static DatabaseReference databaseRef;

    public static DatabaseReference getDbInstance() {

        if (databaseRef == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            database.setPersistenceEnabled(true);
            databaseRef = database.getReference();
        }

        return databaseRef;
    }
}