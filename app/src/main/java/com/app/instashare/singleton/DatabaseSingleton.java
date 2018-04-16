package com.app.instashare.singleton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class DatabaseSingleton {

    private static final String STORAGE_BUCKET_URL = "gs://instashare-d43f5.appspot.com";


    private static DatabaseReference databaseRef;
    private static StorageReference storageRef;

    public static DatabaseReference getDbInstance() {

        if (databaseRef == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            database.setPersistenceEnabled(true);
            databaseRef = database.getReference();
        }

        return databaseRef;
    }


    public static StorageReference getStorageInstance() {

        if (storageRef == null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl(STORAGE_BUCKET_URL);
        }

        return storageRef;
    }
}