package com.example.instagram_clone.model;

import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.user.User;

public class FeedItem implements Comparable<FeedItem> {

    Post post;

    User userWhoPosted;

    public FeedItem() {}

    /**
     * To set that index of sort is post id
     * When sorting by Collections.sort()
     */
    @Override
    public int compareTo(FeedItem o) {
        return post.getId().compareTo(o.post.getId());
    }

    /*** getters and setters ***/
    public Post getPost() { return post; }

    public void setPost(Post post) { this.post = post; }

    public User getUserWhoPosted() { return userWhoPosted; }

    public void setUserWhoPosted(User userWhoPosted) { this.userWhoPosted = userWhoPosted; }


}
