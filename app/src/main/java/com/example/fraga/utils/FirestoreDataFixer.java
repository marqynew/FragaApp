package com.example.fraga.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreDataFixer {
    private static final String TAG = "FirestoreDataFixer";
    private static final String USERS_COLLECTION = "users";
    private static final String SOCIAL_SUBCOLLECTION = "social";
    private static final String FOLLOWERS_DOCUMENT = "followers";
    private static final String FOLLOWING_DOCUMENT = "following";
    private static final String USERS_FIELD = "users";

    private final FirebaseFirestore db;

    public FirestoreDataFixer() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<Void> fixAllUsersSocialData() {
        return db.collection(USERS_COLLECTION)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    QuerySnapshot snapshot = task.getResult();
                    List<Task<Void>> fixTasks = new ArrayList<>();

                    for (DocumentSnapshot userDoc : snapshot.getDocuments()) {
                        String userId = userDoc.getId();
                        fixTasks.add(fixUserSocialData(userId));
                    }

                    return Tasks.whenAll(fixTasks);
                });
    }

    private Task<Void> fixUserSocialData(String userId) {
        return Tasks.whenAll(
            fixFollowersDocument(userId),
            fixFollowingDocument(userId)
        );
    }

    private Task<Void> fixFollowersDocument(String userId) {
        DocumentReference followersRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWERS_DOCUMENT);

        return followersRef.get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    DocumentSnapshot doc = task.getResult();
                    WriteBatch batch = db.batch();

                    if (!doc.exists()) {
                        // Create new document with empty array
                        batch.set(followersRef, new java.util.HashMap<String, Object>() {{
                            put(USERS_FIELD, new ArrayList<String>());
                        }});
                    } else {
                        // Fix existing document
                        Object followers = doc.get(USERS_FIELD);
                        if (followers == null || !(followers instanceof List)) {
                            batch.set(followersRef, new java.util.HashMap<String, Object>() {{
                                put(USERS_FIELD, new ArrayList<String>());
                            }});
                        }
                    }

                    return batch.commit();
                });
    }

    private Task<Void> fixFollowingDocument(String userId) {
        DocumentReference followingRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SOCIAL_SUBCOLLECTION)
                .document(FOLLOWING_DOCUMENT);

        return followingRef.get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    DocumentSnapshot doc = task.getResult();
                    WriteBatch batch = db.batch();

                    if (!doc.exists()) {
                        // Create new document with empty array
                        batch.set(followingRef, new java.util.HashMap<String, Object>() {{
                            put(USERS_FIELD, new ArrayList<String>());
                        }});
                    } else {
                        // Fix existing document
                        Object following = doc.get(USERS_FIELD);
                        if (following == null || !(following instanceof List)) {
                            batch.set(followingRef, new java.util.HashMap<String, Object>() {{
                                put(USERS_FIELD, new ArrayList<String>());
                            }});
                        }
                    }

                    return batch.commit();
                });
    }

    public void fixData(OnDataFixCompleteListener listener) {
        Log.d(TAG, "Starting data fix process...");
        fixAllUsersSocialData()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Data fix completed successfully");
                    listener.onComplete(true, null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fixing data", e);
                    listener.onComplete(false, e);
                });
    }

    public interface OnDataFixCompleteListener {
        void onComplete(boolean success, Exception error);
    }
} 