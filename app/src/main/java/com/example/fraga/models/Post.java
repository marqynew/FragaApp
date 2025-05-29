package com.example.fraga.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String id;
    private String userId;
    private String content;
    private String imageUrl;
    private Date timestamp;
    private List<String> likes;
    private List<Comment> comments;

    public Post() {
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.timestamp = new Date();
    }

    public Post(String userId, String content) {
        this();
        this.userId = userId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static class Comment {
        private String id;
        private String userId;
        private String content;
        private Date timestamp;

        public Comment() {
            this.timestamp = new Date();
        }

        public Comment(String userId, String content) {
            this();
            this.userId = userId;
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }
} 