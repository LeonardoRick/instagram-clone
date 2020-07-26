package com.example.instagram_clone.model.post;

import android.util.Log;

import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

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
            if (followersId != null) {
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
            }

            return true;
        } catch(Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage() );
            return false;
        }
    }

    public static boolean updateOnDatabase(final Post post, final List<String> followersId) {

        final Map<String, Object> postMap = convertPostToMap(post);
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.PostNode.KEY)
                    .child(post.getUserId())
                    .child(post.getId())
                    .updateChildren(postMap);

            // saving feed info on new thread because user can have a lot of followers
            // and we don't want to lose performance on this process
            if (followersId != null) {
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
            }

            return true;
        } catch(Exception e) {
            Log.e(TAG, "updateOnDatabase: " + e.getMessage() );
            return false;
        }
    }

    public static boolean removeOnDatabase(final Post post, final List<String> followersId) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.PostNode.KEY)
                    .child(post.getUserId())
                    .child(post.getId())
                    .removeValue();

            // saving feed info on new thread because user can have a lot of followers
            // and we don't want to lose performance on this process
            if (followersId != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (String follower : followersId)
                            FirebaseConfig.getFirebaseDatabase()
                                    .child(Constants.FeedNode.KEY)
                                    .child(follower)
                                    .child(post.getId())
                                    .removeValue();
                    }
                }).start();
            }

            return true;
        } catch(Exception e) {
            Log.e(TAG, "removeOnDatabase: " + e.getMessage() );
            return false;
        }
    }


    /**
     * Add all posts of followed user on feed of logged user by
     * Acessing posts database and adding all posts on user feed
     * @param followedUserId
     * @return boolean to control success of operation
     */
    public static boolean addAllOnFeed(String followedUserId) {

        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.PostNode.KEY)
                    .child(followedUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    FirebaseConfig.getFirebaseDatabase()
                                        .child(Constants.FeedNode.KEY)
                                        .child(UserHelper.getLogged().getId())
                                        .child(postSnapshot.getKey())
                                        .setValue(postSnapshot.getValue(Post.class));
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
            return true;
        } catch (Exception e) {
            MessageHelper.showLongToast("Erro ao adicionar as imagens do usuário no seu feed");
            Log.e(TAG, "allAllOnFeed: " + e.getMessage());
            return false;
        }

    }

    /**
     * Remove all posts of unfollowed user on feed of logged user by
     * Acessing feed database and removing all posts from specified unfollowedUserId
     * @param unfollowedUserId
     * @return boolean to control success of operation
     */
    public static boolean removeAllOnFeed(String unfollowedUserId) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.FeedNode.KEY)
                    .child(UserHelper.getLogged().getId())
                    .orderByChild(Constants.PostNode.USER_ID)
                    .equalTo(unfollowedUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    postSnapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            return true;
        } catch (Exception e) {
            MessageHelper.showLongToast("Erro ao remover as imagens do seu feed");
            Log.e(TAG, "removeAllOnFeed: " + e.getMessage());
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
        if (post.getLikes() != null) userMap.put(Constants.PostNode.LIKES, post.getLikes());
        if (post.getUsersWhoLiked() != null) userMap.put(Constants.PostNode.USERS_WHO_LIKED, post.getUsersWhoLiked());

        return userMap;
    }

}