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
import androidx.lifecycle.ViewModelProvider; // Akan digunakan nanti dengan ViewModel
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

// import com.bumptech.glide.Glide;
import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.ActivityLog; // Ganti ActivityItem dengan ActivityLog
import com.example.fraga.db.entity.User;
// import com.example.fraga.viewmodel.ProfileViewModel; // Anda akan membuat ini nanti

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private RecentActivityAdapter recentActivityAdapter; // Deklarasikan di sini
    private Button buttonViewAllActivities;
    private Button buttonSettings;
    private Button buttonChallenges;
    private Button buttonSocial;
    private ImageView imageViewProfilePic;

    private AppDatabase appDb;
    private ExecutorService databaseExecutor;
    private int currentUserId = -1;
    // private ProfileViewModel profileViewModel; // Untuk nanti

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            appDb = AppDatabase.getInstance(getContext().getApplicationContext());
            databaseExecutor = Executors.newSingleThreadExecutor();
            SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        }

        initializeViews(view);
        setupButtonListeners();
        setupRecentActivitiesRecyclerView();

        loadUserProfileData();
        loadRecentUserActivities();
    }

    private void initializeViews(View view) {
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
    }

    private void setupButtonListeners() {
        if (buttonViewAllActivities != null) {
            buttonViewAllActivities.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Viewing all activities (not yet implemented)", Toast.LENGTH_SHORT).show();
                // TODO: Navigasi ke layar daftar semua aktivitas pengguna
            });
        }

        if (buttonSettings != null) {
            buttonSettings.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (buttonChallenges != null) {
            buttonChallenges.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (buttonSocial != null) {
            buttonSocial.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), SocialActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupRecentActivitiesRecyclerView() {
        if (recyclerViewRecentActivities != null && getContext() != null) {
            recyclerViewRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
            recentActivityAdapter = new RecentActivityAdapter(getContext());
            recyclerViewRecentActivities.setAdapter(recentActivityAdapter);
        }
    }

    private void loadUserProfileData() {
        if (currentUserId == -1 || appDb == null || getContext() == null) {
            // Tampilkan UI default atau pesan error jika tidak ada pengguna yang login atau DB tidak siap
            if (textViewProfileName != null) textViewProfileName.setText("Guest User");
            // ... (set field lain ke default) ...
            return;
        }

        // Idealnya, gunakan ViewModel dan LiveData di sini
        databaseExecutor.execute(() -> {
            User user = appDb.userDao().getUserById(currentUserId); // Metode sinkron, jadi perlu di background
            // Statistik juga perlu diambil atau dihitung
            // LiveData<Double> totalDistanceLive = appDb.activityLogDao().getTotalDistanceForUser(currentUserId);
            // LiveData<Integer> totalActivitiesLive = appDb.activityLogDao().getTotalActivitiesForUser(currentUserId);
            // LiveData<Integer> totalKudosLive = appDb.activityLogDao().getTotalKudosReceivedByUser(currentUserId);
            // LiveData<Integer> friendCountLive = appDb.friendshipDao().getFriendCountForUser(currentUserId);


            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        if (textViewProfileName != null) textViewProfileName.setText(user.getName());
                        if (textViewProfileLocation != null) textViewProfileLocation.setText(user.getLocation() != null ? user.getLocation() : "N/A");
                        // TODO: Muat gambar profil dengan Glide jika user.getProfileImageUrl() ada
                        if (imageViewProfilePic != null) imageViewProfilePic.setImageResource(R.drawable.profile_image); // Placeholder

                        // Update statistik (ini contoh sederhana, LiveData lebih baik)
                        // Ambil data statistik dari DAO secara langsung untuk contoh ini
                        // (Di aplikasi nyata, ViewModel akan mengelola LiveData ini)
                        updateStatsFromDb();
                    } else {
                        Toast.makeText(getContext(), "Failed to load user profile.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateStatsFromDb() {
        if (currentUserId == -1 || appDb == null || getContext() == null) return;

        appDb.activityLogDao().getTotalDistanceForUser(currentUserId).observe(getViewLifecycleOwner(), totalDistance -> {
            if (textViewTotalDistance != null) {
                textViewTotalDistance.setText(String.format(Locale.getDefault(), "%.1f km", totalDistance != null ? totalDistance : 0.0));
            }
        });
        appDb.activityLogDao().getTotalActivitiesForUser(currentUserId).observe(getViewLifecycleOwner(), totalActivities -> {
            if (textViewTotalActivities != null) {
                textViewTotalActivities.setText(String.valueOf(totalActivities != null ? totalActivities : 0));
            }
        });
        appDb.activityLogDao().getTotalKudosReceivedByUser(currentUserId).observe(getViewLifecycleOwner(), totalKudos -> {
            if (textViewTotalKudos != null) {
                textViewTotalKudos.setText(String.valueOf(totalKudos != null ? totalKudos : 0));
            }
        });
        appDb.friendshipDao().getFriendCountForUser(currentUserId).observe(getViewLifecycleOwner(), friendCount -> {
            if (textViewFollowersCount != null) textViewFollowersCount.setText(String.valueOf(friendCount != null ? friendCount : 0));
            if (textViewFollowingCount != null) textViewFollowingCount.setText(String.valueOf(friendCount != null ? friendCount : 0)); // Asumsi followers = following
        });


    }


    private void loadRecentUserActivities() {
        if (currentUserId == -1 || appDb == null || getContext() == null) {
            if (recentActivityAdapter != null) recentActivityAdapter.submitList(new ArrayList<>());
            return;
        }
        // Idealnya, gunakan ViewModel di sini
        // Untuk contoh, kita ambil langsung dari DAO dan observe LiveData
        appDb.activityLogDao().getRecentActivityLogsForUser(currentUserId, 5) // Ambil 5 aktivitas terbaru
                .observe(getViewLifecycleOwner(), activityLogs -> {
                    if (activityLogs != null && recentActivityAdapter != null) {
                        Log.d(TAG, "ProfileFragment: Recent activities loaded: " + activityLogs.size());
                        recentActivityAdapter.submitList(activityLogs);
                    } else if (recentActivityAdapter != null) {
                        recentActivityAdapter.submitList(new ArrayList<>());
                    }
                });
    }

    // RecentActivityAdapter sekarang menggunakan ListAdapter<ActivityLog, ...>
    static class RecentActivityAdapter extends ListAdapter<ActivityLog, RecentActivityAdapter.ActivityViewHolder> {

        private Context context;

        protected RecentActivityAdapter(Context context) {
            super(DIFF_CALLBACK);
            this.context = context;
        }

        @NonNull
        @Override
        public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity, parent, false); // Menggunakan item_activity.xml
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
            ActivityLog activityLog = getItem(position);
            if (activityLog == null || context == null) return;

            // Karena ini adalah aktivitas pengguna sendiri, kita tidak perlu menampilkan nama pengguna di sini
            // atau bisa tampilkan "Your Activity" atau sembunyikan textViewUsername
            if(holder.textViewUsername != null) holder.textViewUsername.setVisibility(View.GONE); // Sembunyikan username
            if(holder.imageViewUserAvatar != null) holder.imageViewUserAvatar.setVisibility(View.GONE); // Sembunyikan avatar

            holder.textViewActivityTime.setText(activityLog.getFormattedTimestamp("dd MMM, HH:mm"));
            holder.textViewActivityTitle.setText(activityLog.getTitle());
            holder.textViewActivityDescription.setText(activityLog.getDescription());
            holder.textViewActivityDistance.setText(activityLog.getFormattedDistance());
            holder.textViewActivityDuration.setText(activityLog.getFormattedDuration());
            holder.textViewActivityPace.setText(activityLog.getFormattedPace());
            holder.textViewKudosCount.setText(String.valueOf(activityLog.getKudosCount()) + " kudos");

            // Sembunyikan tombol kudos jika ini adalah aktivitas sendiri di profil
            if(holder.buttonKudos != null) holder.buttonKudos.setVisibility(View.GONE);


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
        if (databaseExecutor != null && !databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
        }
    }
}