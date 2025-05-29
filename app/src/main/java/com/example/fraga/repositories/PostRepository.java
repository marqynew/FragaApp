package com.example.fraga.repositories;

import android.util.Log;
import com.example.fraga.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

public class PostRepository {
    private static final String TAG = "PostRepository";
    private final FirebaseFirestore db;
    private final CollectionReference postsCollection;

    public PostRepository() {
        db = FirebaseFirestore.getInstance();
        postsCollection = db.collection("posts");
    }

    public Task<List<Post>> getUserPosts(String userId) {
        return postsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toObjects(Post.class);
                    } else {
                        Log.e(TAG, "Error getting user posts", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<DocumentReference> createPost(Post post) {
        return postsCollection
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Post created with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating post", e);
                });
    }

    public Task<Void> deletePost(String postId) {
        return postsCollection
                .document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting post", e);
                });
    }

    public Task<Void> likePost(String postId, String userId) {
        return postsCollection
                .document(postId)
                .update("likes", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post liked successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error liking post", e);
                });
    }

    public Task<Void> unlikePost(String postId, String userId) {
        return postsCollection
                .document(postId)
                .update("likes", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post unliked successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error unliking post", e);
                });
    }

    public Task<Void> addComment(String postId, Post.Comment comment) {
        return postsCollection
                .document(postId)
                .update("comments", com.google.firebase.firestore.FieldValue.arrayUnion(comment))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Comment added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comment", e);
                });
    }

    public Task<Void> removeComment(String postId, Post.Comment comment) {
        return postsCollection
                .document(postId)
                .update("comments", com.google.firebase.firestore.FieldValue.arrayRemove(comment))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Comment removed successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing comment", e);
                });
    }
} 