package com.example.instagram_clone.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseConfig {

    public static FirebaseAuth auth;
    public static DatabaseReference databaseRef;
    public static FirebaseStorage storageRef;


    /**
     * Static method to keep FirebaseAuth as one instance on entire app
     * @return FirebaseAuth global instance
     */
    public static FirebaseAuth getAuth () {
        if (auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }

    /**
     * Static method to keep DatabaseReference as one instance on entire app
     * @return DatabaseReference global instance
     */
    public static DatabaseReference getFirebaseDatabase() {
        if (databaseRef == null) databaseRef = FirebaseDatabase.getInstance().getReference();
        return databaseRef;
    }

    /**
     * Static method to keep StorageReference as one instance on entire app
     * @return StorageReference global instance
     */
    public static FirebaseStorage getFirebaseStorage() {
        if (storageRef == null) storageRef = FirebaseStorage.getInstance();
        return storageRef;
    }
}
