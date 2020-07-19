package com.example.instagram_clone.model.user;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class User {


    private String id;
    private String name;
    private String email;
    private String password;
    private String picturePath;

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

    @Exclude
    public String getPassword() { return password; }

    public String getPicturePath() { return picturePath; }

    public void setId(String id) { this.id = id;}

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setPicturePath(String picturePath) {this.picturePath = picturePath; }
}
