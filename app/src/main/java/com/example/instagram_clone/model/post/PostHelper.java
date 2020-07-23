package com.example.instagram_clone.model.post;

import android.util.Log;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;

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

    public static boolean savePostOnFollowersFeed(Post post, List<String> followersId) {
        try {
            DatabaseReference feedRef = FirebaseConfig.getFirebaseDatabase().child(Constants.FeedNode.KEY);
            for (String id : followersId)
                feedRef.child(id)
                        .child(post.getId());

            return true;
        } catch (Exception e) {
            Log.e(TAG, "savePostOnFollowersFeed" + e.getMessage());
            return false;
        }
    }

}