package com.example.instagram_clone.model.user;

import android.util.Log;

import com.example.instagram_clone.utils.FirebaseConfig;

public class UserHelper {

    public static final String TAG = "UserHelper";
    public static class UsersNode {
        public static final String KEY = "users";
    }
    public static boolean saveOnDatabase(User user) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(UsersNode.KEY)
                    .child(user.getId())
                    .setValue(user);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage());
            return false;
        }
    }
}
