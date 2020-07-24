package com.example.instagram_clone.model.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {

    private String id;
    private String desc;
    private String imagePath;
    private String userId;
    private int likes = 0;

    private List<String> usersWhoLiked = new ArrayList<>();



    public Post() {}


    public void addLike() {
        likes++;
    }

    public void removeLike() {
        likes--;
    }
    /********** getters and setters **********/
    public String getId() { return id; }
    public String getDesc() { return desc; }
    public String getImagePath() { return imagePath; }
    public String getUserId() { return userId; }
    public int getLikes() { return likes; }
    public List<String> getUsersWhoLiked() { return usersWhoLiked; }


    public void setId(String id) { this.id = id; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setUsersWhoLiked(List<String> usersWhoLiked) { this.usersWhoLiked = usersWhoLiked; }
}
