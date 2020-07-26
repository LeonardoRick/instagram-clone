package com.example.instagram_clone.utils;

public class Constants {

    public static class Labels {
        public static final String LIKES = " curtidas";
    }

    public static class FeatureRequest {
        public static final int STORAGE = 10;
        public static final int CAMERA = 20;
        public static final int SETTINGS = 30;

        public static final int NAV_BOTTOM_CODE = 100;
    }

    public static class IntentKey {
        public static String SELECTED_USER = "selected_user";
        public static String SELECTED_IMAGE = "selected_image";
        public static String SELECTED_POST = "selected_post";

        public static String NAV_BOTTOM = "NAV_BOTTOM" ;
    }

    public static class Storage {
        public static final String IMAGES = "images";
        public static final String PROFILE = "profile";
        public static final String POST = "post";
        public static final String JPEG = ".jpeg";
    }

    public static class UsersNode {
        public static final String KEY = "users";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String NAME_TO_SEARCH = "nameToSearch";
        public static final String EMAIL = "email";
        public static final String IMAGE_PATH = "imagePath";

        public static final String COUNT_POSTS = "countPosts";
        public static final String COUNT_FOLLOWERS = "countFollowers";
        public static final String COUNT_FOLLOWING = "countFollowing";

        public static final String FOLLOWERS_ID = "followersId";
        public static final String FOLLOWINGS_ID = "followingsId";

    }

    public static class FollowNode {
        public static final String KEY = "followers-list";
        public static final String FOLLOWER = "follower";
    }

    public static class PostNode  {
        public static final String KEY = "posts";
        public static final String ID = "id";
        public static final String USER_ID = "userId";
        public static final String DESC = "desc";
        public static final String IMAGE_PATH = "imagePath";
        public static final String LIKES = "likes";
        public static final String USERS_WHO_LIKED = "usersWhoLiked";
    }

    public static class FeedNode {
        public static final String KEY = "feed";
    }
}