package com.example.instagram_clone.model.post;

public class Post {

    private String id;
    private String desc;
    private String picturePath;
    private String userId;

    public Post() {}

    /********** getters and setters **********/
    public String getId() { return id; }
    public String getDesc() { return desc; }
    public String getPicturePath() { return picturePath; }
    public String getUserId() { return userId; }

    public void setId(String id) { this.id = id; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setPicturePath(String picturePath) { this.picturePath = picturePath; }
    public void setUserId(String userId) { this.userId = userId; }
}
