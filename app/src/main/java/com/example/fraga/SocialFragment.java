package com.example.fraga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Jika Anda ingin menambahkan view secara programatik
import android.widget.TextView;    // Untuk contoh
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// Import yang akan dibutuhkan nanti:
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;
// import java.util.ArrayList;
// import java.util.List;

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// import com.example.fraga.db.entity.User; // Untuk ringkasan teman
// import com.example.fraga.db.entity.LeaderboardEntry; // Untuk cuplikan leaderboard
// import com.example.fraga.db.entity.GroupActivitySummary; // Untuk ringkasan aktivitas grup
// import com.example.fraga.adapter.MiniFriendsAdapter;
// import com.example.fraga.adapter.MiniLeaderboardAdapter;
// import com.example.fraga.adapter.MiniGroupActivityAdapter;


public class SocialFragment extends Fragment {

    private static final String TAG = "SocialFragment";

    // Contoh: LinearLayout di dalam CardView untuk menambahkan view secara programatik atau RecyclerView
    // private LinearLayout layoutFriendsContent;
    // private LinearLayout layoutLeaderboardsContent;
    // private LinearLayout layoutGroupActivitiesContent;

    // Atau RecyclerView jika Anda memutuskan untuk menggunakannya untuk setiap bagian
    // private RecyclerView recyclerFriendsSummary;
    // private RecyclerView recyclerLeaderboardSnippet;
    // private RecyclerView recyclerGroupActivitiesPreview;

    // private AppDatabase appDb;
    private int currentUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false); //
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil currentUserId
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        }
        if (currentUserId == -1) {
            Log.w(TAG, "Current User ID not found. Social data might be limited.");
        }

        // Dapatkan instance database (akan diaktifkan nanti)
        // if (getContext() != null) {
        //     appDb = AppDatabase.getInstance(getContext().getApplicationContext());
        // }

        // Inisialisasi Views (layout container untuk setiap Card)
        // layoutFriendsContent = view.findViewById(R.id.id_friends_content_container); // Ganti dengan ID yang sesuai dari XML
        // layoutLeaderboardsContent = view.findViewById(R.id.id_leaderboards_content_container);
        // layoutGroupActivitiesContent = view.findViewById(R.id.id_group_activities_content_container);

        // Muat dan tampilkan fitur sosial
        setupSocialFeatures(view);
    }

    private void setupSocialFeatures(View view) {
        // Hapus Toast placeholder
        // Toast.makeText(getContext(), "Social features coming soon!", Toast.LENGTH_SHORT).show(); //

        loadFriendsSummary(view);
        loadLeaderboardSnippet(view);
        loadGroupActivitiesPreview(view);
    }

    private void loadFriendsSummary(View parentView) {
        // TODO: Ambil ringkasan data teman dari database (misal, jumlah teman, beberapa avatar)
        //       dan tampilkan di dalam CardView "Friends".
        //       Layout `fragment_social.xml` memiliki `LinearLayout` di dalam `MaterialCardView`
        //       yang bisa Anda gunakan untuk menambahkan `TextView` atau `RecyclerView` kecil.

        // Contoh: Menambahkan TextView secara programatik (atau idealnya gunakan RecyclerView)
        // if (layoutFriendsContent != null && getContext() != null) {
        //     TextView tvFriendCount = new TextView(getContext());
        //     // Misal: int friendCount = appDb.friendshipDao().getFriendCount(currentUserId);
        //     // tvFriendCount.setText("You have " + friendCount + " friends.");
        //     tvFriendCount.setText("Friend summary will appear here (from DB).");
        //     layoutFriendsContent.addView(tvFriendCount);

        //     MaterialButton viewAllFriendsButton = new MaterialButton(getContext());
        //     viewAllFriendsButton.setText("View All Friends");
        //     viewAllFriendsButton.setOnClickListener(v -> {
        //         // Navigasi ke SocialActivity atau FriendsFragment yang lebih detail
        //         // Intent intent = new Intent(getActivity(), SocialActivity.class);
        //         // startActivity(intent);
        //          Toast.makeText(getContext(), "Navigate to full friends list", Toast.LENGTH_SHORT).show();
        //     });
        //     layoutFriendsContent.addView(viewAllFriendsButton);
        // }
        if (getContext()!=null) {
            Toast.makeText(getContext(), "Friends summary (DB load pending)", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLeaderboardSnippet(View parentView) {
        // TODO: Ambil cuplikan data leaderboard (misal, top 3) dari database
        //       dan tampilkan di dalam CardView "Leaderboards".

        // if (layoutLeaderboardsContent != null && getContext() != null) {
        //     TextView tvLeaderboardInfo = new TextView(getContext());
        //     // List<LeaderboardEntry> topEntries = appDb.leaderboardDao().getTopNEntries(3);
        //     // tvLeaderboardInfo.setText("Top player: " + (topEntries.isEmpty() ? "N/A" : topEntries.get(0).getUserName()));
        //     tvLeaderboardInfo.setText("Leaderboard snippet will appear here (from DB).");
        //     layoutLeaderboardsContent.addView(tvLeaderboardInfo);
        //     // Tambahkan tombol untuk melihat leaderboard penuh
        // }
        if (getContext()!=null) {
            Toast.makeText(getContext(), "Leaderboard snippet (DB load pending)", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGroupActivitiesPreview(View parentView) {
        // TODO: Ambil data pratinjau aktivitas grup (misal, aktivitas grup yang akan datang atau yang diikuti)
        //       dan tampilkan di dalam CardView "Group Activities".

        // if (layoutGroupActivitiesContent != null && getContext() != null) {
        //     TextView tvGroupInfo = new TextView(getContext());
        //     // List<GroupActivitySummary> upcomingGroups = appDb.groupActivityDao().getUpcomingActivitiesForUser(currentUserId, 3);
        //     // tvGroupInfo.setText("Next group activity: " + (upcomingGroups.isEmpty() ? "None scheduled" : upcomingGroups.get(0).getTitle()));
        //     tvGroupInfo.setText("Group activities preview will appear here (from DB).");
        //     layoutGroupActivitiesContent.addView(tvGroupInfo);
        //     // Tambahkan tombol untuk melihat semua aktivitas grup
        // }
        if (getContext()!=null) {
            Toast.makeText(getContext(), "Group activities preview (DB load pending)", Toast.LENGTH_SHORT).show();
        }
    }
}