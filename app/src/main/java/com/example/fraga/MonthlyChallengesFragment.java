package com.example.fraga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MonthlyChallengesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize components and set listeners
        setupFeaturedChallenge(view);
        setupChallengeCards(view);
    }
    
    private void setupFeaturedChallenge(View view) {
        MaterialCardView cardViewFeaturedChallenge = view.findViewById(R.id.cardViewFeaturedChallenge);
        MaterialButton buttonJoinFeatured = view.findViewById(R.id.buttonJoinFeatured);
        
        // Set click listener for featured challenge card
        cardViewFeaturedChallenge.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Viewing August Distance Challenge", Toast.LENGTH_SHORT).show();
            // Future: navigate to challenge detail
        });
        
        // Set click listener for featured challenge button
        buttonJoinFeatured.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Challenge detail coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupChallengeCards(View view) {
        // Setup Challenge 1
        MaterialCardView cardViewChallenge1 = view.findViewById(R.id.cardViewChallenge1);
        MaterialButton buttonJoinChallenge1 = view.findViewById(R.id.buttonJoinChallenge1);
        
        cardViewChallenge1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Viewing 5K Challenge", Toast.LENGTH_SHORT).show();
        });
        
        buttonJoinChallenge1.setOnClickListener(v -> {
            buttonJoinChallenge1.setText("Joined");
            buttonJoinChallenge1.setEnabled(false);
            Toast.makeText(getContext(), "Joined 5K Challenge!", Toast.LENGTH_SHORT).show();
        });
        
        // Setup Challenge 2
        MaterialCardView cardViewChallenge2 = view.findViewById(R.id.cardViewChallenge2);
        MaterialButton buttonJoinChallenge2 = view.findViewById(R.id.buttonJoinChallenge2);
        
        cardViewChallenge2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Viewing Streak Challenge", Toast.LENGTH_SHORT).show();
        });
        
        buttonJoinChallenge2.setOnClickListener(v -> {
            buttonJoinChallenge2.setText("Joined");
            buttonJoinChallenge2.setEnabled(false);
            Toast.makeText(getContext(), "Joined 15-Day Streak Challenge!", Toast.LENGTH_SHORT).show();
        });
        
        // Setup Challenge 3
        MaterialCardView cardViewChallenge3 = view.findViewById(R.id.cardViewChallenge3);
        MaterialButton buttonJoinChallenge3 = view.findViewById(R.id.buttonJoinChallenge3);
        
        cardViewChallenge3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Viewing Cycling Challenge", Toast.LENGTH_SHORT).show();
        });
        
        buttonJoinChallenge3.setOnClickListener(v -> {
            buttonJoinChallenge3.setText("Joined");
            buttonJoinChallenge3.setEnabled(false);
            Toast.makeText(getContext(), "Joined Cycling Challenge!", Toast.LENGTH_SHORT).show();
        });
    }
} 