package com.example.instagram_clone.model.user;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class User {

    @Nullable
    private String id;
    @Nullable
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @Nullable
    private String picturePath;


    public User() {}

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
