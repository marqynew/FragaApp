package com.example.fraga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.fraga.R;
import com.example.fraga.repositories.SocialRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public class ProfileFragment extends Fragment {
    private View rootView;
    private TextView tvFollowersCount;
    private TextView tvFollowingCount;
    private SocialRepository socialRepository;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialRepository = new SocialRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        tvFollowersCount = rootView.findViewById(R.id.tvFollowersCount);
        tvFollowingCount = rootView.findViewById(R.id.tvFollowingCount);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAuthStateListener();
        loadSocialStats();
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth;
            if (currentUser != null) {
                loadSocialStats();
            }
        };
    }

    private void loadSocialStats() {
        if (currentUser == null) return;

        socialRepository.getFollowers(currentUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> followers = (List<String>) documentSnapshot.get("users");
                        if (followers != null) {
                            tvFollowersCount.setText(String.valueOf(followers.size()));
                        }
                    }
                });

        socialRepository.getFollowing(currentUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> following = (List<String>) documentSnapshot.get("users");
                        if (following != null) {
                            tvFollowingCount.setText(String.valueOf(following.size()));
                        }
                    }
                });
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
        tvFollowersCount = null;
        tvFollowingCount = null;
    }
} 