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
import android.widget.ImageView; // Untuk gambar di adapter
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


// import com.bumptech.glide.Glide;
import com.example.fraga.adapter.MonthlyChallengeAdapter;
import com.example.fraga.db.entity.MonthlyChallenge;
import com.example.fraga.db.entity.UserChallengeStatus;
import com.example.fraga.viewmodel.MonthlyChallengeViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MonthlyChallengesFragment extends Fragment {

    private static final String TAG = "MonthlyChallengesFrag";

    // Untuk Tantangan Unggulan
    private MaterialCardView cardViewFeaturedChallenge;
    private TextView textViewFeaturedTitle, textViewFeaturedDesc, textViewFeaturedProgress, textViewFeaturedDaysLeft;
    private ProgressBar progressBarFeatured;
    private MaterialButton buttonJoinFeatured; // Tetap menggunakan ID dari XML

    // Untuk Daftar Tantangan Lainnya
    private RecyclerView recyclerViewMoreChallenges; // Ganti CardView statis dengan RecyclerView
    private MonthlyChallengeAdapter moreChallengesAdapter;

    private MonthlyChallengeViewModel viewModel;
    private int currentUserId = -1;

    // Interface untuk callback dari adapter
    public interface ChallengeInteractionListener {
        void onJoinChallengeClicked(MonthlyChallenge challenge);
        void onViewChallengeDetailsClicked(MonthlyChallenge challenge);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Pastikan layout fragment_monthly_challenges.xml Anda memiliki RecyclerView dengan ID recyclerViewMoreChallenges
        // dan hapus cardViewChallenge1, cardViewChallenge2, cardViewChallenge3 statis dari sana.
        return inflater.inflate(R.layout.fragment_monthly_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        }

        initializeViews(view);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                    .get(MonthlyChallengeViewModel.class);

            setupFeaturedChallengeObserver();
            setupMoreChallengesRecyclerView();
            observeMoreChallenges();
        } else {
            Log.e(TAG, "Activity is null, cannot initialize ViewModel.");
        }
    }

    private void initializeViews(View view) {
        cardViewFeaturedChallenge = view.findViewById(R.id.cardViewFeaturedChallenge);
        textViewFeaturedTitle = view.findViewById(R.id.textViewFeaturedTitle);
        textViewFeaturedDesc = view.findViewById(R.id.textViewFeaturedDesc);
        progressBarFeatured = view.findViewById(R.id.progressBarFeatured);
        textViewFeaturedProgress = view.findViewById(R.id.textViewFeaturedProgress);
        textViewFeaturedDaysLeft = view.findViewById(R.id.textViewFeaturedDaysLeft);
        buttonJoinFeatured = view.findViewById(R.id.buttonJoinFeatured);

        // Inisialisasi RecyclerView untuk "More Challenges"
        // Pastikan ID ini ada di fragment_monthly_challenges.xml
        recyclerViewMoreChallenges = view.findViewById(R.id.recyclerViewMoreChallenges);
    }

    private void setupFeaturedChallengeObserver() {
        if (viewModel == null || cardViewFeaturedChallenge == null) return;

        viewModel.getFeaturedChallenge().observe(getViewLifecycleOwner(), featuredChallenge -> {
            if (featuredChallenge != null) {
                cardViewFeaturedChallenge.setVisibility(View.VISIBLE);
                updateFeaturedChallengeUI(featuredChallenge);

                // Amati juga status pengguna untuk tantangan unggulan ini
                viewModel.getUserStatusForChallenge(featuredChallenge.getId()).observe(getViewLifecycleOwner(), status -> {
                    updateFeaturedChallengeButton(featuredChallenge, status);
                });

                cardViewFeaturedChallenge.setOnClickListener(v -> {
                    // Navigasi ke detail tantangan
                    // Toast.makeText(getContext(), "Viewing " + featuredChallenge.getTitle(), Toast.LENGTH_SHORT).show();
                    onChallengeAction(featuredChallenge, "view_details");
                });

            } else {
                cardViewFeaturedChallenge.setVisibility(View.GONE);
                Log.d(TAG, "No featured challenge available.");
            }
        });
    }

    private void updateFeaturedChallengeUI(MonthlyChallenge challenge) {
        if (textViewFeaturedTitle != null) textViewFeaturedTitle.setText(challenge.getTitle());
        if (textViewFeaturedDesc != null) textViewFeaturedDesc.setText(challenge.getDescription());
        if (textViewFeaturedDaysLeft != null) textViewFeaturedDaysLeft.setText(challenge.getDaysLeft());

        // Logika progres (perlu UserChallengeStatus)
        // Untuk sekarang, kita set ke nilai default atau ambil dari challenge jika ada progres global
        if (progressBarFeatured != null) progressBarFeatured.setProgress(0); // Default
        if (textViewFeaturedProgress != null) textViewFeaturedProgress.setText("0 / " + challenge.getTargetValueDouble() + " " + challenge.getUnit()); // Sesuaikan target
    }

    private void updateFeaturedChallengeButton(MonthlyChallenge challenge, @Nullable UserChallengeStatus status) {
        if (buttonJoinFeatured == null) return;

        if (status != null && ("JOINED".equals(status.getStatus()) || "IN_PROGRESS".equals(status.getStatus()) || "COMPLETED".equals(status.getStatus()))) {
            buttonJoinFeatured.setText(getString(R.string.view_progress_button)); // Anda perlu string ini
            buttonJoinFeatured.setEnabled(true);
            buttonJoinFeatured.setOnClickListener(v -> {
                // Navigasi ke detail tantangan/progres
                // Toast.makeText(getContext(), "Viewing progress for " + challenge.getTitle(), Toast.LENGTH_SHORT).show();
                onChallengeAction(challenge, "view_details");
            });
        } else { // Belum join
            buttonJoinFeatured.setText(getString(R.string.join_challenge_button)); // Anda perlu string ini
            buttonJoinFeatured.setEnabled(true);
            buttonJoinFeatured.setOnClickListener(v -> {
                if (currentUserId != -1) {
                    viewModel.joinChallenge(challenge.getId());
                    // Tombol akan update otomatis karena observasi UserChallengeStatus
                } else {
                    Toast.makeText(getContext(), "Please login to join challenges.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void setupMoreChallengesRecyclerView() {
        if (recyclerViewMoreChallenges != null && getContext() != null) {
            recyclerViewMoreChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
            moreChallengesAdapter = new MonthlyChallengeAdapter(getContext(), currentUserId, new ChallengeInteractionListener() {
                @Override
                public void onJoinChallengeClicked(MonthlyChallenge challenge) {
                    if (currentUserId != -1 && viewModel != null) {
                        viewModel.joinChallenge(challenge.getId());
                    } else if (currentUserId == -1) {
                        Toast.makeText(getContext(), "Please login to join challenges.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onViewChallengeDetailsClicked(MonthlyChallenge challenge) {
                    // Toast.makeText(getContext(), "View details for " + challenge.getTitle(), Toast.LENGTH_SHORT).show();
                    onChallengeAction(challenge, "view_details");
                }
            });
            recyclerViewMoreChallenges.setAdapter(moreChallengesAdapter);
        } else {
            Log.e(TAG, "recyclerViewMoreChallenges is null. 'More Challenges' list will not be displayed.");
        }
    }

    private void observeMoreChallenges() {
        if (viewModel == null || moreChallengesAdapter == null) return;

        viewModel.getActiveChallenges().observe(getViewLifecycleOwner(), challenges -> {
            if (challenges != null) {
                Log.d(TAG, "Active challenges updated: " + challenges.size());
                // Untuk setiap challenge, kita mungkin perlu mengambil status join user secara terpisah
                // atau adapter yang akan menanganinya dengan memanggil ViewModel.
                // Untuk ListAdapter, kita langsung submit list MonthlyChallenge.
                // Adapter akan meng-handle tampilan tombol "Join/View Progress" dengan meng-observe UserChallengeStatus per item.
                moreChallengesAdapter.submitList(challenges);
            } else {
                moreChallengesAdapter.submitList(new ArrayList<>());
            }
        });
    }

    // Metode untuk menangani aksi dari adapter atau tombol featured challenge
    private void onChallengeAction(MonthlyChallenge challenge, String action) {
        if (action.equals("view_details")) {
            Toast.makeText(getContext(), "Navigate to details for: " + challenge.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Buat dan navigasi ke ChallengeDetailActivity
            // Intent intent = new Intent(getActivity(), ChallengeDetailActivity.class);
            // intent.putExtra("CHALLENGE_ID", challenge.getId());
            // startActivity(intent);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Tidak ada executor yang dikelola langsung di sini
    }
}

// Anda perlu membuat MonthlyChallengeAdapter.java
// Berikut adalah kerangka dasarnya:

// File: app/src/main/java/com/example/fraga/adapter/MonthlyChallengeAdapter.java
/*
package com.example.fraga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Atau MaterialButton
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner; // Diperlukan untuk observe LiveData di ViewHolder
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fraga.MonthlyChallengesFragment; // Untuk interface listener
import com.example.fraga.R;
import com.example.fraga.db.entity.MonthlyChallenge;
import com.example.fraga.db.entity.UserChallengeStatus;
import com.example.fraga.viewmodel.MonthlyChallengeViewModel; // Untuk mendapatkan status per item

import java.util.Objects;

public class MonthlyChallengeAdapter extends ListAdapter<MonthlyChallenge, MonthlyChallengeAdapter.ChallengeViewHolder> {

    private Context context;
    private int currentUserId;
    private MonthlyChallengesFragment.ChallengeInteractionListener listener;
    private MonthlyChallengeViewModel viewModel; // Diperlukan untuk observe status per item
    private LifecycleOwner lifecycleOwner; // Diperlukan untuk observe LiveData di ViewHolder

    public MonthlyChallengeAdapter(Context context, int currentUserId, MonthlyChallengesFragment.ChallengeInteractionListener listener, MonthlyChallengeViewModel viewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Buat layout item_monthly_challenge.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        MonthlyChallenge challenge = getItem(position);
        if (challenge == null) return;

        holder.textViewTitle.setText(challenge.getTitle());
        holder.textViewDesc.setText(challenge.getDescription());
        // TODO: Muat gambar badge dengan Glide: holder.imageViewBadge
        // holder.imageViewBadge.setImageResource(R.drawable.challenge_badge_placeholder);

        // Progres dan tombol join/view
        // Amati status pengguna untuk tantangan ini
        viewModel.getUserStatusForChallenge(challenge.getId()).observe(lifecycleOwner, status -> {
            if (status != null && ("JOINED".equals(status.getStatus()) || "IN_PROGRESS".equals(status.getStatus()) || "COMPLETED".equals(status.getStatus()))) {
                holder.buttonJoin.setText("View Progress"); // Atau sesuai status
                holder.buttonJoin.setEnabled(true);
                holder.buttonJoin.setOnClickListener(v -> listener.onViewChallengeDetailsClicked(challenge));
                // Tampilkan progres jika ada
                // holder.progressBar.setProgress(...);
                // holder.textViewProgress.setText(...);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.textViewProgress.setVisibility(View.VISIBLE);

            } else { // Belum join atau status lain
                holder.buttonJoin.setText("Join");
                holder.buttonJoin.setEnabled(true);
                holder.buttonJoin.setOnClickListener(v -> listener.onJoinChallengeClicked(challenge));
                holder.progressBar.setVisibility(View.GONE);
                holder.textViewProgress.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onViewChallengeDetailsClicked(challenge));
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBadge; // Asumsi ada ImageView untuk badge di item_monthly_challenge.xml
        TextView textViewTitle, textViewDesc, textViewProgress;
        ProgressBar progressBar;
        Button buttonJoin; // Atau MaterialButton

        ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dari item_monthly_challenge.xml
            // imageViewBadge = itemView.findViewById(R.id.item_challenge_badge);
            // textViewTitle = itemView.findViewById(R.id.item_challenge_title);
            // textViewDesc = itemView.findViewById(R.id.item_challenge_desc);
            // textViewProgress = itemView.findViewById(R.id.item_challenge_progress_text);
            // progressBar = itemView.findViewById(R.id.item_challenge_progress_bar);
            // buttonJoin = itemView.findViewById(R.id.item_challenge_button_join);
        }
    }

    public static final DiffUtil.ItemCallback<MonthlyChallenge> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MonthlyChallenge>() {
        @Override
        public boolean areItemsTheSame(@NonNull MonthlyChallenge oldItem, @NonNull MonthlyChallenge newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MonthlyChallenge oldItem, @NonNull MonthlyChallenge newItem) {
            return oldItem.equals(newItem); // Membutuhkan equals() di MonthlyChallenge.java
        }
    };
}
*/