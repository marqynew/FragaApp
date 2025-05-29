package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Diperlukan untuk RecyclerView

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList; // Diperlukan untuk data contoh
import java.util.List;      // Diperlukan untuk List

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// import com.example.fraga.db.entity.ActiveChallenge; // Contoh Entitas
// import com.example.fraga.db.entity.PersonalGoal;    // Contoh Entitas
// import com.example.fraga.db.entity.Achievement;     // Contoh Entitas
// import com.example.fraga.adapter.ActiveChallengeAdapter; // Contoh Adapter
// import com.example.fraga.adapter.PersonalGoalAdapter;    // Contoh Adapter
// import com.example.fraga.adapter.AchievementAdapter;     // Contoh Adapter
// Untuk kelas Activity/Dialog pembuatan goal:
// import com.example.fraga.CreateGoalActivity; // Contoh

public class ChallengesFragment extends Fragment {

    // Deklarasikan RecyclerViews dan Adapters (akan diinisialisasi nanti)
    // private RecyclerView recyclerViewActiveChallenges;
    // private RecyclerView recyclerViewPersonalGoals;
    // private RecyclerView recyclerViewAchievements;

    // private ActiveChallengeAdapter activeChallengeAdapter;
    // private PersonalGoalAdapter personalGoalAdapter;
    // private AchievementAdapter achievementAdapter;

    private ExtendedFloatingActionButton fabCreateGoal;

    // private AppDatabase appDb; // Akan digunakan setelah AppDatabase dibuat

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dapatkan instance database (akan diaktifkan nanti)
        // if (getContext() != null) {
        //     appDb = AppDatabase.getInstance(getContext().getApplicationContext());
        // }

        // Inisialisasi UI components
        // recyclerViewActiveChallenges = view.findViewById(R.id.id_recyclerview_active_challenges); // Ganti dengan ID yang sesuai dari XML
        // recyclerViewPersonalGoals = view.findViewById(R.id.id_recyclerview_personal_goals); // Ganti dengan ID yang sesuai dari XML
        // recyclerViewAchievements = view.findViewById(R.id.id_recyclerview_achievements); // Ganti dengan ID yang sesuai dari XML
        fabCreateGoal = view.findViewById(R.id.fabCreateGoal);

        // Setup RecyclerViews (akan diaktifkan nanti setelah ada ID di XML dan Adapter dibuat)
        // setupRecyclerViews();

        // Load data dan setup fitur tantangan
        setupChallengesFeatures(view);

        // Setup listener untuk FAB
        if (fabCreateGoal != null) {
            fabCreateGoal.setOnClickListener(v -> {
                // Navigasi ke Activity untuk membuat Goal baru atau tampilkan Dialog
                Toast.makeText(getContext(), "Navigasi ke halaman pembuatan goal...", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(getActivity(), CreateGoalActivity.class);
                // startActivity(intent);
            });
        }
    }

    /*
    private void setupRecyclerViews() {
        // Setup untuk Active Challenges
        if (recyclerViewActiveChallenges != null) {
            recyclerViewActiveChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
            // activeChallengeAdapter = new ActiveChallengeAdapter(new ArrayList<>()); // Inisialisasi dengan list kosong
            // recyclerViewActiveChallenges.setAdapter(activeChallengeAdapter);
        }

        // Setup untuk Personal Goals
        if (recyclerViewPersonalGoals != null) {
            recyclerViewPersonalGoals.setLayoutManager(new LinearLayoutManager(getContext()));
            // personalGoalAdapter = new PersonalGoalAdapter(new ArrayList<>()); // Inisialisasi dengan list kosong
            // recyclerViewPersonalGoals.setAdapter(personalGoalAdapter);
        }

        // Setup untuk Achievements
        if (recyclerViewAchievements != null) {
            recyclerViewAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
            // achievementAdapter = new AchievementAdapter(new ArrayList<>()); // Inisialisasi dengan list kosong
            // recyclerViewAchievements.setAdapter(achievementAdapter);
        }
    }
    */

    private void setupChallengesFeatures(View view) {
        // Hapus Toast placeholder
        // Toast.makeText(getContext(), "Challenges features coming soon!", Toast.LENGTH_SHORT).show();

        // TODO: Implementasi pengambilan dan penampilan fitur tantangan dari database.
        // Ini akan melibatkan:
        // 1. Mengambil data dari appDb menggunakan DAO yang sesuai.
        // 2. Memperbarui adapter RecyclerView dengan data yang diambil.

        loadActiveChallenges();
        loadPersonalGoals();
        loadAchievements();
    }

    private void loadActiveChallenges() {
        // Contoh bagaimana data akan diambil dan ditampilkan (AKAN DIAKTIFKAN NANTI)
        /*
        if (appDb != null) {
            // Jalankan di background thread atau gunakan LiveData/ViewModel
            new Thread(() -> {
                List<ActiveChallenge> challenges = appDb.activeChallengeDao().getAllActiveChallenges();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (activeChallengeAdapter != null) {
                            activeChallengeAdapter.updateData(challenges);
                        } else if (challenges.isEmpty()){
                            // Tampilkan pesan jika tidak ada tantangan aktif
                            // Misalnya: textViewNoActiveChallenges.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }
        */
        // Untuk sekarang, tampilkan pesan bahwa fitur ini akan datang atau data dummy
        if (getContext() != null) {
            // Jika Anda sudah menambahkan RecyclerView ke XML (misalnya dengan ID: recyclerViewActiveChallenges),
            // Anda bisa mengisi data dummy di sini atau di adapter.
            Toast.makeText(getContext(), "Fitur daftar tantangan aktif akan mengambil data dari DB.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPersonalGoals() {
        // Contoh (AKAN DIAKTIFKAN NANTI)
        /*
        if (appDb != null) {
            new Thread(() -> {
                List<PersonalGoal> goals = appDb.personalGoalDao().getAllPersonalGoals();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (personalGoalAdapter != null) {
                            personalGoalAdapter.updateData(goals);
                        }
                    });
                }
            }).start();
        }
        */
        if (getContext() != null) {
            Toast.makeText(getContext(), "Fitur daftar tujuan pribadi akan mengambil data dari DB.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAchievements() {
        // Contoh (AKAN DIAKTIFKAN NANTI)
        /*
        if (appDb != null) {
            new Thread(() -> {
                List<Achievement> achievements = appDb.achievementDao().getAllAchievements();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (achievementAdapter != null) {
                            achievementAdapter.updateData(achievements);
                        }
                    });
                }
            }).start();
        }
        */
        if (getContext() != null) {
            Toast.makeText(getContext(), "Fitur daftar pencapaian akan mengambil data dari DB.", Toast.LENGTH_SHORT).show();
        }
    }
}