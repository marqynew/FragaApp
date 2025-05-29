package com.example.fraga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.ActivityLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackingFragment extends Fragment {

    private static final String TAG = "TrackingFragment";
    private RadioGroup radioGroupActivityType;
    private RadioButton radioButtonRunning, radioButtonCycling, radioButtonSwimming;
    private TextView textViewDistance, textViewDuration, textViewPace;
    private Button buttonStartTracking;
    private Button buttonCreateActivity;
    private ImageView imageViewMap;
    private boolean isTracking = false;

    private long startTimeMillis = 0;
    private long activityStartWallClockTimeMs = 0;
    private String selectedActivityType = "Running"; // Default

    private AppDatabase appDb;
    private ExecutorService databaseWriteExecutor;
    private int currentUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) {
            Log.e(TAG, "Context is null in onViewCreated. Fragment may not be attached correctly.");
            return;
        }

        appDb = AppDatabase.getInstance(getContext().getApplicationContext());
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Log.w(TAG, "Current User ID not found. Tracking will not save correctly and tracking button might be disabled.");
        }

        initializeViews(view);
        setupListeners();

        // Nonaktifkan tombol start jika tidak ada user ID
        if (buttonStartTracking != null) {
            buttonStartTracking.setEnabled(currentUserId != -1);
            if(currentUserId == -1) {
                Toast.makeText(getContext(), "Please login to use tracking features.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeViews(View view) {
        radioGroupActivityType = view.findViewById(R.id.radioGroupActivityType);
        radioButtonRunning = view.findViewById(R.id.radioButtonRunning);
        radioButtonCycling = view.findViewById(R.id.radioButtonCycling);
        radioButtonSwimming = view.findViewById(R.id.radioButtonSwimming);
        textViewDistance = view.findViewById(R.id.textViewDistance);
        textViewDuration = view.findViewById(R.id.textViewDuration);
        textViewPace = view.findViewById(R.id.textViewPace);
        buttonStartTracking = view.findViewById(R.id.buttonStartTracking);
        buttonCreateActivity = view.findViewById(R.id.buttonCreateActivity);
        imageViewMap = view.findViewById(R.id.imageViewMap);

        // Set default activity type
        if (radioButtonRunning != null && radioButtonRunning.isChecked()) {
            selectedActivityType = getString(R.string.running); // Gunakan string resource
        } else if (radioButtonCycling != null && radioButtonCycling.isChecked()) {
            selectedActivityType = getString(R.string.cycling);
        } else if (radioButtonSwimming != null && radioButtonSwimming.isChecked()) {
            selectedActivityType = getString(R.string.swimming);
        } else if (radioButtonRunning != null) { // Default jika tidak ada yang ter-check
            radioButtonRunning.setChecked(true);
            selectedActivityType = getString(R.string.running);
        }
    }

    private void setupListeners() {
        if (buttonStartTracking != null) {
            buttonStartTracking.setOnClickListener(v -> {
                if (currentUserId == -1) {
                    Toast.makeText(getContext(), "Please login to track activities.", Toast.LENGTH_SHORT).show();
                    return;
                }
                toggleTracking();
            });
        }

        if (buttonCreateActivity != null) {
            buttonCreateActivity.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), CreateActivityActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (radioGroupActivityType != null) {
            radioGroupActivityType.setOnCheckedChangeListener((group, checkedId) -> {
                if (isTracking) {
                    Toast.makeText(getContext(), "Stop current tracking to change activity type.", Toast.LENGTH_LONG).show();
                    // Kembalikan pilihan ke tipe yang sedang dilacak
                    if (selectedActivityType.equals(getString(R.string.running)) && radioButtonRunning != null) radioButtonRunning.setChecked(true);
                    else if (selectedActivityType.equals(getString(R.string.cycling)) && radioButtonCycling != null) radioButtonCycling.setChecked(true);
                    else if (selectedActivityType.equals(getString(R.string.swimming)) && radioButtonSwimming != null) radioButtonSwimming.setChecked(true);
                    return;
                }
                resetStats();
                if (radioButtonRunning != null && checkedId == radioButtonRunning.getId()) selectedActivityType = getString(R.string.running);
                else if (radioButtonCycling != null && checkedId == radioButtonCycling.getId()) selectedActivityType = getString(R.string.cycling);
                else if (radioButtonSwimming != null && checkedId == radioButtonSwimming.getId()) selectedActivityType = getString(R.string.swimming);
                Log.d(TAG, "Activity type set to: " + selectedActivityType);
            });
        }
    }

    private void toggleTracking() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null in toggleTracking. Cannot proceed.");
            return;
        }
        isTracking = !isTracking;

        if (isTracking) {
            buttonStartTracking.setText(getString(R.string.stop_tracking));
            buttonStartTracking.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error));
            disableActivityTypeSelection(true);

            startTimeMillis = SystemClock.elapsedRealtime();
            activityStartWallClockTimeMs = System.currentTimeMillis();
            resetStats();
            // TODO: Implementasi startTimerUpdates() untuk update UI durasi secara live
            Log.d(TAG, "Tracking started for " + selectedActivityType + " by user ID: " + currentUserId);
            Toast.makeText(getContext(), "Tracking started: " + selectedActivityType, Toast.LENGTH_SHORT).show();
            // TODO: Mulai layanan GPS/sensor di sini. Update UI jarak dan pace secara berkala.

        } else { // Hentikan Tracking
            // TODO: Implementasi stopTimerUpdates()
            buttonStartTracking.setText(getString(R.string.start_tracking));
            buttonStartTracking.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.success));
            disableActivityTypeSelection(false);

            long durationTrackedMs = SystemClock.elapsedRealtime() - startTimeMillis;

            // **PENTING**: Ambil data aktual jarak. Untuk sekarang, kita hardcode atau biarkan 0.
            // Di aplikasi nyata, ini akan berasal dari sensor GPS.
            double distanceKmTracked = 0.0; // Default dummy
            // Contoh jika ingin parse dari TextView (kurang ideal untuk data live)
            if (textViewDistance != null && !TextUtils.isEmpty(textViewDistance.getText())) {
                try {
                    String distanceText = textViewDistance.getText().toString().toLowerCase().replace("km", "").trim();
                    if (!TextUtils.isEmpty(distanceText)) {
                        distanceKmTracked = Double.parseDouble(distanceText);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Could not parse distance from TextView for saving: " + textViewDistance.getText().toString(), e);
                }
            }

            // Validasi sederhana sebelum menyimpan
            if (durationTrackedMs < 5000 && distanceKmTracked < 0.01) { // Min 5 detik dan ada sedikit jarak
                Toast.makeText(getContext(), "Activity too short, not saved.", Toast.LENGTH_LONG).show();
                resetStats();
                return;
            }

            Log.d(TAG, "Tracking stopped. Type: " + selectedActivityType + ", Distance: " + distanceKmTracked + "km, Duration (ms): " + durationTrackedMs);
            saveTrackedActivity(selectedActivityType, distanceKmTracked, durationTrackedMs, activityStartWallClockTimeMs);
        }
    }

    private void disableActivityTypeSelection(boolean disable) {
        if (radioGroupActivityType != null) {
            for (int i = 0; i < radioGroupActivityType.getChildCount(); i++) {
                View child = radioGroupActivityType.getChildAt(i);
                if (child instanceof RadioButton) {
                    child.setEnabled(!disable);
                }
            }
        }
    }

    private void saveTrackedActivity(String type, double distance, long durationMs, long activityStartTime) {
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Cannot save activity: User not logged in.", Toast.LENGTH_LONG).show();
            return;
        }
        if (getContext() == null || appDb == null) {
            Log.e(TAG, "Cannot save activity: context or appDb is null.");
            Toast.makeText(getContext(), "Error: Cannot save activity.", Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityLog newActivityLog = new ActivityLog();
        newActivityLog.setUserId(currentUserId);
        newActivityLog.setActivityType(type);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        newActivityLog.setTitle(type + " on " + sdf.format(new Date(activityStartTime)));
        newActivityLog.setDescription("Tracked live via FragaApp.");

        newActivityLog.setTimestampMs(activityStartTime);
        newActivityLog.setDurationMs(durationMs);
        newActivityLog.setDistanceKm(distance);

        // Pace akan dihitung dan ditampilkan oleh metode getFormattedPace() di ActivityLog
        // Jika ingin menyimpan nilai pace yang sudah dihitung:
        // if (distance > 0 && durationMs > 0) {
        //     double paceSecondsPerKm = (double) durationMs / 1000.0 / distance;
        //     newActivityLog.setPaceMinPerKm(paceSecondsPerKm / 60.0);
        // }

        newActivityLog.setCaloriesKcal(0); // Placeholder
        newActivityLog.setAvgHeartRateBpm(0); // Placeholder
        newActivityLog.setMaxHeartRateBpm(0); // Placeholder
        newActivityLog.setElevationGainM(0); // Placeholder
        newActivityLog.setRouteDataJson(null); // Placeholder
        newActivityLog.setKudosCount(0);
        newActivityLog.setPrivacySetting("Public"); // Default, atau ambil dari preferensi
        newActivityLog.setStatus("COMPLETED");

        Log.d(TAG, "Saving tracked activity: Title=" + newActivityLog.getTitle() + ", Distance=" + newActivityLog.getDistanceKm());

        databaseWriteExecutor.execute(() -> {
            long newId = -1;
            try {
                newId = appDb.activityLogDao().insertActivityLog(newActivityLog);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting tracked activity into database", e);
            }
            final long finalNewId = newId; // Perlu final untuk diakses di runOnUiThread

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (finalNewId > 0) {
                        Toast.makeText(getContext(), type + " activity saved! ID: " + finalNewId, Toast.LENGTH_SHORT).show();
                        resetStats();
                        // Opsional: Navigasi ke ActivityDetailActivity
                        // Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
                        // intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY_ID, (int) finalNewId);
                        // startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Failed to save tracked activity.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void resetStats() {
        if (textViewDistance != null) textViewDistance.setText(R.string.default_distance_tracking); // Gunakan string resource
        if (textViewDuration != null) textViewDuration.setText(R.string.default_duration_tracking); // Gunakan string resource
        if (textViewPace != null) textViewPace.setText(R.string.default_pace_tracking);         // Gunakan string resource
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseWriteExecutor != null && !databaseWriteExecutor.isShutdown()) {
            databaseWriteExecutor.shutdown();
        }
        Log.d(TAG, "TrackingFragment: onDestroyView called, executor shutdown.");
    }
}