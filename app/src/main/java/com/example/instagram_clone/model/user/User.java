package com.example.instagram_clone.model.user;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;


public class User implements Serializable {


    private String id;
    private String name;
    private String email;
    private String password;
    private String picturePath;

    private int countPosts = 0;
    private int countFollowers = 0;
    private int countFollowing = 0;

    public User() {}

    /**
     * Used only to get user view FirebaseAuth (We need a Uri param in this case)
     */
    public User (String id, String name, String email, Uri picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        if (picture != null)
            this.picturePath = picture.toString();
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }


    /********* getters and setters *******/

    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public int getCountPosts() { return countPosts; }

    public int getCountFollowers() { return countFollowers; }

    public int getCountFollowing() { return countFollowing; }

    @Exclude
    public String getPassword() { return password; }

    public String getPicturePath() { return picturePath; }

    public void setId(String id) { this.id = id;}

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setPicturePath(String picturePath) {this.picturePath = picturePath; }

    public void setCountPosts(int countPosts) { this.countPosts = countPosts; }

    public void setCountFollowers(int countFollowers) { this.countFollowers = countFollowers; }

    public void setCountFollowing(int countFollowing) { this.countFollowing = countFollowing; }
}
