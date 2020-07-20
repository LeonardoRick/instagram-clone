package com.example.instagram_clone.model.post;

import android.util.Log;

public class PostHelper {

    public static final String TAG = "PostHelper";
    /**
     * Save post on database within his id
     * @param post
     * @return boolean to control success of operations
     */
    public static boolean saveOnDatabase(Post post) {
        try {
            return true;
        } catch(Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage() );
            return false;
        }
    }

}