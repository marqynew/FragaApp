package com.example.fraga;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView textViewProfileName;
    private TextView textViewProfileLocation;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialize UI components
            textViewProfileName = view.findViewById(R.id.textViewProfileName);
            textViewProfileLocation = view.findViewById(R.id.textViewProfileLocation);
            textViewFollowersCount = view.findViewById(R.id.textViewFollowersCount);
            textViewFollowingCount = view.findViewById(R.id.textViewFollowingCount);
            textViewTotalDistance = view.findViewById(R.id.textViewTotalDistance);
            textViewTotalActivities = view.findViewById(R.id.textViewTotalActivities);
            textViewTotalKudos = view.findViewById(R.id.textViewTotalKudos);
            recyclerViewRecentActivities = view.findViewById(R.id.recyclerViewRecentActivities);
            buttonViewAllActivities = view.findViewById(R.id.buttonViewAllActivities);
            buttonSettings = view.findViewById(R.id.buttonSettings);
            buttonChallenges = view.findViewById(R.id.buttonChallenges);
            buttonSocial = view.findViewById(R.id.buttonSocial);
            imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
            
            // Set up mock data
            setupUserProfile();
            setupUserActivities();
            
            // Set up button click listeners
            if (buttonViewAllActivities != null) {
                buttonViewAllActivities.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Viewing all activities", Toast.LENGTH_SHORT).show();
                });
            } else {
                Log.e(TAG, "buttonViewAllActivities not found!");
            }
            
            // Navigation to other screens
            if (buttonSettings != null) {
                buttonSettings.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching SettingsActivity: " + e.getMessage());
                        Toast.makeText(getContext(), "Could not open settings", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "buttonSettings not found!");
            }
            
            if (buttonChallenges != null) {
                buttonChallenges.setOnClickListener(v -> {
//                    try {
//                        Intent intent = new Intent(getActivity(), ChallengesActivity.class);
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error launching ChallengesActivity: " + e.getMessage());
//                        Toast.makeText(getContext(), "Could not open challenges", Toast.LENGTH_SHORT).show();
//                    }
                    Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                       startActivity(intent);
                });
            } else {
                Log.e(TAG, "buttonChallenges not found!");
            }
            
            if (buttonSocial != null) {
                buttonSocial.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), SocialActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching SocialActivity: " + e.getMessage());
                        Toast.makeText(getContext(), "Could not open social", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                Log.e(TAG, "buttonSocial not found!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI: " + e.getMessage());
        }
    }
    
    private void setupUserProfile() {
        try {
            // In a real app, this would come from a database or API
            if (textViewProfileName != null) textViewProfileName.setText("Ammar Qurthuby");
            if (textViewProfileLocation != null) textViewProfileLocation.setText("Banda Aceh");
            if (textViewFollowersCount != null) textViewFollowersCount.setText("256");
            if (textViewFollowingCount != null) textViewFollowingCount.setText("124");
            if (textViewTotalDistance != null) textViewTotalDistance.setText("254.6 km");
            if (textViewTotalActivities != null) textViewTotalActivities.setText("43");
            if (textViewTotalKudos != null) textViewTotalKudos.setText("328");
            
            // Profile image is set from layout XML via android:src="@drawable/profile_image"
            // For dynamic loading, you could use something like Glide:
        } catch (Exception e) {
            Log.e(TAG, "Error setting up user profile: " + e.getMessage());
        }
    }
    
    private void setupUserActivities() {
        try {
            // Set up recycler view for recent activities
            if (recyclerViewRecentActivities != null) {
                recyclerViewRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
                
                // Sample data for recent activities
                List<FeedFragment.ActivityItem> recentActivities = new ArrayList<>();
                
                // Create sample activities
                recentActivities.add(new FeedFragment.ActivityItem(
                        "Ammar Qurthuby",
                        "Today at 6:30 AM",
                        "Morning Run",
                        "Great start to the day!",
                        "5.2 km",
                        "25:15",
                        "4:51 /km",
                        12));
                        
                recentActivities.add(new FeedFragment.ActivityItem(
                        "Ammar Qurthuby",
                        "Yesterday at 5:45 PM",
                        "Evening Cycle",
                        "Quick ride after work",
                        "10.5 km",
                        "33:20",
                        "18.9 km/h",
                        8));
                        
                recentActivities.add(new FeedFragment.ActivityItem(
                        "Ammar Qurthuby",
                        "Monday at 7:15 AM",
                        "Morning Run",
                        "Ran through the park",
                        "6.3 km",
                        "31:45",
                        "5:02 /km",
                        15));
                
                // Create adapter for recent activities
                RecentActivityAdapter adapter = new RecentActivityAdapter(recentActivities);
                recyclerViewRecentActivities.setAdapter(adapter);
            } else {
                Log.e(TAG, "recyclerViewRecentActivities not found!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up activities: " + e.getMessage());
        }
    }
    
    // Adapter for recent activities (simplified version of the feed adapter)
    class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {
        
        private List<FeedFragment.ActivityItem> activityItems;
        
        RecentActivityAdapter(List<FeedFragment.ActivityItem> activityItems) {
            this.activityItems = activityItems;
        }
        
        @NonNull
        @Override
        public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity, parent, false);
            return new ActivityViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
            try {
                FeedFragment.ActivityItem activityItem = activityItems.get(position);
                
                holder.textViewActivityTitle.setText(activityItem.title);
                holder.textViewActivityTime.setText(activityItem.time);
                holder.textViewActivityDescription.setText(activityItem.description);
                holder.textViewActivityDistance.setText(activityItem.distance);
                holder.textViewActivityDuration.setText(activityItem.duration);
                holder.textViewActivityPace.setText(activityItem.pace);
                holder.textViewKudosCount.setText(activityItem.kudosCount + " kudos");
            } catch (Exception e) {
                Log.e(TAG, "Error binding view holder: " + e.getMessage());
            }
        }
        
        @Override
        public int getItemCount() {
            return activityItems.size();
        }
        
        class ActivityViewHolder extends RecyclerView.ViewHolder {
            TextView textViewActivityTitle;
            TextView textViewActivityTime;
            TextView textViewActivityDescription;
            TextView textViewActivityDistance;
            TextView textViewActivityDuration;
            TextView textViewActivityPace;
            TextView textViewKudosCount;
            
            ActivityViewHolder(@NonNull View itemView) {
                super(itemView);
                
                try {
                    textViewActivityTitle = itemView.findViewById(R.id.textViewActivityTitle);
                    textViewActivityTime = itemView.findViewById(R.id.textViewActivityTime);
                    textViewActivityDescription = itemView.findViewById(R.id.textViewActivityDescription);
                    textViewActivityDistance = itemView.findViewById(R.id.textViewActivityDistance);
                    textViewActivityDuration = itemView.findViewById(R.id.textViewActivityDuration);
                    textViewActivityPace = itemView.findViewById(R.id.textViewActivityPace);
                    textViewKudosCount = itemView.findViewById(R.id.textViewKudosCount);
                } catch (Exception e) {
                    Log.e(TAG, "Error initializing ViewHolder: " + e.getMessage());
                }
            }
        }
    }
} 