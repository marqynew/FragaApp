package com.example.fraga;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar; // Atau LinearProgressIndicator
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraga.db.entity.PersonalGoal;
import com.example.fraga.viewmodel.PersonalGoalViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PersonalGoalsFragment extends Fragment {

    private static final String TAG = "PersonalGoalsFragment";

    private RecyclerView recyclerViewCurrentGoals;
    private RecyclerView recyclerViewCompletedGoals;
    private PersonalGoalAdapter currentGoalsAdapter;
    private PersonalGoalAdapter completedGoalsAdapter;
    private PersonalGoalViewModel personalGoalViewModel;

    // Views untuk template, listenernya akan tetap
    private MaterialCardView cardViewTemplate1, cardViewTemplate2, cardViewTemplate3, cardViewTemplate4;

    private int currentUserId = -1;

    // Interface untuk callback dari adapter
    public interface GoalInteractionListener {
        void onEditGoal(PersonalGoal goal);
        void onLogProgress(PersonalGoal goal); // Atau onSyncProgress
        void onViewGoalDetail(PersonalGoal goal);
        void onShareGoal(PersonalGoal goal); // Untuk completed goal
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        }

        // Inisialisasi RecyclerView (Anda perlu menambahkan ini ke fragment_personal_goals.xml)
        // Misalnya:
        // recyclerViewCurrentGoals = view.findViewById(R.id.recyclerViewCurrentGoals);
        // recyclerViewCompletedGoals = view.findViewById(R.id.recyclerViewCompletedGoals);
        // Jika ID belum ada, build akan error. Untuk sekarang, kita fokus pada logika Java.
        // Untuk pengujian, Anda bisa mengomentari bagian RecyclerView jika XML belum siap.

        // Hapus inisialisasi CardView statis untuk tujuan dari initializeViews jika Anda akan menggunakan RecyclerView
        // Setup Goal Templates Listeners (ini tetap karena template statis)
        cardViewTemplate1 = view.findViewById(R.id.cardViewTemplate1);
        cardViewTemplate2 = view.findViewById(R.id.cardViewTemplate2);
        cardViewTemplate3 = view.findViewById(R.id.cardViewTemplate3);
        cardViewTemplate4 = view.findViewById(R.id.cardViewTemplate4);
        setupGoalTemplatesListeners();


        if (getActivity() != null) {
            personalGoalViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                    .get(PersonalGoalViewModel.class);

            setupRecyclerViews(); // Panggil setup RecyclerView di sini

            observeViewModel();
        } else {
            Log.e(TAG, "Activity is null, cannot initialize ViewModel.");
        }
    }

    private void setupRecyclerViews() {
        GoalInteractionListener listener = new GoalInteractionListener() {
            @Override
            public void onEditGoal(PersonalGoal goal) {
                Toast.makeText(getContext(), "Edit goal: " + goal.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Navigasi ke layar edit goal dengan goal.getId()
            }

            @Override
            public void onLogProgress(PersonalGoal goal) {
                // Contoh: Simulasikan penambahan progres
                // Logika ini bisa lebih kompleks tergantung goalType
                Toast.makeText(getContext(), "Logging progress for: " + goal.getTitle(), Toast.LENGTH_SHORT).show();
                if (goal.getUnit().equals("times") || goal.getUnit().equals("days")) {
                    personalGoalViewModel.updateGoalProgress(goal.getId(), 0, 1, false); // Tambah 1
                } else if (goal.getUnit().equals("km") || goal.getUnit().equals("steps")) {
                    // Untuk jarak/langkah, Anda mungkin perlu dialog untuk memasukkan progres
                    // Untuk contoh, kita tambahkan sejumlah progres dummy
                    personalGoalViewModel.updateGoalProgress(goal.getId(), 1.0, 0, true); // Tambah 1.0
                }
            }

            @Override
            public void onViewGoalDetail(PersonalGoal goal) {
                Toast.makeText(getContext(), "View details for: " + goal.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Navigasi ke layar detail goal jika ada
            }
            @Override
            public void onShareGoal(PersonalGoal goal) {
                Toast.makeText(getContext(), "Sharing completed goal: " + goal.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Implementasi sharing
            }
        };

        // Setup RecyclerView untuk Current Goals
        // Anda perlu menambahkan RecyclerView dengan ID recyclerViewCurrentGoals di XML Anda
        View currentGoalsRecyclerViewPlaceholder = getView().findViewById(R.id.cardViewGoal1); // Ganti dengan ID RecyclerView yang benar
        if (currentGoalsRecyclerViewPlaceholder instanceof RecyclerView) {
            recyclerViewCurrentGoals = (RecyclerView) currentGoalsRecyclerViewPlaceholder;
            recyclerViewCurrentGoals.setLayoutManager(new LinearLayoutManager(getContext()));
            currentGoalsAdapter = new PersonalGoalAdapter(getContext(), listener, false); // false untuk bukan completed goals
            recyclerViewCurrentGoals.setAdapter(currentGoalsAdapter);
        } else if (currentGoalsRecyclerViewPlaceholder != null) {
            Log.w(TAG, "recyclerViewCurrentGoals (or its placeholder ID) not found or not a RecyclerView. Current goals list will not be dynamic.");
            // Sembunyikan CardView statis jika Anda beralih ke RecyclerView
            getView().findViewById(R.id.cardViewGoal1).setVisibility(View.GONE);
            getView().findViewById(R.id.cardViewGoal2).setVisibility(View.GONE);
        }


        // Setup RecyclerView untuk Completed Goals
        // Anda perlu menambahkan RecyclerView dengan ID recyclerViewCompletedGoals di XML Anda
        View completedGoalsRecyclerViewPlaceholder = getView().findViewById(R.id.cardViewCompletedGoal); // Ganti dengan ID RecyclerView yang benar
        if (completedGoalsRecyclerViewPlaceholder instanceof RecyclerView) {
            recyclerViewCompletedGoals = (RecyclerView) completedGoalsRecyclerViewPlaceholder;
            recyclerViewCompletedGoals.setLayoutManager(new LinearLayoutManager(getContext()));
            completedGoalsAdapter = new PersonalGoalAdapter(getContext(), listener, true); // true untuk completed goals
            recyclerViewCompletedGoals.setAdapter(completedGoalsAdapter);
        } else if (completedGoalsRecyclerViewPlaceholder != null){
            Log.w(TAG, "recyclerViewCompletedGoals (or its placeholder ID) not found or not a RecyclerView. Completed goals list will not be dynamic.");
            // Sembunyikan CardView statis jika Anda beralih ke RecyclerView
            getView().findViewById(R.id.cardViewCompletedGoal).setVisibility(View.GONE);
        }
    }


    private void observeViewModel() {
        if (personalGoalViewModel == null) return;

        personalGoalViewModel.getInProgressGoals().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null && currentGoalsAdapter != null) {
                Log.d(TAG, "In-progress goals updated: " + goals.size());
                currentGoalsAdapter.submitList(goals);
            } else if (currentGoalsAdapter != null) {
                currentGoalsAdapter.submitList(new ArrayList<>());
            }
        });

        personalGoalViewModel.getCompletedGoals().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null && completedGoalsAdapter != null) {
                Log.d(TAG, "Completed goals updated: " + goals.size());
                completedGoalsAdapter.submitList(goals);
            } else if (completedGoalsAdapter != null) {
                completedGoalsAdapter.submitList(new ArrayList<>());
            }
        });
    }


    private void setupGoalTemplatesListeners() {
        // Listener ini akan tetap, tapi logika di showTemplateConfirmation akan memanggil ViewModel
        if (cardViewTemplate1 != null) cardViewTemplate1.setOnClickListener(v -> showTemplateConfirmation("Run a Marathon", "This will set up a 16-week marathon training plan. Ready to start?", "template_marathon"));
        if (cardViewTemplate2 != null) cardViewTemplate2.setOnClickListener(v -> showTemplateConfirmation("Cycle 1000km", "This will set up a monthly 1000km cycling challenge. Ready to start?", "template_cycle_1000km"));
        if (cardViewTemplate3 != null) cardViewTemplate3.setOnClickListener(v -> showTemplateConfirmation("30-Day Streak", "This will set up a 30-day workout streak challenge. Ready to start?", "template_30day_streak"));
        if (cardViewTemplate4 != null) cardViewTemplate4.setOnClickListener(v -> showTemplateConfirmation("Improve 5K Time", "This will set up an 8-week plan to improve your 5K time. Ready to start?", "template_improve_5k"));
    }

    private void showTemplateConfirmation(String title, String message, final String templateId) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Start", (dialog, which) -> {
                    if (currentUserId == -1) {
                        Toast.makeText(getContext(), "Please login to set goals.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (personalGoalViewModel != null) {
                        PersonalGoal newGoal = createGoalFromTemplate(templateId, currentUserId, title); // Title dari dialog
                        if (newGoal != null) {
                            personalGoalViewModel.insertGoal(newGoal);
                            Toast.makeText(getContext(), "Goal '" + title + "' started!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Could not create goal from template.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Metode helper untuk membuat objek PersonalGoal dari template
    private PersonalGoal createGoalFromTemplate(String templateId, int userId, String defaultTitle) {
        PersonalGoal goal = new PersonalGoal();
        goal.setUserId(userId);
        goal.setCreationDateMs(System.currentTimeMillis());
        goal.setStatus("in_progress");
        goal.setCurrentProgressDouble(0.0);
        goal.setCurrentProgressInt(0);
        goal.setTitle(defaultTitle); // Gunakan title dari parameter

        // Logika spesifik untuk setiap template
        switch (templateId) {
            case "template_marathon":
                goal.setDescription("Complete a full marathon by following the 16-week training plan.");
                goal.setGoalType("marathon_training_weeks"); // Tipe spesifik
                goal.setTargetValueInt(16); // Target 16 minggu
                goal.setUnit("weeks");
                // targetDateMs bisa dihitung: now + 16 weeks
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_YEAR, 16);
                goal.setTargetDateMs(cal.getTimeInMillis());
                break;
            case "template_cycle_1000km":
                goal.setDescription("Accumulate 1000 kilometers of cycling distance within the current month.");
                goal.setGoalType("distance_cycle_monthly");
                goal.setTargetValueDouble(1000.0);
                goal.setUnit("km");
                Calendar calMonth = Calendar.getInstance();
                calMonth.set(Calendar.DAY_OF_MONTH, calMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                goal.setTargetDateMs(calMonth.getTimeInMillis()); // Akhir bulan ini
                break;
            case "template_30day_streak":
                goal.setDescription("Exercise for at least 20 minutes for 30 days in a row.");
                goal.setGoalType("daily_streak_days");
                goal.setTargetValueInt(30);
                goal.setUnit("days");
                Calendar calStreak = Calendar.getInstance();
                calStreak.add(Calendar.DAY_OF_YEAR, 30);
                goal.setTargetDateMs(calStreak.getTimeInMillis());
                break;
            case "template_improve_5k":
                goal.setDescription("Follow an 8-week plan to improve your 5K time.");
                goal.setGoalType("improve_5k_time_weeks");
                goal.setTargetValueInt(8); // 8 minggu
                goal.setUnit("weeks");
                Calendar cal5k = Calendar.getInstance();
                cal5k.add(Calendar.WEEK_OF_YEAR, 8);
                goal.setTargetDateMs(cal5k.getTimeInMillis());
                break;
            default:
                Log.e(TAG, "Unknown templateId: " + templateId);
                return null; // Template tidak dikenal
        }
        return goal;
    }


    // Adapter untuk PersonalGoal (menggunakan ListAdapter)
    static class PersonalGoalAdapter extends ListAdapter<PersonalGoal, PersonalGoalAdapter.GoalViewHolder> {
        private Context context;
        private GoalInteractionListener listener;
        private boolean isCompletedList; // Untuk membedakan tampilan/aksi

        protected PersonalGoalAdapter(Context context, GoalInteractionListener listener, boolean isCompletedList) {
            super(DIFF_CALLBACK);
            this.context = context;
            this.listener = listener;
            this.isCompletedList = isCompletedList;
        }

        @NonNull
        @Override
        public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Gunakan layout item yang sama dengan yang ada di fragment_personal_goals.xml (cardViewGoal1/cardViewGoal2)
            // atau buat layout item_personal_goal.xml baru yang lebih generik.
            // Untuk sekarang, kita asumsikan Anda akan membuat R.layout.item_personal_goal
            // Jika tidak, Anda perlu menyesuaikan ViewHolder dan onBindViewHolder.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_goal, parent, false);
            return new GoalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
            PersonalGoal goal = getItem(position);
            if (goal == null) return;

            holder.textViewTitle.setText(goal.getTitle());
            holder.textViewDesc.setText(goal.getDescription());
            holder.textViewProgress.setText(goal.getFormattedProgress());
            holder.progressIndicator.setProgressCompat(goal.getProgressPercentage(), true);
            holder.textViewStatus.setText(goal.getStatus().toUpperCase(Locale.ROOT));

            if (isCompletedList) {
                holder.buttonEdit.setVisibility(View.GONE);
                holder.buttonLogProgress.setVisibility(View.GONE);
                holder.buttonShare.setVisibility(View.VISIBLE);
                holder.buttonShare.setOnClickListener(v -> listener.onShareGoal(goal));
                holder.textViewStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.success_light)); // Contoh warna
                holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
            } else { // In-progress goals
                holder.buttonEdit.setVisibility(View.VISIBLE);
                holder.buttonLogProgress.setVisibility(View.VISIBLE);
                holder.buttonShare.setVisibility(View.GONE);
                holder.buttonEdit.setOnClickListener(v -> listener.onEditGoal(goal));
                holder.buttonLogProgress.setOnClickListener(v -> listener.onLogProgress(goal));
                // Atur warna status berdasarkan progres
                if (goal.getProgressPercentage() > 80) { // Contoh "Almost There"
                    holder.textViewStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.accent_light));
                    holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.accent));
                } else {
                    holder.textViewStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                    holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.primary));
                }
            }
            holder.itemView.setOnClickListener(v -> listener.onViewGoalDetail(goal));
        }

        static class GoalViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTitle, textViewDesc, textViewProgress, textViewStatus;
            LinearProgressIndicator progressIndicator;
            MaterialButton buttonEdit, buttonLogProgress, buttonShare;

            GoalViewHolder(@NonNull View itemView) {
                super(itemView);
                // Inisialisasi view dari R.layout.item_personal_goal
                textViewTitle = itemView.findViewById(R.id.item_goal_title);
                textViewDesc = itemView.findViewById(R.id.item_goal_description);
                textViewProgress = itemView.findViewById(R.id.item_goal_progress_text);
                textViewStatus = itemView.findViewById(R.id.item_goal_status);
                progressIndicator = itemView.findViewById(R.id.item_goal_progress_indicator);
                buttonEdit = itemView.findViewById(R.id.item_goal_button_edit);
                buttonLogProgress = itemView.findViewById(R.id.item_goal_button_log_progress);
                buttonShare = itemView.findViewById(R.id.item_goal_button_share); // Tombol share untuk completed
            }
        }

        public static final DiffUtil.ItemCallback<PersonalGoal> DIFF_CALLBACK =
                new DiffUtil.ItemCallback<PersonalGoal>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull PersonalGoal oldItem, @NonNull PersonalGoal newItem) {
                        return oldItem.getId() == newItem.getId();
                    }

                    @SuppressLint("DiffUtilEquals")
                    @Override
                    public boolean areContentsTheSame(@NonNull PersonalGoal oldItem, @NonNull PersonalGoal newItem) {
                        return oldItem.equals(newItem); // Membutuhkan equals() di PersonalGoal.java
                    }
                };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Tidak ada ExecutorService yang dikelola di sini lagi
    }
}