package com.example.fraga.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Atau com.google.android.material.button.MaterialButton
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
// import androidx.lifecycle.Observer; // Tidak perlu import Observer secara eksplisit jika menggunakan lambda
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

// import com.bumptech.glide.Glide; // Uncomment jika Anda menggunakan Glide
import com.example.fraga.MonthlyChallengesFragment; // Untuk interface listener
import com.example.fraga.R; // Untuk mengakses R.layout.item_monthly_challenge
import com.example.fraga.db.entity.MonthlyChallenge;
import com.example.fraga.db.entity.UserChallengeStatus;
import com.example.fraga.viewmodel.MonthlyChallengeViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MonthlyChallengeAdapter extends ListAdapter<MonthlyChallenge, MonthlyChallengeAdapter.ChallengeViewHolder> {

    private static final String ADAPTER_TAG = "MonthlyChallengeAdapter";
    private Context context;
    private int currentUserId; // Diperlukan untuk logika tombol Join/View
    private MonthlyChallengesFragment.ChallengeInteractionListener listener;
    private MonthlyChallengeViewModel viewModel;
    private LifecycleOwner lifecycleOwner; // Diperlukan untuk meng-observe LiveData di ViewHolder

    public MonthlyChallengeAdapter(@NonNull Context context,
                                   int currentUserId,
                                   @NonNull MonthlyChallengesFragment.ChallengeInteractionListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        MonthlyChallenge challenge = getItem(position);
        if (challenge == null) {
            Log.w(ADAPTER_TAG, "Challenge at position " + position + " is null.");
            // Mungkin sembunyikan item view atau tampilkan state error
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        holder.itemView.setVisibility(View.VISIBLE);

        holder.textViewTitle.setText(challenge.getTitle());
        holder.textViewDesc.setText(challenge.getDescription());
        // TODO: Implementasi Glide untuk memuat gambar badge dari challenge.getBadgeImageUrl()
        // if (challenge.getBadgeImageUrl() != null && !challenge.getBadgeImageUrl().isEmpty() && context != null) {
        //     Glide.with(context).load(challenge.getBadgeImageUrl()).placeholder(R.drawable.challenge_badge_placeholder).into(holder.imageViewBadge);
        // } else if (holder.imageViewBadge != null){
        //     holder.imageViewBadge.setImageResource(R.drawable.challenge_badge_placeholder); // Placeholder default
        // }
        if (holder.imageViewBadge != null) {
            holder.imageViewBadge.setImageResource(R.drawable.ic_launcher_foreground); // Ganti dengan ikon tantangan yang sesuai
        }


        // Mengamati status pengguna untuk tantangan ini dari ViewModel
        viewModel.getUserStatusForChallenge(challenge.getId()).observe(lifecycleOwner, status -> {
            updateChallengeItemUI(holder, challenge, status);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewChallengeDetailsClicked(challenge);
            }
        });
    }

    private void updateChallengeItemUI(@NonNull ChallengeViewHolder holder, @NonNull MonthlyChallenge challenge, @Nullable UserChallengeStatus status) {
        boolean userHasJoined = status != null &&
                ("JOINED".equals(status.getStatus()) ||
                        "IN_PROGRESS".equals(status.getStatus()) ||
                        "COMPLETED".equals(status.getStatus()));

        if (userHasJoined) {
            holder.buttonJoin.setText(context.getString(R.string.view_progress_button));
            holder.buttonJoin.setEnabled(true); // Selalu bisa melihat progres jika sudah join
            holder.buttonJoin.setOnClickListener(v -> {
                if (listener != null) listener.onViewChallengeDetailsClicked(challenge);
            });

            // Tampilkan progres
            if (holder.progressBar != null && holder.textViewProgress != null) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.textViewProgress.setVisibility(View.VISIBLE);
                int progressPercent = 0;
                String progressText = "0 / -- " + challenge.getUnit(); // Default jika target 0

                // Asumsi 'status' tidak null karena userHasJoined true
                if (challenge.getGoalType() != null && challenge.getGoalType().contains("DISTANCE") && challenge.getTargetValueDouble() > 0) {
                    progressPercent = (int) ((status.getCurrentProgressDouble() / challenge.getTargetValueDouble()) * 100);
                    progressText = String.format(Locale.getDefault(), "%.1f / %.1f %s", status.getCurrentProgressDouble(), challenge.getTargetValueDouble(), challenge.getUnit());
                } else if (challenge.getTargetValueInt() > 0) { // Untuk tipe goal "COUNT" atau "DAYS"
                    progressPercent = (int) (((double) status.getCurrentProgressInt() / challenge.getTargetValueInt()) * 100);
                    progressText = String.format(Locale.getDefault(), "%d / %d %s", status.getCurrentProgressInt(), challenge.getTargetValueInt(), challenge.getUnit());
                }
                holder.progressBar.setProgress(Math.min(progressPercent, 100)); // Batasi progres maksimal 100%
                holder.textViewProgress.setText(progressText);
            }
        } else { // Belum join atau status lain (misalnya, FAILED)
            holder.buttonJoin.setText(context.getString(R.string.join_challenge_button));
            holder.buttonJoin.setEnabled(currentUserId != -1); // Hanya bisa join jika sudah login
            holder.buttonJoin.setOnClickListener(v -> {
                if (currentUserId == -1) {
                    Toast.makeText(context, "Please login to join challenges.", Toast.LENGTH_SHORT).show();
                } else if (listener != null) {
                    listener.onJoinChallengeClicked(challenge);
                }
            });
            if (holder.progressBar != null) holder.progressBar.setVisibility(View.GONE);
            if (holder.textViewProgress != null) holder.textViewProgress.setVisibility(View.GONE);
        }
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBadge;
        TextView textViewTitle, textViewDesc, textViewProgress;
        ProgressBar progressBar;
        Button buttonJoin; // Bisa juga MaterialButton

        ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Pastikan ID ini sesuai dengan yang ada di R.layout.item_monthly_challenge.xml Anda
            textViewTitle = itemView.findViewById(R.id.item_challenge_title);
            textViewDesc = itemView.findViewById(R.id.item_challenge_desc);
            textViewProgress = itemView.findViewById(R.id.item_challenge_progress_text);
            progressBar = itemView.findViewById(R.id.item_challenge_progress_bar);
            buttonJoin = itemView.findViewById(R.id.item_challenge_button_join);
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
                    // Pastikan MonthlyChallenge.java memiliki implementasi equals() dan hashCode() yang benar.
                    return oldItem.equals(newItem);
                }
            };
}