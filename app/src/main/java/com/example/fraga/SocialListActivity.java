package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraga.adapters.UserAdapter;
import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.SocialRepository;
import com.example.fraga.repositories.UserProfileRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SocialListActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {
    private static final String TAG = "SocialListActivity";
    public static final String EXTRA_LIST_TYPE = "list_type";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String TYPE_FOLLOWERS = "followers";
    public static final String TYPE_FOLLOWING = "following";

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private UserAdapter adapter;
    private SocialRepository socialRepository;
    private UserProfileRepository userProfileRepository;
    private String currentUserId;
    private String listType;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_list);

        // Initialize repositories
        socialRepository = new SocialRepository();
        userProfileRepository = new UserProfileRepository();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get intent extras
        listType = getIntent().getStringExtra(EXTRA_LIST_TYPE);
        targetUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (targetUserId == null) {
            targetUserId = currentUserId;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(listType.equals(TYPE_FOLLOWERS) ? "Followers" : "Following");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewUsers);
        editTextSearch = findViewById(R.id.editTextSearch);

        // Setup RecyclerView
        adapter = new UserAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup search
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load data based on list type
        if (TYPE_FOLLOWERS.equals(listType)) {
            loadFollowers();
        } else if (TYPE_FOLLOWING.equals(listType)) {
            loadFollowing();
        }
    }

    private void loadFollowers() {
        socialRepository.getFollowersList(targetUserId)
                .addOnSuccessListener(userIds -> {
                    if (userIds != null && !userIds.isEmpty()) {
                        loadUserProfiles(userIds);
                    } else {
                        showEmptyList();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading followers", e);
                    showError("Failed to load followers");
                });
    }

    private void loadFollowing() {
        socialRepository.getFollowingList(targetUserId)
                .addOnSuccessListener(userIds -> {
                    if (userIds != null && !userIds.isEmpty()) {
                        loadUserProfiles(userIds);
                    } else {
                        showEmptyList();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading following", e);
                    showError("Failed to load following");
                });
    }

    private void loadUserProfiles(List<String> userIds) {
        userProfileRepository.getUserProfiles(userIds)
                .addOnSuccessListener(querySnapshot -> {
                    List<UserProfile> profiles = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            UserProfile profile = doc.toObject(UserProfile.class);
                            if (profile != null) {
                                profiles.add(profile);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error converting document to UserProfile", e);
                            // Skip this document and continue with others
                        }
                    }
                    updateUI(profiles);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user profiles", e);
                    showError("Failed to load user profiles");
                });
    }

    private void updateUI(List<UserProfile> profiles) {
        if (profiles.isEmpty()) {
            showEmptyList();
        } else {
            adapter.updateUsers(profiles);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyList() {
        recyclerView.setVisibility(View.GONE);
        // You might want to show a TextView with "No users found" message
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void filterUsers(String query) {
        List<UserProfile> filteredList = new ArrayList<>();
        for (UserProfile user : adapter.getUsers()) {
            if (user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateUsers(filteredList);
    }

    @Override
    public void onFollowClick(UserProfile user, boolean isFollowing) {
        if (isFollowing) {
            socialRepository.unfollowUser(currentUserId, user.getUserId())
                .addOnSuccessListener(aVoid -> {
                    user.setFollowing(false);
                    adapter.updateUser(user);
                    Toast.makeText(this, "Unfollowed " + user.getEmail(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error unfollowing user", Toast.LENGTH_SHORT).show();
                });
        } else {
            socialRepository.followUser(currentUserId, user.getUserId())
                .addOnSuccessListener(aVoid -> {
                    user.setFollowing(true);
                    adapter.updateUser(user);
                    Toast.makeText(this, "Followed " + user.getEmail(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error following user", Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public void onUserClick(UserProfile user) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
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