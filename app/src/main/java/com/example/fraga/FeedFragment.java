package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerViewActivityFeed;
    private ActivityAdapter activityAdapter;
    private FloatingActionButton fabCreateActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerViewActivityFeed = view.findViewById(R.id.recyclerViewActivityFeed);
        recyclerViewActivityFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Create and set adapter with sample data
        activityAdapter = new ActivityAdapter(generateSampleActivities());
        recyclerViewActivityFeed.setAdapter(activityAdapter);

        // Initialize FAB
        fabCreateActivity = view.findViewById(R.id.fabCreateActivity);
        fabCreateActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateActivityActivity.class);
            startActivity(intent);
        });
    }
    
    private List<ActivityItem> generateSampleActivities() {
        List<ActivityItem> activities = new ArrayList<>();
        
        // Add sample activities
        activities.add(new ActivityItem(
                "Jane Smith",
                "Today at 8:45 AM",
                "Morning Run",
                "Started the day with a refreshing run around the lake!",
                "5.2 km",
                "28:15",
                "5:26 /km",
                18));
                
        activities.add(new ActivityItem(
                "Mike Johnson",
                "Yesterday at 6:30 PM",
                "Evening Ride",
                "Great bike ride through the mountain trails!",
                "15.7 km",
                "45:22",
                "17.4 km/h",
                42));
                
        activities.add(new ActivityItem(
                "Sarah Williams",
                "Yesterday at 9:15 AM",
                "Pool Swim",
                "Did some laps at the community pool. Feeling refreshed!",
                "1.5 km",
                "35:10",
                "2:20 /100m",
                15));
                
        activities.add(new ActivityItem(
                "David Chen",
                "Monday at 5:45 PM",
                "Trail Run",
                "Explored some new trails in the forest. Beautiful scenery!",
                "8.3 km",
                "48:33",
                "5:51 /km",
                31));
                
        activities.add(new ActivityItem(
                "Emma Wilson",
                "Monday at 7:20 AM",
                "Morning Cycle",
                "Commuted to work on my bike. Great way to start the day!",
                "12.5 km",
                "38:45",
                "19.3 km/h",
                27));
        
        return activities;
    }
    
    // Model class for activity items
    static class ActivityItem {
        String username;
        String time;
        String title;
        String description;
        String distance;
        String duration;
        String pace;
        int kudosCount;
        
        ActivityItem(String username, String time, String title, String description, 
                     String distance, String duration, String pace, int kudosCount) {
            this.username = username;
            this.time = time;
            this.title = title;
            this.description = description;
            this.distance = distance;
            this.duration = duration;
            this.pace = pace;
            this.kudosCount = kudosCount;
        }
    }
    
    // Adapter for the activity feed
    class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
        
        private List<ActivityItem> activityItems;
        
        ActivityAdapter(List<ActivityItem> activityItems) {
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
            ActivityItem activityItem = activityItems.get(position);
            
            holder.textViewUsername.setText(activityItem.username);
            holder.textViewActivityTime.setText(activityItem.time);
            holder.textViewActivityTitle.setText(activityItem.title);
            holder.textViewActivityDescription.setText(activityItem.description);
            holder.textViewActivityDistance.setText(activityItem.distance);
            holder.textViewActivityDuration.setText(activityItem.duration);
            holder.textViewActivityPace.setText(activityItem.pace);
            holder.textViewKudosCount.setText(activityItem.kudosCount + " kudos");
            
            // Set up like button click listener
            holder.buttonKudos.setOnClickListener(v -> {
                // In a real app, this would send a kudos to the server
                activityItem.kudosCount++;
                holder.textViewKudosCount.setText(activityItem.kudosCount + " kudos");
            });
            
            // Set up item click listener to open activity details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
                // In a real app, we would pass activity ID or data
                // intent.putExtra("activity_id", activityItem.id);
                startActivity(intent);
            });
        }
        
        @Override
        public int getItemCount() {
            return activityItems.size();
        }
        
        class ActivityViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewUserAvatar;
            ImageView imageViewActivityMap;
            TextView textViewUsername;
            TextView textViewActivityTime;
            TextView textViewActivityTitle;
            TextView textViewActivityDescription;
            TextView textViewActivityDistance;
            TextView textViewActivityDuration;
            TextView textViewActivityPace;
            TextView textViewKudosCount;
            Button buttonKudos;
            
            ActivityViewHolder(@NonNull View itemView) {
                super(itemView);
                
                imageViewUserAvatar = itemView.findViewById(R.id.imageViewUserAvatar);
                imageViewActivityMap = itemView.findViewById(R.id.imageViewActivityMap);
                textViewUsername = itemView.findViewById(R.id.textViewUsername);
                textViewActivityTime = itemView.findViewById(R.id.textViewActivityTime);
                textViewActivityTitle = itemView.findViewById(R.id.textViewActivityTitle);
                textViewActivityDescription = itemView.findViewById(R.id.textViewActivityDescription);
                textViewActivityDistance = itemView.findViewById(R.id.textViewActivityDistance);
                textViewActivityDuration = itemView.findViewById(R.id.textViewActivityDuration);
                textViewActivityPace = itemView.findViewById(R.id.textViewActivityPace);
                textViewKudosCount = itemView.findViewById(R.id.textViewKudosCount);
                buttonKudos = itemView.findViewById(R.id.buttonKudos);
            }
        }
    }
} 