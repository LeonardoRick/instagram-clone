package com.example.instagram_clone.model.user;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;


public class User implements Serializable {


    private String id;
    private String name;
    private String nameToSearch;
    private String email;
    private String password;
    private String imagePath;

    private Integer countPosts;
    private Integer countFollowers;
    private Integer countFollowing;

    public User() {}

    /**
     * Used only to get user view FirebaseAuth (We need a Uri param in this case)
     */
    public User (String id, String name, String email, Uri picture) {
        this.id = id;
        this.name = name;
        if (name != null) this.nameToSearch = name.toLowerCase();
        this.email = email;
        if (picture != null)
            this.imagePath = picture.toString();
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.nameToSearch = name.toLowerCase();
        this.email = email;
        this.password = password;
    }

    public void incrementCountPosts() {
        if (countPosts == null) countPosts = 1;
        else countPosts++;
    }
    /********* getters and setters *******/

    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public Integer getCountPosts() { return countPosts; }

    public Integer getCountFollowers() { return countFollowers; }

    public Integer getCountFollowing() { return countFollowing; }

    public String getNameToSearch() { return nameToSearch; }

    @Exclude
    public String getPassword() { return password; }

    public String getImagePath() { return imagePath; }

    public void setId(String id) { this.id = id;}

    public void setName(String name) {
        this.name = name;
        this.nameToSearch = name.toLowerCase();
    }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setImagePath(String imagePath) {this.imagePath = imagePath; }

    public void setCountPosts(Integer countPosts) { this.countPosts = countPosts; }

    public void setCountFollowers(Integer countFollowers) { this.countFollowers = countFollowers; }

    public void setCountFollowing(Integer countFollowing) { this.countFollowing = countFollowing; }
}
