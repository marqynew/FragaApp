package com.example.fraga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class FriendsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Setup friend requests
        setupFriendRequests(view);
        
        // Setup friends list
        setupFriendsList(view);
        
        // Setup groups
        setupGroups(view);
    }
    
    private void setupFriendRequests(View view) {
        // Request 1
        MaterialButton buttonAccept1 = view.findViewById(R.id.buttonAcceptRequest1);
        MaterialButton buttonDecline1 = view.findViewById(R.id.buttonDeclineRequest1);
        
        buttonAccept1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
            // Hide the request or refresh the list in a real app
            buttonAccept1.setEnabled(false);
            buttonDecline1.setEnabled(false);
            buttonAccept1.setText("Accepted");
        });
        
        buttonDecline1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            // Hide the request or refresh the list in a real app
            buttonAccept1.setEnabled(false);
            buttonDecline1.setEnabled(false);
            buttonDecline1.setText("Declined");
        });
        
        // Request 2
        MaterialButton buttonAccept2 = view.findViewById(R.id.buttonAcceptRequest2);
        MaterialButton buttonDecline2 = view.findViewById(R.id.buttonDeclineRequest2);
        
        buttonAccept2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
            // Hide the request or refresh the list in a real app
            buttonAccept2.setEnabled(false);
            buttonDecline2.setEnabled(false);
            buttonAccept2.setText("Accepted");
        });
        
        buttonDecline2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            // Hide the request or refresh the list in a real app
            buttonAccept2.setEnabled(false);
            buttonDecline2.setEnabled(false);
            buttonDecline2.setText("Declined");
        });
    }
    
    private void setupFriendsList(View view) {
        // Friend 1
        CardView cardViewFriend1 = view.findViewById(R.id.cardViewFriend1);
        cardViewFriend1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Alex's profile", Toast.LENGTH_SHORT).show();
        });
        
        // Friend 2
        CardView cardViewFriend2 = view.findViewById(R.id.cardViewFriend2);
        cardViewFriend2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Emily's profile", Toast.LENGTH_SHORT).show();
        });
        
        // Friend 3
        CardView cardViewFriend3 = view.findViewById(R.id.cardViewFriend3);
        cardViewFriend3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View James's profile", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupGroups(View view) {
        // Group 1
        MaterialCardView cardViewGroup1 = view.findViewById(R.id.cardViewGroup1);
        cardViewGroup1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Morning Runners group details", Toast.LENGTH_SHORT).show();
        });
        
        // Group 2
        MaterialCardView cardViewGroup2 = view.findViewById(R.id.cardViewGroup2);
        cardViewGroup2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Weekend Warriors group details", Toast.LENGTH_SHORT).show();
        });
        
        // Group 3
        MaterialCardView cardViewGroup3 = view.findViewById(R.id.cardViewGroup3);
        cardViewGroup3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "City Cyclists group details", Toast.LENGTH_SHORT).show();
        });
    }
} 