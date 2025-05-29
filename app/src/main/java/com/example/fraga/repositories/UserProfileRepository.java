package com.example.fraga.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fraga.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;

public class UserProfileRepository {
    private static final String TAG = "UserProfileRepository";
    private static final String COLLECTION_USERS = "users";
    
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final SocialRepository socialRepository;
    
    public UserProfileRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        socialRepository = new SocialRepository();
        
        // Enable offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
        db.setFirestoreSettings(settings);
    }
    
    public void createProfile(UserProfile profile, OnProfileUpdatedListener listener) {
        if (profile == null || profile.getUserId() == null) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Invalid profile data")));
            return;
        }

        Log.d(TAG, "Creating profile for user: " + profile.getUserId());
        
        // First create the profile
        db.collection(COLLECTION_USERS)
            .document(profile.getUserId())
            .set(profile, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Profile created successfully");
                // Then initialize social documents
                socialRepository.initializeSocialDocuments(profile.getUserId())
                    .addOnSuccessListener(aVoid2 -> {
                        Log.d(TAG, "Social documents initialized successfully");
                        listener.onComplete(Tasks.forResult(null));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error initializing social documents", e);
                        listener.onComplete(Tasks.forException(e));
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error creating profile", e);
                listener.onComplete(Tasks.forException(e));
            });
    }
    
    public void getProfile(String userId, OnProfileLoadedListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Invalid user ID")));
            return;
        }

        Log.d(TAG, "Fetching profile for user: " + userId);
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                    if (profile != null) {
                        Log.d(TAG, "Profile loaded successfully");
                        listener.onComplete(Tasks.forResult(profile));
                    } else {
                        Log.e(TAG, "Failed to convert document to UserProfile");
                        listener.onComplete(Tasks.forException(new FirebaseFirestoreException(
                            "Failed to convert document to UserProfile",
                            FirebaseFirestoreException.Code.INTERNAL)));
                    }
                } else {
                    Log.d(TAG, "Profile not found, creating new profile");
                    // Create a new profile if it doesn't exist
                    UserProfile newProfile = new UserProfile(userId, auth.getCurrentUser().getDisplayName());
                    newProfile.setEmail(auth.getCurrentUser().getEmail());
                    createProfile(newProfile, task -> {
                        if (task.isSuccessful()) {
                            listener.onComplete(Tasks.forResult(newProfile));
                        } else {
                            listener.onComplete(Tasks.forException(task.getException()));
                        }
                    });
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching profile", e);
                listener.onComplete(Tasks.forException(e));
            });
    }
    
    public void updateProfile(UserProfile profile, OnProfileUpdatedListener listener) {
        if (profile == null || profile.getUserId() == null) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Invalid profile data")));
            return;
        }

        Log.d(TAG, "Updating profile for user: " + profile.getUserId());
        
        db.collection(COLLECTION_USERS)
            .document(profile.getUserId())
            .set(profile, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Profile updated successfully");
                listener.onComplete(Tasks.forResult(null));
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating profile", e);
                listener.onComplete(Tasks.forException(e));
            });
    }
    
    public void updateProfileField(String userId, String field, Object value, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS).document(userId)
            .update(field, value, "updatedAt", com.google.firebase.Timestamp.now())
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Profile field updated successfully");
                listener.onComplete(Tasks.forResult(null));
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating profile field", e);
                listener.onComplete(Tasks.forException(e));
            });
    }

    public Task<DocumentSnapshot> getProfile(String userId) {
        return db.collection(COLLECTION_USERS)
                .document(userId)
                .get();
    }

    public Task<QuerySnapshot> getUserProfiles(List<String> userIds) {
        return db.collection(COLLECTION_USERS)
                .whereIn("userId", userIds)
                .get();
    }

    public Task<Void> updateProfile(String userId, Map<String, Object> updates) {
        return db.collection(COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile", e);
                });
    }

    public Task<List<UserProfile>> getAllUsers() {
        return db.collection("users")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toObjects(UserProfile.class);
                    } else {
                        Log.e(TAG, "Error getting users", task.getException());
                        throw task.getException();
                    }
                });
    }

    public interface OnProfileLoadedListener {
        void onComplete(Task<UserProfile> task);
    }

    public interface OnProfileUpdatedListener {
        void onComplete(Task<Void> task);
    }
} 