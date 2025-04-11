package com.example.fraga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LeaderboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        
        TextView textViewTitle = view.findViewById(R.id.textViewPlaceholder);
        textViewTitle.setText("Leaderboard\n\nSee how you stack up against friends and the community. Check weekly, monthly, and all-time rankings.");
        
        return view;
    }
} 