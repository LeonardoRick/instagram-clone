package com.example.instagram_clone.model.post;

import java.io.Serializable;

public class Post implements Serializable {

    private String id;
    private String desc;
    private String imagePath;
    private String userId;

    public Post() {}

    /********** getters and setters **********/
    public String getId() { return id; }
    public String getDesc() { return desc; }
    public String getImagePath() { return imagePath; }
    public String getUserId() { return userId; }

    public void setId(String id) { this.id = id; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setUserId(String userId) { this.userId = userId; }
}
