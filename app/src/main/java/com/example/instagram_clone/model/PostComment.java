package com.example.instagram_clone.model;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;

import java.io.Serializable;

public class PostComment implements Serializable {

    String id;
    String userWhoCommentId;
    String comment;

    public PostComment() { generateNewId(); };

    public PostComment(String userWhoCommentId, String comment) {
        generateNewId();
        this.userWhoCommentId = userWhoCommentId;
        this.comment = comment;
    }

    public void generateNewId() {
        id = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PostNode.KEY)
                .push()
                .getKey();
    }

    public String getId() { return id; }
    public String getUserWhoCommentId() { return userWhoCommentId; }

    public String getComment() { return comment; }
}
