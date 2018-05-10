package com.app.instashare.interactor;

import android.net.Uri;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.utils.Constants;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 10/5/18.
 */

public class ImagesInteractor {



    public static void addImage(String imagePath, String storageRoute,
                                ArrayList<String> databaseRoutes, String document)
    {
        String[] splitted = imagePath.split("/");
        String photoName = splitted[splitted.length - 1];
        Uri uri;

        if (!imagePath.contains(":"))
        {
            File file = new File(imagePath);
            uri = Uri.parse(file.toURI().toString());
        } else uri = Uri.parse(imagePath);



        UploadTask task = DatabaseSingleton.getStorageInstance()
                .child(storageRoute)
                .child(photoName).putFile(uri);


        task.addOnSuccessListener(taskSnapshot -> {

            if (taskSnapshot.getDownloadUrl() != null) {
                String downloadURL = taskSnapshot.getDownloadUrl().toString();

                if (databaseRoutes != null && databaseRoutes.size() != 0) {
                    for (String route : databaseRoutes)
                    {
                        DatabaseSingleton.getDbInstance().child(route).setValue(downloadURL);
                    }
                }
                if (document != null) {
                    Map<String, Object> photo = new HashMap<>();
                    photo.put(Constants.POST_CONTENT_IMAGE_K, downloadURL);

                    DatabaseSingleton.getFirestoreInstance().collection(Constants.POSTS_T)
                            .document(document).update(photo);
                }
            }

        }).addOnFailureListener(e -> System.out.println(e.getMessage()));
    }
}
