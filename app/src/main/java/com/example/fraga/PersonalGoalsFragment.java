package com.example.fraga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PersonalGoalsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize components and set listeners
        setupCurrentGoals(view);
        setupCompletedGoals(view);
        setupGoalTemplates(view);
    }
    
    private void setupCurrentGoals(View view) {
        // Setup Goal 1
        MaterialCardView cardViewGoal1 = view.findViewById(R.id.cardViewGoal1);
        MaterialButton buttonEditGoal1 = view.findViewById(R.id.buttonEditGoal1);
        MaterialButton buttonCompleteGoal1 = view.findViewById(R.id.buttonCompleteGoal1);
        
        cardViewGoal1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Goal details coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        buttonEditGoal1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit goal coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        buttonCompleteGoal1.setOnClickListener(v -> {
            // Simulate logging a run and updating progress
            Toast.makeText(getContext(), "Run logged successfully!", Toast.LENGTH_SHORT).show();
        });
        
        // Setup Goal 2
        MaterialCardView cardViewGoal2 = view.findViewById(R.id.cardViewGoal2);
        MaterialButton buttonEditGoal2 = view.findViewById(R.id.buttonEditGoal2);
        MaterialButton buttonCompleteGoal2 = view.findViewById(R.id.buttonCompleteGoal2);
        
        cardViewGoal2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Goal details coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        buttonEditGoal2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit goal coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        buttonCompleteGoal2.setOnClickListener(v -> {
            // Simulate syncing steps from device
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sync Steps")
                .setMessage("Connecting to health data provider...")
                .setPositiveButton("OK", (dialog, which) -> {
                    Toast.makeText(getContext(), "Steps synced successfully!", Toast.LENGTH_SHORT).show();
                })
                .show();
        });
    }
    
    private void setupCompletedGoals(View view) {
        MaterialCardView cardViewCompletedGoal = view.findViewById(R.id.cardViewCompletedGoal);
        MaterialButton buttonShareCompletedGoal = view.findViewById(R.id.buttonShareCompletedGoal);
        
        cardViewCompletedGoal.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Completed goal details coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        buttonShareCompletedGoal.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sharing functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupGoalTemplates(View view) {
        // Template 1 - Marathon Training
        MaterialCardView cardViewTemplate1 = view.findViewById(R.id.cardViewTemplate1);
        cardViewTemplate1.setOnClickListener(v -> {
            showTemplateConfirmation("Run a Marathon", 
                    "This will set up a 16-week marathon training plan. Ready to start?");
        });
        
        // Template 2 - Cycling Challenge
        MaterialCardView cardViewTemplate2 = view.findViewById(R.id.cardViewTemplate2);
        cardViewTemplate2.setOnClickListener(v -> {
            showTemplateConfirmation("Cycle 1000km", 
                    "This will set up a monthly 1000km cycling challenge. Ready to start?");
        });
        
        // Template 3 - 30-Day Streak
        MaterialCardView cardViewTemplate3 = view.findViewById(R.id.cardViewTemplate3);
        cardViewTemplate3.setOnClickListener(v -> {
            showTemplateConfirmation("30-Day Streak", 
                    "This will set up a 30-day workout streak challenge. Ready to start?");
        });
        
        // Template 4 - Improve 5K Time
        MaterialCardView cardViewTemplate4 = view.findViewById(R.id.cardViewTemplate4);
        cardViewTemplate4.setOnClickListener(v -> {
            showTemplateConfirmation("Improve 5K Time", 
                    "This will set up an 8-week plan to improve your 5K time. Ready to start?");
        });
    }
    
    private void showTemplateConfirmation(String title, String message) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Start", (dialog, which) -> {
                Toast.makeText(getContext(), "Goal template applied: " + title, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 