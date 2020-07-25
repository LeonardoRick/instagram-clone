package com.example.instagram_clone.utils;

import android.util.Log;

import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;

public class FollowHelper {

    public static final String TAG = "FollowHelper";
    /**
     * save followed id --> following id
     * @param selectedUser followed
     * database:
     * follow
     *     <selectedUser id>
     *              <loggedUser id (followerId)>
     *                     follower : true
     */
    public static boolean follow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(selectedUser.getId())
                    .child(UserHelper.getLogged().getId())
                    .child(Constants.FollowNode.FOLLOWER)
                    .setValue(true);

            selectedUser.addFollower(UserHelper.getLogged().getId());
            UserHelper.updateOnDatabase(selectedUser);
            PostHelper.addAllOnFeed(selectedUser.getId());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "follow: " + e.getMessage());
            return false;
        }
    }


    /**
     * save followed id --> following id
     * @param selectedUser followed
     * database 1:
     * follow
     *     <selectedUser id>
     *              <loggedUser id (followerId)>
     *                     REMOVE THIS TREE
     *
     * database 2:
     * users
     *    <selectedUser id>
     *            followers : arraylist.remove(loggedUserId)
     *
     */
    public static boolean unfollow(User selectedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(selectedUser.getId())
                    .child(UserHelper.getLogged().getId())
                    .child(Constants.FollowNode.FOLLOWER)
                    .removeValue();

            selectedUser.removeFollower(UserHelper.getLogged().getId());
            UserHelper.updateOnDatabase(selectedUser);
            PostHelper.removeAllOnFeed(selectedUser.getId());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "unfollow: " + e.getMessage());
            return false;
        }
    }


}
