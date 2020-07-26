package com.example.instagram_clone.model.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post implements Serializable {

    private String id;
    private String desc;
    private String imagePath;
    private String userId;
    private Integer likes = 0;

    private List<String> usersWhoLiked;

    public Post() {}

    public void addLike(String whoLikedId) {
        if (usersWhoLiked == null) usersWhoLiked = new ArrayList<>();
        usersWhoLiked.add(whoLikedId);

        if (likes == null) likes = 1;
        else likes++;
    }

    public void removeLike(String whoDislikedId) {
        if (usersWhoLiked == null) usersWhoLiked = new ArrayList<>();
        usersWhoLiked.remove(whoDislikedId);

        if (likes == null) likes = 1;
        else likes--;
    }


//    public void addComment(PostComment comment) {};
//
//    public void removeComment(PostComment comment) {};


    /**
     * Used to say that when comparing, if posts id are equal
     * their are the seme
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(getId(), post.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /********** getters and setters **********/
    public String getId() { return id; }
    public String getDesc() { return desc; }
    public String getImagePath() { return imagePath; }
    public String getUserId() { return userId; }
    public Integer getLikes() { return likes; }
    public List<String> getUsersWhoLiked() { return usersWhoLiked; }


    public void setId(String id) { this.id = id; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public void setUsersWhoLiked(List<String> usersWhoLiked) { this.usersWhoLiked = usersWhoLiked; }
}
