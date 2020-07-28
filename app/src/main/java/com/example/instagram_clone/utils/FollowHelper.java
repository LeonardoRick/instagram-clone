package com.example.instagram_clone.utils;

import android.util.Log;

import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;

public class FollowHelper {

    public static final String TAG = "FollowHelper";
    /**
     * save followed id --> following id
     * @param followerUser
     * @param toBeFollowedUser
     * database:
     * follow
     *     <selectedUser id>
     *              <loggedUser id (followerId)>
     *                     follower : true
     */
    public static boolean follow(User followerUser, User toBeFollowedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(toBeFollowedUser.getId())
                    .child(followerUser.getId())
                    .child(Constants.FollowNode.FOLLOWER)
                    .setValue(true);

            followerUser.startFollowing(toBeFollowedUser.getId());
            toBeFollowedUser.addFollower(followerUser.getId());

            UserHelper.updateOnDatabase(followerUser);
            UserHelper.updateOnDatabase(toBeFollowedUser);

            PostHelper.addAllOnFeed(toBeFollowedUser.getId());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "follow: " + e.getMessage());
            return false;
        }
    }


    /**
     * save followed id --> following id
     * @param unfollowerUser
     * @param toBeUnfollowedUser
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
    public static boolean unfollow(User unfollowerUser, User toBeUnfollowedUser) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FollowNode.KEY)
                    .child(toBeUnfollowedUser.getId())
                    .child(unfollowerUser.getId())
                    .removeValue();

            unfollowerUser.stopFollowing(toBeUnfollowedUser.getId());
            toBeUnfollowedUser.removeFollower(unfollowerUser.getId());

            UserHelper.updateOnDatabase(unfollowerUser);
            UserHelper.updateOnDatabase(toBeUnfollowedUser);

            PostHelper.removeAllOnFeed(unfollowerUser, toBeUnfollowedUser);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "unfollow: " + e.getMessage());
            return false;
        }
    }


}
