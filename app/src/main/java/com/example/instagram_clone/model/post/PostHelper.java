package com.example.instagram_clone.model.post;

import android.util.Log;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostHelper {

    public static final String TAG = "PostHelper";
    /**
     * Save post on database within his id
     * @param post
     * @return boolean to control success of operations
     * post tree on firebase:
     *  posts
     *      <userId>
     *          <postId>
     *              POST OBJECT
     *
     * feed tree on firebase.
     * for each follower:
     *  feed
     *      <followerId>
     *          <postId>
     *              POST OBJECT
     */
    public static boolean saveOnDatabase(final Post post, final List<String> followersId) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.PostNode.KEY)
                    .child(post.getUserId())
                    .child(post.getId())
                    .setValue(post);

            // saving feed info on new thread because user can have a lot of followers
            // and we don't want to lose performance on this process
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String follower : followersId)
                    FirebaseConfig.getFirebaseDatabase()
                            .child(Constants.FeedNode.KEY)
                            .child(follower)
                            .child(post.getId())
                            .setValue(post);
                }
            }).start();
            return true;
        } catch(Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage() );
            return false;
        }
    }

    private boolean updateLikesOnDatabase(final Post post, final List<String> followersId) {

        final Map<String, Object> postMap = convertPostToMap(post);
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.PostNode.KEY)
                    .child(post.getUserId())
                    .child(post.getId())
                    .updateChildren(postMap);

            // saving feed info on new thread because user can have a lot of followers
            // and we don't want to lose performance on this process
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String follower : followersId)
                        FirebaseConfig.getFirebaseDatabase()
                                .child(Constants.FeedNode.KEY)
                                .child(follower)
                                .child(post.getId())
                                .updateChildren(postMap);
                }
            }).start();
            return true;
        } catch(Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage() );
            return false;
        }
    }

    /**
     * @param post to be converted to hashMap to firebase .updateChildren() accepts
     * @return Map<String, Object> where Object is user info
     */
    public static Map<String, Object> convertPostToMap(Post post) {
        Map<String, Object> userMap = new HashMap<>();

        if (post.getId() != null) userMap.put(Constants.PostNode.ID, post.getId());
        if (post.getUserId() != null) userMap.put(Constants.PostNode.USER_ID, post.getUserId());
        if (post.getDesc() != null) userMap.put(Constants.PostNode.DESC, post.getDesc());
        if (post.getImagePath() != null) userMap.put(Constants.PostNode.IMAGE_PATH, post.getImagePath());

        userMap.put(Constants.PostNode.LIKES, post.getLikes());
        userMap.put(Constants.PostNode.USERS_WHO_LIKED, post.getUsersWhoLiked());


        return userMap;
    }

}