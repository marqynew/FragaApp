package com.example.fraga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Untuk pesan sementara

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
// import com.example.fraga.db.entity.User; // Atau entitas khusus LeaderboardItem
// import com.example.fraga.adapter.LeaderboardAdapter;

public class LeaderboardFragment extends Fragment {

    // Variabel yang akan dibutuhkan nanti untuk RecyclerView
    // private RecyclerView recyclerViewLeaderboard;
    // private LeaderboardAdapter leaderboardAdapter;
    // private AppDatabase appDb;

    private TextView textViewPlaceholder; // TextView dari fragment_placeholder.xml

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tetap menggunakan layout placeholder untuk saat ini
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewPlaceholder = view.findViewById(R.id.textViewPlaceholder);

        // Dapatkan instance database (akan diaktifkan nanti)
        // if (getContext() != null) {
        //     appDb = AppDatabase.getInstance(getContext().getApplicationContext());
        // }

        // Inisialisasi RecyclerView dan Adapter (akan diaktifkan nanti setelah layout diubah)
        // recyclerViewLeaderboard = view.findViewById(R.id.id_recyclerview_leaderboard); // Ganti dengan ID dari layout baru
        // if (recyclerViewLeaderboard != null) {
        //     recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        //     leaderboardAdapter = new LeaderboardAdapter(new ArrayList<>()); // Inisialisasi dengan list kosong
        //     recyclerViewLeaderboard.setAdapter(leaderboardAdapter);
        // }

        // Muat data papan peringkat atau tampilkan placeholder
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        // Untuk saat ini, kita akan tetap menggunakan teks placeholder.
        // Jika layout sudah diubah menjadi RecyclerView, logika pengambilan data dari DB akan ada di sini.
        if (textViewPlaceholder != null) {
            textViewPlaceholder.setText("Leaderboard\n\nSee how you stack up against friends and the community. Check weekly, monthly, and all-time rankings. This feature will soon list ranked users from the database.");
        }

        // Contoh bagaimana data akan diambil dan ditampilkan (AKAN DIAKTIFKAN NANTI)
        /*
        if (appDb != null && leaderboardAdapter != null) {
            // Jalankan di background thread atau gunakan LiveData/ViewModel
            new Thread(() -> {
                // Misalkan ada metode di UserDao atau LeaderboardDao untuk mengambil data peringkat
                // List<User> rankedUsers = appDb.userDao().getRankedUsersByDistance("monthly"); // Contoh
                // Atau: List<LeaderboardItem> leaderboardData = appDb.leaderboardDao().getMonthlyLeaderboard();


                // Contoh sederhana mengambil semua pengguna dan mengurutkannya (ini mungkin tidak efisien untuk leaderboard besar)
                List<User> users = appDb.userDao().getAllUsers();
                // Lakukan pengurutan di sini berdasarkan kriteria leaderboard
                // Collections.sort(users, (u1, u2) -> Double.compare(u2.getTotalDistance(), u1.getTotalDistance()));


                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (users != null && !users.isEmpty()) {
                            // leaderboardAdapter.updateData(users); // Atau leaderboardData
                            if (textViewPlaceholder != null) {
                                textViewPlaceholder.setVisibility(View.GONE); // Sembunyikan placeholder jika ada data
                            }
                            if (recyclerViewLeaderboard != null) {
                                recyclerViewLeaderboard.setVisibility(View.VISIBLE); // Tampilkan RecyclerView
                            }
                        } else {
                            // Tampilkan pesan jika tidak ada data leaderboard
                            if (textViewPlaceholder != null) {
                                textViewPlaceholder.setText("Leaderboard data is not available at the moment.");
                                textViewPlaceholder.setVisibility(View.VISIBLE);
                            }
                            if (recyclerViewLeaderboard != null) {
                                recyclerViewLeaderboard.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
        } else if (getContext() != null) {
            // Jika appDb atau leaderboardAdapter null
            // Toast.makeText(getContext(), "Database or adapter not ready for Leaderboard.", Toast.LENGTH_SHORT).show();
        }
        */
    }

    // Anda perlu membuat kelas User (Entitas) atau LeaderboardItem (jika berbeda)
    // dan LeaderboardAdapter (Adapter RecyclerView) secara terpisah.
}