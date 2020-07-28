package com.example.instagram_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
    
    private List<String> followersId;
    private List<String> followingsId;

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

    public void decrementCountPosts() {
        if (countPosts == null || countPosts == 0) countPosts = 0;
        else countPosts--;
    }

    public void addFollower(String id) {
        if (followersId == null)  {
            followersId = new ArrayList<>();
            countFollowers = 0;
        }
        followersId.add(id);
        countFollowers++;
    }

    public void removeFollower(String id) {
        if (followersId == null) return;
        followersId.remove(id);

        if (countFollowers == null || countFollowers == 0) countFollowers = 0;
        else countFollowers--;
    }

    public void startFollowing(String id) {
        if (followingsId == null)  {
            followingsId = new ArrayList<>();
            countFollowing = 0;
        }

        followingsId.add(id);
        countFollowing++;
    }

    public void stopFollowing(String id) {
        if (followingsId == null) return;
        followingsId.remove(id);

        if (countFollowing == null || countFollowing == 0) countFollowing = 0;
        else countFollowing--;
    }
    /********* getters and setters *******/

    public String getId() { return id; }

    public String getName() { return name; }

    // keep all get's for firebase work properly
    public String getNameToSearch() { return nameToSearch; }
    
    public String getEmail() { return email; }
    @Exclude
    public String getPassword() { return password; }

    public String getImagePath() { return imagePath; }

    public Integer getCountPosts() { return countPosts; }

    public Integer getCountFollowers() { return countFollowers; }

    public Integer getCountFollowing() { return countFollowing; }
    
    public List<String> getFollowersId() { return followersId; }

    public List<String> getFollowingsId() { return followingsId; }

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
