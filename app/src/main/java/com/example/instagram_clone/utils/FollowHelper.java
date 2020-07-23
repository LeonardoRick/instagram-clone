package com.example.instagram_clone.utils;

import android.util.Log;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;

public class FollowHelper {

    public static final String TAG = "FollowHelper";
    /**
     * save followed id --> following id
     * @param selectedUser followed
     */
    public static boolean follow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(selectedUser.getId())
                    .child(UserHelper.getLogged().getId())
                    .child(Constants.FollowNode.FOLLOWER)
                    .setValue(true);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "follow: " + e.getMessage());
            return false;
        }
    }

    /**
     * remove structure followed user id -> follower id
     * @param selectedUser followed
     */
    public static boolean unfollow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(selectedUser.getId())
                    .child(UserHelper.getLogged().getId())
                    .child(Constants.FollowNode.FOLLOWER)
                    .removeValue();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "unfollow: " + e.getMessage());
            return false;
        }
    }


}
