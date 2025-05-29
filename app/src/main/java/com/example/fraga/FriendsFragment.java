package com.example.fraga;

import android.content.Intent; // Untuk navigasi ke profil teman/detail grup
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
// Import yang akan dibutuhkan nanti:
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;
// import java.util.ArrayList;
// import java.util.List;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// import com.example.fraga.db.entity.User;
// import com.example.fraga.db.entity.FriendRequest;
// import com.example.fraga.db.entity.FriendGroup;
// import com.example.fraga.adapter.FriendRequestAdapter;
// import com.example.fraga.adapter.FriendAdapter;
// import com.example.fraga.adapter.GroupAdapter;
// import com.example.fraga.UserProfileActivity; // Contoh Activity untuk profil user
// import com.example.fraga.GroupDetailActivity; // Contoh Activity untuk detail grup


public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    // private AppDatabase appDb;

    // Variabel untuk RecyclerView (akan digunakan jika layout diubah)
    // private RecyclerView recyclerViewFriendRequests;
    // private RecyclerView recyclerViewFriendsList;
    // private RecyclerView recyclerViewGroups;

    // private FriendRequestAdapter friendRequestAdapter;
    // private FriendAdapter friendAdapter;
    // private GroupAdapter groupAdapter;

    // Tombol-tombol dari layout saat ini
    private MaterialButton buttonAcceptRequest1, buttonDeclineRequest1;
    private MaterialButton buttonAcceptRequest2, buttonDeclineRequest2;
    private CardView cardViewFriend1, cardViewFriend2, cardViewFriend3;
    private MaterialCardView cardViewGroup1, cardViewGroup2, cardViewGroup3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dapatkan instance database (akan diaktifkan nanti)
        // if (getContext() != null) {
        //     appDb = AppDatabase.getInstance(getContext().getApplicationContext());
        // }

        // Inisialisasi view yang ada di layout
        initializeViews(view);

        // Setup friend requests
        setupFriendRequests();

        // Setup friends list
        setupFriendsList();

        // Setup groups
        setupGroups();

        // Idealnya, Anda akan memuat data dari database di sini
        // loadFriendRequestsFromDb();
        // loadFriendsListFromDb();
        // loadGroupsFromDb();
    }

    private void initializeViews(View view) {
        buttonAcceptRequest1 = view.findViewById(R.id.buttonAcceptRequest1);
        buttonDeclineRequest1 = view.findViewById(R.id.buttonDeclineRequest1);
        buttonAcceptRequest2 = view.findViewById(R.id.buttonAcceptRequest2);
        buttonDeclineRequest2 = view.findViewById(R.id.buttonDeclineRequest2);

        cardViewFriend1 = view.findViewById(R.id.cardViewFriend1);
        cardViewFriend2 = view.findViewById(R.id.cardViewFriend2);
        cardViewFriend3 = view.findViewById(R.id.cardViewFriend3);

        cardViewGroup1 = view.findViewById(R.id.cardViewGroup1);
        cardViewGroup2 = view.findViewById(R.id.cardViewGroup2);
        cardViewGroup3 = view.findViewById(R.id.cardViewGroup3);

        // Jika menggunakan RecyclerView:
        // recyclerViewFriendRequests = view.findViewById(R.id.id_recyclerview_friend_requests);
        // recyclerViewFriendsList = view.findViewById(R.id.id_recyclerview_friends_list);
        // recyclerViewGroups = view.findViewById(R.id.id_recyclerview_groups);
        // setupRecyclerViews();
    }
    /*
    private void setupRecyclerViews() {
        if (recyclerViewFriendRequests != null) {
            recyclerViewFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
            // friendRequestAdapter = new FriendRequestAdapter(new ArrayList<>(), this::onAcceptRequest, this::onDeclineRequest);
            // recyclerViewFriendRequests.setAdapter(friendRequestAdapter);
        }
        // ... setup untuk friends list dan groups
    }
    */

    private void setupFriendRequests() {
        // Data untuk permintaan pertemanan saat ini statis di layout.
        // Saat menggunakan DB, Anda akan mengambil List<FriendRequest> dan menampilkannya
        // (kemungkinan menggunakan RecyclerView dan FriendRequestAdapter).

        // Request 1 - Tombol sudah diinisialisasi di initializeViews
        // Asumsikan ID permintaan pertemanan adalah 1 (contoh)
        final int friendRequestId1 = 1; // ID ini akan datang dari objek FriendRequest dari DB
        buttonAcceptRequest1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
            // TODO: Logika Database untuk menerima permintaan (misal, update status FriendRequest, tambahkan ke tabel pertemanan)
            // new Thread(() -> {
            //    appDb.friendRequestDao().updateStatus(friendRequestId1, "ACCEPTED");
            //    appDb.friendshipDao().addFriendship(currentUserId, senderId1); // currentUserId dan senderId1 dari FriendRequest
            //    getActivity().runOnUiThread(() -> {
            //        // Refresh list atau hapus item ini dari UI
            //        buttonAcceptRequest1.setEnabled(false);
            //        buttonDeclineRequest1.setEnabled(false);
            //        buttonAcceptRequest1.setText("Accepted");
            //    });
            // }).start();
            buttonAcceptRequest1.setEnabled(false);
            buttonDeclineRequest1.setEnabled(false);
            buttonAcceptRequest1.setText("Accepted");
        });

        buttonDeclineRequest1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            // TODO: Logika Database untuk menolak permintaan (misal, update status FriendRequest atau hapus)
            // new Thread(() -> {
            //    appDb.friendRequestDao().updateStatus(friendRequestId1, "DECLINED");
            //    getActivity().runOnUiThread(() -> {
            //        // Refresh list atau hapus item ini dari UI
            //        buttonAcceptRequest1.setEnabled(false);
            //        buttonDeclineRequest1.setEnabled(false);
            //        buttonDeclineRequest1.setText("Declined");
            //    });
            // }).start();
            buttonAcceptRequest1.setEnabled(false);
            buttonDeclineRequest1.setEnabled(false);
            buttonDeclineRequest1.setText("Declined");
        });

        // Request 2 - Tombol sudah diinisialisasi
        final int friendRequestId2 = 2; // ID ini akan datang dari objek FriendRequest dari DB
        buttonAcceptRequest2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
            // TODO: Logika Database
            buttonAcceptRequest2.setEnabled(false);
            buttonDeclineRequest2.setEnabled(false);
            buttonAcceptRequest2.setText("Accepted");
        });

        buttonDeclineRequest2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            // TODO: Logika Database
            buttonAcceptRequest2.setEnabled(false);
            buttonDeclineRequest2.setEnabled(false);
            buttonDeclineRequest2.setText("Declined");
        });
    }

    // Metode callback jika menggunakan adapter
    // private void onAcceptRequest(FriendRequest request) { /* ... DB logic ... */ }
    // private void onDeclineRequest(FriendRequest request) { /* ... DB logic ... */ }


    private void setupFriendsList() {
        // Data teman saat ini statis di layout.
        // Saat menggunakan DB, Anda akan mengambil List<User> (teman) dan menampilkannya.

        // Friend 1 (Alex Martinez) - CardView sudah diinisialisasi
        // Asumsikan ID pengguna Alex adalah "user_alex" (contoh)
        final String userIdAlex = "user_alex"; // ID ini akan datang dari objek User dari DB
        cardViewFriend1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Alex's profile", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke profil pengguna dengan userIdAlex
            // Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            // intent.putExtra("USER_ID", userIdAlex);
            // startActivity(intent);
        });

        // Friend 2 (Emily Wong)
        final String userIdEmily = "user_emily";
        cardViewFriend2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View Emily's profile", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke profil pengguna dengan userIdEmily
        });

        // Friend 3 (James Wilson)
        final String userIdJames = "user_james";
        cardViewFriend3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View James's profile", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke profil pengguna dengan userIdJames
        });
    }

    private void setupGroups() {
        // Data grup saat ini statis di layout.
        // Saat menggunakan DB, Anda akan mengambil List<FriendGroup> dan menampilkannya.

        // Group 1 (Morning Runners) - CardView sudah diinisialisasi
        // Asumsikan ID grup adalah "group_morning_runners" (contoh)
        final String groupIdMorningRunners = "group_morning_runners"; // ID ini akan datang dari objek Group dari DB
        cardViewGroup1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Morning Runners group details", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke detail grup dengan groupIdMorningRunners
            // Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
            // intent.putExtra("GROUP_ID", groupIdMorningRunners);
            // startActivity(intent);
        });

        // Group 2 (Weekend Warriors)
        final String groupIdWeekendWarriors = "group_weekend_warriors";
        cardViewGroup2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Weekend Warriors group details", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke detail grup
        });

        // Group 3 (City Cyclists)
        final String groupIdCityCyclists = "group_city_cyclists";
        cardViewGroup3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "City Cyclists group details", Toast.LENGTH_SHORT).show();
            // TODO: Navigasi ke detail grup
        });
    }

    // Metode untuk memuat data dari database (akan diimplementasikan nanti)
    /*
    private void loadFriendRequestsFromDb() {
        if (appDb == null || friendRequestAdapter == null) return;
        new Thread(() -> {
            // List<FriendRequest> requests = appDb.friendRequestDao().getPendingRequestsForUser(currentUserId);
            // getActivity().runOnUiThread(() -> friendRequestAdapter.updateData(requests));
        }).start();
    }

    private void loadFriendsListFromDb() {
        if (appDb == null || friendAdapter == null) return;
        new Thread(() -> {
            // List<User> friends = appDb.userDao().getFriendsOfUser(currentUserId);
            // getActivity().runOnUiThread(() -> friendAdapter.updateData(friends));
        }).start();
    }

    private void loadGroupsFromDb() {
        if (appDb == null || groupAdapter == null) return;
        new Thread(() -> {
            // List<FriendGroup> groups = appDb.friendGroupDao().getGroupsForUser(currentUserId);
            // getActivity().runOnUiThread(() -> groupAdapter.updateData(groups));
        }).start();
    }
    */
}