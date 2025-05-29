package com.example.fraga;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.UserProfileRepository;
import com.example.fraga.repositories.SocialRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private View rootView;
    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewBio;
    private TextView textViewFollowersCount;
    private TextView textViewFollowingCount;
    private TextView textViewTotalDistance;
    private TextView textViewTotalActivities;
    private TextView textViewTotalKudos;
    private RecyclerView recyclerViewRecentActivities;
    private Button buttonViewAllActivities;
    private Button buttonSettings;
    private Button buttonChallenges;
    private Button buttonSocial;
    private ImageView imageViewProfilePic;
    private FloatingActionButton fabEditProfile;

    private FirebaseAuth mAuth;
    private UserProfileRepository profileRepository;
    private SocialRepository socialRepository;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialRepository = new SocialRepository();
        mAuth = FirebaseAuth.getInstance();
        profileRepository = new UserProfileRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Check internet connection
            if (!isNetworkAvailable()) {
                showError("No internet connection. Please check your connection and try again.");
                return;
            }
            
            // Set up button click listeners
            setupButtonListeners();
            
            // Set up recent activities
            setupUserActivities();
            
            setupAuthStateListener();
            
            // Load user profile
            loadUserProfile();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI: " + e.getMessage());
            showError("Error initializing profile");
        }
    }

    private void initializeViews(View view) {
        textViewName = view.findViewById(R.id.textViewProfileName);
        textViewLocation = view.findViewById(R.id.textViewProfileLocation);
        textViewBio = view.findViewById(R.id.textViewBio);
        textViewFollowersCount = view.findViewById(R.id.tvFollowersCount);
        textViewFollowingCount = view.findViewById(R.id.tvFollowingCount);
            textViewTotalDistance = view.findViewById(R.id.textViewTotalDistance);
            textViewTotalActivities = view.findViewById(R.id.textViewTotalActivities);
            textViewTotalKudos = view.findViewById(R.id.textViewTotalKudos);
            recyclerViewRecentActivities = view.findViewById(R.id.recyclerViewRecentActivities);
            buttonViewAllActivities = view.findViewById(R.id.buttonViewAllActivities);
            buttonSettings = view.findViewById(R.id.buttonSettings);
            buttonChallenges = view.findViewById(R.id.buttonChallenges);
            buttonSocial = view.findViewById(R.id.buttonSocial);
            imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
        fabEditProfile = view.findViewById(R.id.fabEditProfile);
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) {
            showError("User not logged in");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        profileRepository.getProfile(userId, task -> {
            if (task.isSuccessful()) {
                UserProfile profile = task.getResult();
                if (profile != null) {
                    if (textViewName != null) textViewName.setText(profile.getFullName());
                    if (textViewLocation != null) textViewLocation.setText(profile.getLocation());
                    if (textViewBio != null) textViewBio.setText(profile.getBio());
                    
                    // Load followers and following counts
                    loadSocialStats(userId);
                    
                    if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty() && imageViewProfilePic != null) {
                        Glide.with(this)
                            .load(profile.getProfileImageUrl())
                            .circleCrop()
                            .into(imageViewProfilePic);
                    }
                }
            } else {
                showError("Error loading profile");
            }
        });
    }

    private void loadSocialStats(String userId) {
        if (textViewFollowersCount == null || textViewFollowingCount == null) return;

        socialRepository.getFollowers(userId)
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> followers = (List<String>) documentSnapshot.get("users");
                    if (followers != null && textViewFollowersCount != null) {
                        textViewFollowersCount.setText(String.valueOf(followers.size()));
                    }
                }
            });

        socialRepository.getFollowing(userId)
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> following = (List<String>) documentSnapshot.get("users");
                    if (following != null && textViewFollowingCount != null) {
                        textViewFollowingCount.setText(String.valueOf(following.size()));
                    }
                }
            });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Log.e(TAG, "Error: " + message);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        if (getContext() == null) return false;
        
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void setupButtonListeners() {
        try {
            if (buttonViewAllActivities != null) {
                buttonViewAllActivities.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Viewing all activities", Toast.LENGTH_SHORT).show();
                });
            }
            
            if (buttonSettings != null) {
                buttonSettings.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching SettingsActivity: " + e.getMessage());
                        showError("Could not open settings");
                    }
                });
            }
            
            if (buttonChallenges != null) {
                buttonChallenges.setOnClickListener(v -> {
                    try {
                    Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                       startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching ChallengesActivity: " + e.getMessage());
                        showError("Could not open challenges");
                    }
                });
            }
            
            if (buttonSocial != null) {
                buttonSocial.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), SocialActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching SocialActivity: " + e.getMessage());
                        showError("Could not open social");
                    }
                });
                    }

            if (fabEditProfile != null) {
                fabEditProfile.setOnClickListener(v -> {
                    try {
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                            startActivity(intent);
            } else {
                            Log.e(TAG, "Activity is null");
                            showError("Could not open edit profile");
            }
        } catch (Exception e) {
                        Log.e(TAG, "Error launching EditProfileActivity: " + e.getMessage());
                        showError("Could not open edit profile: " + e.getMessage());
                    }
                });
            }

            // Add click listeners for followers/following counts
            if (textViewFollowersCount != null) {
                textViewFollowersCount.setOnClickListener(v -> {
                    if (getActivity() != null && mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(getActivity(), SocialListActivity.class);
                        intent.putExtra(SocialListActivity.EXTRA_USER_ID, mAuth.getCurrentUser().getUid());
                        intent.putExtra(SocialListActivity.EXTRA_LIST_TYPE, SocialListActivity.TYPE_FOLLOWERS);
                        startActivity(intent);
                    }
                });
            }

            if (textViewFollowingCount != null) {
                textViewFollowingCount.setOnClickListener(v -> {
                    if (getActivity() != null && mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(getActivity(), SocialListActivity.class);
                        intent.putExtra(SocialListActivity.EXTRA_USER_ID, mAuth.getCurrentUser().getUid());
                        intent.putExtra(SocialListActivity.EXTRA_LIST_TYPE, SocialListActivity.TYPE_FOLLOWING);
                        startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up button listeners: " + e.getMessage());
        }
    }
    
    private void setupUserActivities() {
        // TODO: Implement recent activities
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth;
            if (currentUser != null) {
                loadUserProfile();
        }
        };
    }

        @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        }
        
        @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
            }
        }
        
        @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        textViewName = null;
        textViewLocation = null;
        textViewBio = null;
        textViewFollowersCount = null;
        textViewFollowingCount = null;
        textViewTotalDistance = null;
        textViewTotalActivities = null;
        textViewTotalKudos = null;
        recyclerViewRecentActivities = null;
        buttonViewAllActivities = null;
        buttonSettings = null;
        buttonChallenges = null;
        buttonSocial = null;
        imageViewProfilePic = null;
        fabEditProfile = null;
    }
} 