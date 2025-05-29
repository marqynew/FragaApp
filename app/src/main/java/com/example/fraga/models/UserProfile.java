package com.example.fraga.models;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class UserProfile {
    private String userId;
    private String fullName;
    private String email;
    private String location;
    private String profileImageUrl;
    private String bio;
    private UserStats stats;
    private UserSocial social;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean following;

    // Empty constructor needed for Firestore
    public UserProfile() {}

    public UserProfile(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        this.stats = new UserStats();
        this.social = new UserSocial();
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getId() { return userId; }
    public void setId(String id) { this.userId = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public UserStats getStats() { return stats; }
    public void setStats(UserStats stats) { this.stats = stats; }

    public UserSocial getSocial() { return social; }
    public void setSocial(UserSocial social) { this.social = social; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    // Inner class for user stats
    public static class UserStats {
        private double totalDistance;
        private int totalActivities;
        private int totalKudos;

        public UserStats() {
            this.totalDistance = 0.0;
            this.totalActivities = 0;
            this.totalKudos = 0;
        }

        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

        public int getTotalActivities() { return totalActivities; }
        public void setTotalActivities(int totalActivities) { this.totalActivities = totalActivities; }

        public int getTotalKudos() { return totalKudos; }
        public void setTotalKudos(int totalKudos) { this.totalKudos = totalKudos; }
    }

    // Inner class for social stats
    public static class UserSocial {
        private List<String> followers;
        private List<String> following;

        public UserSocial() {
            this.followers = new ArrayList<>();
            this.following = new ArrayList<>();
        }

        public List<String> getFollowers() { return followers; }
        public void setFollowers(List<String> followers) { this.followers = followers; }

        public List<String> getFollowing() { return following; }
        public void setFollowing(List<String> following) { this.following = following; }

        public int getFollowersCount() { return followers != null ? followers.size() : 0; }
        public int getFollowingCount() { return following != null ? following.size() : 0; }
    }
} 