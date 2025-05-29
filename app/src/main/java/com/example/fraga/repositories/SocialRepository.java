package com.example.fraga.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SocialRepository {
    private static final String TAG = "SocialRepository";
    private static final String USERS_COLLECTION = "users";
    private static final String SOCIAL_SUBCOLLECTION = "social";
    private static final String FOLLOWERS_DOCUMENT = "followers";
    private static final String FOLLOWING_DOCUMENT = "following";
    private static final String USERS_FIELD = "users";

    private final FirebaseFirestore db;

    public SocialRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Get user's followers
    public Task<DocumentSnapshot> getFollowers(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT)
                .get();
    }

    // Get user's following
    public Task<DocumentSnapshot> getFollowing(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT)
                .get();
    }

    // Follow a user
    public Task<Void> followUser(String currentUserId, String targetUserId) {
        Log.d(TAG, "Following user: " + targetUserId + " by user: " + currentUserId);
        
        WriteBatch batch = db.batch();

        // Add to current user's following
        DocumentReference currentUserFollowingRef = db.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT);

        // Add to target user's followers
        DocumentReference targetUserFollowersRef = db.collection(USERS_COLLECTION)
                .document(targetUserId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT);

        // Initialize documents if they don't exist and add the user
        batch.set(currentUserFollowingRef, 
                new java.util.HashMap<String, Object>() {{
                    put(USERS_FIELD, FieldValue.arrayUnion(targetUserId));
                }}, 
                com.google.firebase.firestore.SetOptions.merge());

        batch.set(targetUserFollowersRef, 
                new java.util.HashMap<String, Object>() {{
                    put(USERS_FIELD, FieldValue.arrayUnion(currentUserId));
                }}, 
                com.google.firebase.firestore.SetOptions.merge());

        return batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully followed user");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error following user", e);
                });
    }

    // Unfollow a user
    public Task<Void> unfollowUser(String currentUserId, String targetUserId) {
        Log.d(TAG, "Unfollowing user: " + targetUserId + " by user: " + currentUserId);
        
        WriteBatch batch = db.batch();

        // Remove from current user's following
        DocumentReference currentUserFollowingRef = db.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT);

        // Remove from target user's followers
        DocumentReference targetUserFollowersRef = db.collection(USERS_COLLECTION)
                .document(targetUserId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT);

        batch.update(currentUserFollowingRef, USERS_FIELD, FieldValue.arrayRemove(targetUserId));
        batch.update(targetUserFollowersRef, USERS_FIELD, FieldValue.arrayRemove(currentUserId));

        return batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully unfollowed user");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error unfollowing user", e);
                });
    }

    // Check if current user is following target user
    public Task<Boolean> isFollowing(String currentUserId, String targetUserId) {
        Log.d(TAG, "Checking if user: " + currentUserId + " is following: " + targetUserId);
        
        return getFollowingList(currentUserId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<String> following = task.getResult();
                        return following != null && following.contains(targetUserId);
                    }
                    return false;
                });
    }

    // Get user profiles for a list of user IDs
    public Task<QuerySnapshot> getUserProfiles(List<String> userIds) {
        return db.collection(USERS_COLLECTION)
                .whereIn("userId", userIds)
                .get();
    }

    public Task<List<String>> getFollowersList(String userId) {
        Log.d(TAG, "Getting followers for user: " + userId);
        
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<String> followers = (List<String>) task.getResult().get(USERS_FIELD);
                        return followers != null ? followers : new ArrayList<>();
                    }
                    return new ArrayList<>();
                });
    }

    public Task<List<String>> getFollowingList(String userId) {
        Log.d(TAG, "Getting following for user: " + userId);
        
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<String> following = (List<String>) task.getResult().get(USERS_FIELD);
                        return following != null ? following : new ArrayList<>();
                    }
                    return new ArrayList<>();
                });
    }

    // Initialize social documents for a new user
    public Task<Void> initializeSocialDocuments(String userId) {
        Log.d(TAG, "Initializing social documents for user: " + userId);
        
        WriteBatch batch = db.batch();

        DocumentReference followersRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT);

        DocumentReference followingRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT);

        batch.set(followersRef, new java.util.HashMap<String, Object>() {{
            put(USERS_FIELD, new ArrayList<String>());
        }}, com.google.firebase.firestore.SetOptions.merge());

        batch.set(followingRef, new java.util.HashMap<String, Object>() {{
            put(USERS_FIELD, new ArrayList<String>());
        }}, com.google.firebase.firestore.SetOptions.merge());

        return batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully initialized social documents");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error initializing social documents", e);
                });
    }
} 