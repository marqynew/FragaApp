package com.example.fraga;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

// import com.bumptech.glide.Glide;
import com.example.fraga.db.entity.ActivityLog;
import com.example.fraga.viewmodel.FeedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    private RecyclerView recyclerViewActivityFeed;
    private ActivityAdapter activityAdapter;
    private FloatingActionButton fabCreateActivity;
    private FeedViewModel feedViewModel;
    private int currentUserId = -1;

    public interface KudosClickListener {
        void onKudosClicked(int activityId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        }

        recyclerViewActivityFeed = view.findViewById(R.id.recyclerViewActivityFeed);
        recyclerViewActivityFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        activityAdapter = new ActivityAdapter(getContext(), currentUserId,
                activityId -> {
                    if (feedViewModel != null) {
                        feedViewModel.incrementKudos(activityId);
                    }
                }
        );
        recyclerViewActivityFeed.setAdapter(activityAdapter);

        fabCreateActivity = view.findViewById(R.id.fabCreateActivity);
        if (fabCreateActivity != null) {
            fabCreateActivity.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), CreateActivityActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (getActivity() != null) {
            feedViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                    .get(FeedViewModel.class);

            feedViewModel.getPublicActivitiesFeed().observe(getViewLifecycleOwner(), activityLogs -> {
                if (activityLogs != null) {
                    Log.d(TAG, "FeedFragment: Feed updated with " + activityLogs.size() + " activities.");
                    activityAdapter.submitList(activityLogs); // Menerima List<ActivityLog>
                } else {
                    Log.w(TAG, "FeedFragment: Received null activityLogs list from ViewModel.");
                    activityAdapter.submitList(new ArrayList<>());
                }
            });
        } else {
            Log.e(TAG, "FeedFragment: Activity is null, cannot initialize ViewModel.");
            if(getContext() != null) {
                Toast.makeText(getContext(), "Error loading feed data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class ActivityAdapter extends ListAdapter<ActivityLog, ActivityAdapter.ActivityViewHolder> {

        private Context context;
        private int currentUserIdAdapter;
        private KudosClickListener kudosClickListenerAdapter;

        protected ActivityAdapter(Context context, int currentUserId, KudosClickListener kudosClickListener) {
            super(DIFF_CALLBACK);
            this.context = context;
            this.currentUserIdAdapter = currentUserId;
            this.kudosClickListenerAdapter = kudosClickListener;
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
            ActivityLog activityLog = getItem(position);
            if (activityLog == null || context == null) return;

            if (activityLog.getUserId() == currentUserIdAdapter && currentUserIdAdapter != -1) {
                SharedPreferences prefs = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
                String userEmail = prefs.getString(LoginActivity.KEY_USER_EMAIL, "You");
                holder.textViewUsername.setText(userEmail.contains("@") ? userEmail.split("@")[0] : userEmail);
            } else {
                // TODO: Implementasi pengambilan nama pengguna yang sebenarnya untuk ID pengguna lain.
                holder.textViewUsername.setText("User " + activityLog.getUserId());
            }

            holder.imageViewUserAvatar.setImageResource(R.drawable.user_avatar);
            holder.imageViewActivityMap.setImageResource(R.drawable.activity_map);

            holder.textViewActivityTime.setText(activityLog.getFormattedTimestamp("dd MMM, HH:mm"));
            holder.textViewActivityTitle.setText(activityLog.getTitle());
            holder.textViewActivityDescription.setText(activityLog.getDescription());
            holder.textViewActivityDistance.setText(activityLog.getFormattedDistance());
            holder.textViewActivityDuration.setText(activityLog.getFormattedDuration());
            holder.textViewActivityPace.setText(activityLog.getFormattedPace());
            holder.textViewKudosCount.setText(String.valueOf(activityLog.getKudosCount()) + " kudos");

            if (holder.buttonKudos != null) {
                holder.buttonKudos.setOnClickListener(v -> {
                    if (currentUserIdAdapter == -1) {
                        Toast.makeText(context, "Please login to give kudos.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (kudosClickListenerAdapter != null) {
                        kudosClickListenerAdapter.onKudosClicked(activityLog.getId());
                    }
                });
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityDetailActivity.class);
                intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY_ID, activityLog.getId());
                context.startActivity(intent);
            });
        }

        static class ActivityViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewUserAvatar, imageViewActivityMap;
            TextView textViewUsername, textViewActivityTime, textViewActivityTitle, textViewActivityDescription;
            TextView textViewActivityDistance, textViewActivityDuration, textViewActivityPace, textViewKudosCount;
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

        public static final DiffUtil.ItemCallback<ActivityLog> DIFF_CALLBACK =
                new DiffUtil.ItemCallback<ActivityLog>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull ActivityLog oldItem, @NonNull ActivityLog newItem) {
                        return oldItem.getId() == newItem.getId();
                    }

                    @SuppressLint("DiffUtilEquals")
                    @Override
                    public boolean areContentsTheSame(@NonNull ActivityLog oldItem, @NonNull ActivityLog newItem) {
                        return oldItem.equals(newItem); // Pastikan ActivityLog.java punya equals()
                    }
                };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}