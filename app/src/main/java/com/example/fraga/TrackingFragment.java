package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TrackingFragment extends Fragment {

    private static final String TAG = "TrackingFragment";
    private RadioGroup radioGroupActivityType;
    private RadioButton radioButtonRunning, radioButtonCycling, radioButtonSwimming;
    private TextView textViewDistance, textViewDuration, textViewPace;
    private Button buttonStartTracking;
    private Button buttonCreateActivity;
    private ImageView imageViewMap;
    private boolean isTracking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialize UI components
            radioGroupActivityType = view.findViewById(R.id.radioGroupActivityType);
            radioButtonRunning = view.findViewById(R.id.radioButtonRunning);
            radioButtonCycling = view.findViewById(R.id.radioButtonCycling);
            radioButtonSwimming = view.findViewById(R.id.radioButtonSwimming);
            
            textViewDistance = view.findViewById(R.id.textViewDistance);
            textViewDuration = view.findViewById(R.id.textViewDuration);
            textViewPace = view.findViewById(R.id.textViewPace);
            
            buttonStartTracking = view.findViewById(R.id.buttonStartTracking);
            buttonCreateActivity = view.findViewById(R.id.buttonCreateActivity);
            imageViewMap = view.findViewById(R.id.imageViewMap);
            
            // Set up listeners
            if (buttonStartTracking != null) {
                buttonStartTracking.setOnClickListener(v -> toggleTracking());
            } else {
                Log.e(TAG, "buttonStartTracking not found!");
            }
            
            if (buttonCreateActivity != null) {
                buttonCreateActivity.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Planning a new activity...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(getActivity(), CreateActivityActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting CreateActivityActivity: " + e.getMessage());
                        Toast.makeText(getContext(), "Could not open activity creation page", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "buttonCreateActivity not found!");
            }
            
            if (radioGroupActivityType != null) {
                radioGroupActivityType.setOnCheckedChangeListener((group, checkedId) -> {
                    // Reset stats when changing activity type
                    resetStats();
                });
            } else {
                Log.e(TAG, "radioGroupActivityType not found!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI: " + e.getMessage());
        }
    }
    
    private void toggleTracking() {
        isTracking = !isTracking;
        
        if (isTracking) {
            buttonStartTracking.setText(R.string.stop_tracking);
            buttonStartTracking.setBackgroundColor(getResources().getColor(R.color.error, null));
            // Start tracking logic would go here
        } else {
            buttonStartTracking.setText(R.string.start_tracking);
            buttonStartTracking.setBackgroundColor(getResources().getColor(R.color.success, null));
            // Stop tracking logic would go here
        }
    }
    
    private void resetStats() {
        if (textViewDistance != null) textViewDistance.setText("0.00 km");
        if (textViewDuration != null) textViewDuration.setText("00:00:00");
        if (textViewPace != null) textViewPace.setText("0:00 /km");
    }
} 