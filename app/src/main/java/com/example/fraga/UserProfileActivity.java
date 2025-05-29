package com.example.fraga;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fraga.adapters.PostAdapter;
import com.example.fraga.models.Post;
import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.PostRepository;
import com.example.fraga.repositories.SocialRepository;
import com.example.fraga.repositories.UserProfileRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";

    private String targetUserId;
    private String currentUserId;
    private UserProfile userProfile;
    private SocialRepository socialRepository;
    private UserProfileRepository userProfileRepository;
    private PostRepository postRepository;

    private ImageView profileImage;
    private TextView usernameText;
    private TextView bioText;
    private TextView followersCount;
    private TextView followingCount;
    private Button followButton;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize repositories
        socialRepository = new SocialRepository();
        userProfileRepository = new UserProfileRepository();
        postRepository = new PostRepository();

        // Get user IDs
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        targetUserId = getIntent().getStringExtra("userId");
        if (targetUserId == null) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        // Initialize views
        initializeViews();
        setupRecyclerView();

        // Load user profile
        loadUserProfile();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        bioText = findViewById(R.id.bioText);
        followersCount = findViewById(R.id.followersCount);
        followingCount = findViewById(R.id.followingCount);
        followButton = findViewById(R.id.followButton);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);

        followButton.setOnClickListener(v -> toggleFollow());
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(new ArrayList<>());
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);
    }

    private void loadUserProfile() {
        userProfileRepository.getProfile(targetUserId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        userProfile = documentSnapshot.toObject(UserProfile.class);
                        if (userProfile != null) {
                            updateUI(userProfile);
                            checkFollowStatus();
                            loadSocialStats();
                            loadUserPosts();
                        } else {
                            Toast.makeText(this, "Error parsing user profile", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateUI(UserProfile userProfile) {
        usernameText.setText(userProfile.getFullName());
        bioText.setText(userProfile.getBio());
        
        if (userProfile.getProfileImageUrl() != null && !userProfile.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(userProfile.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.default_profile);
        }

        followersCount.setText(String.valueOf(userProfile.getSocial().getFollowers().size()));
        followingCount.setText(String.valueOf(userProfile.getSocial().getFollowing().size()));

        // Update follow button state
        boolean isFollowing = userProfile.getSocial().getFollowers().contains(currentUserId);
        updateFollowButton(isFollowing);
    }

    private void checkFollowStatus() {
        if (currentUserId.equals(targetUserId)) {
            followButton.setVisibility(View.GONE);
        } else {
            socialRepository.isFollowing(currentUserId, targetUserId)
                    .addOnSuccessListener(isFollowing -> {
                        updateFollowButton(isFollowing);
                    });
        }
    }

    private void loadSocialStats() {
        // Load followers count
        socialRepository.getFollowersList(targetUserId)
                .addOnSuccessListener(followers -> {
                    followersCount.setText(String.valueOf(followers.size()));
                });

        // Load following count
        socialRepository.getFollowingList(targetUserId)
                .addOnSuccessListener(following -> {
                    followingCount.setText(String.valueOf(following.size()));
                });
    }

    private void loadUserPosts() {
        postRepository.getUserPosts(targetUserId)
                .addOnSuccessListener(posts -> {
                    postAdapter.updatePosts(posts);
                });
    }

    private void updateFollowButton(boolean isFollowing) {
        followButton.setText(isFollowing ? "Unfollow" : "Follow");
        followButton.setEnabled(true);
    }

    private void toggleFollow() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please log in to follow users", Toast.LENGTH_SHORT).show();
            return;
        }

        followButton.setEnabled(false);
        if (followButton.getText().toString().equals("Follow")) {
            socialRepository.followUser(currentUserId, targetUserId)
                    .addOnSuccessListener(aVoid -> {
                        updateFollowButton(true);
                        loadSocialStats();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        followButton.setEnabled(true);
                    });
        } else {
            socialRepository.unfollowUser(currentUserId, targetUserId)
                    .addOnSuccessListener(aVoid -> {
                        updateFollowButton(false);
                        loadSocialStats();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        followButton.setEnabled(true);
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 