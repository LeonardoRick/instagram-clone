package com.example.instagram_clone.utils;

import android.util.Log;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;

public class FollowHelper {

    public static final String TAG = "FollowHelper";
    /**
     * save followed user inside structure follower id -> followed user id
     * @param selectedUser followed
     */
    public static boolean follow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(UserHelper.getLogged().getId())
                    .child(selectedUser.getId())
                    .setValue(true);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "follow: " + e.getMessage());
            return false;
        }
    }

    /**
     * remove structure follower id -> followed user id
     * @param selectedUser followed
     */
    public static boolean unfollow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(UserHelper.getLogged().getId())
                    .child(selectedUser.getId())
                    .removeValue();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "unfollow: " + e.getMessage());
            return false;
        }
    }
}
