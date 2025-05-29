package com.example.fraga;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.ActivityLog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateActivityActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivityActivity";

    private Toolbar toolbar;
    private MaterialCardView cardViewMap;
    private MaterialCardView cardViewRouteLibrary;
    private MaterialCardView cardViewPopularRoutes;
    private MaterialButton buttonStartActivity;

    private ChipGroup chipGroupActivityType;
    private String selectedActivityType = "Running"; // Default

    private RadioGroup radioGroupGoalType;
    private RadioButton radioButtonDistance;
    private RadioButton radioButtonTime;
    private RadioButton radioButtonNoGoal;
    private TextInputLayout textInputLayoutDistance;
    private TextInputEditText editTextDistance;
    private TextInputLayout textInputLayoutTime;
    private TextInputEditText editTextTime;

    private RadioGroup radioGroupSchedule;
    private RadioButton radioButtonStartNow;
    private RadioButton radioButtonSchedule;
    private LinearLayout layoutDateTime;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextScheduleTime;
    private CheckBox checkBoxReminder;

    private RadioGroup radioGroupPrivacy;

    private Calendar selectedDateTime;
    private AppDatabase appDb;
    private ExecutorService databaseWriteExecutor;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        appDb = AppDatabase.getInstance(getApplicationContext());
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in. Please login to create an activity.", Toast.LENGTH_LONG).show();
            // Pertimbangkan untuk menutup activity jika tidak ada user
            // finish();
            // return;
        }

        initializeViews();
        setupToolbar();
        setupInteractions();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        cardViewMap = findViewById(R.id.cardViewMap);
        cardViewRouteLibrary = findViewById(R.id.cardViewRouteLibrary);
        cardViewPopularRoutes = findViewById(R.id.cardViewPopularRoutes);
        buttonStartActivity = findViewById(R.id.buttonStartActivity);
        chipGroupActivityType = findViewById(R.id.chipGroupActivityType);
        radioGroupGoalType = findViewById(R.id.radioGroupGoalType);
        radioButtonDistance = findViewById(R.id.radioButtonDistance);
        radioButtonTime = findViewById(R.id.radioButtonTime);
        radioButtonNoGoal = findViewById(R.id.radioButtonNoGoal);
        textInputLayoutDistance = findViewById(R.id.textInputLayoutDistance);
        editTextDistance = findViewById(R.id.editTextDistance);
        textInputLayoutTime = findViewById(R.id.textInputLayoutTime);
        editTextTime = findViewById(R.id.editTextTime);
        radioGroupSchedule = findViewById(R.id.radioGroupSchedule);
        radioButtonStartNow = findViewById(R.id.radioButtonStartNow);
        radioButtonSchedule = findViewById(R.id.radioButtonSchedule);
        layoutDateTime = findViewById(R.id.layoutDateTime);
        editTextDate = findViewById(R.id.editTextDate);
        editTextScheduleTime = findViewById(R.id.editTextScheduleTime);
        checkBoxReminder = findViewById(R.id.checkBoxReminder);
        radioGroupPrivacy = findViewById(R.id.radioGroupPrivacy);

        selectedDateTime = Calendar.getInstance();
        updateDateEditText();
        updateTimeEditText();

        updateGoalInputsState();
        if (radioButtonStartNow != null && radioButtonStartNow.isChecked()) {
            updateScheduleInputsState(false);
        } else if (radioButtonSchedule != null && radioButtonSchedule.isChecked()) {
            updateScheduleInputsState(true);
        } else if (radioButtonStartNow != null) {
            radioButtonStartNow.setChecked(true);
            updateScheduleInputsState(false);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New Activity");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupInteractions() {
        setupChipGroupListener();
        setupGoalsRadioGroupListener();
        setupScheduleRadioGroupListener();
        setupDateTimePickers();
        setupRouteCardsListeners();
        setupStartButtonListener();
    }

    private void setupChipGroupListener() {
        int checkedChipId = chipGroupActivityType.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip initialChip = chipGroupActivityType.findViewById(checkedChipId);
            if (initialChip != null) {
                selectedActivityType = initialChip.getText().toString();
            }
        } else {
            Chip runningChip = findViewById(R.id.chipRunning);
            if (runningChip != null) {
                runningChip.setChecked(true);
                selectedActivityType = runningChip.getText().toString();
            }
        }

        chipGroupActivityType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    selectedActivityType = chip.getText().toString();
                    Log.d(TAG, "Activity type selected: " + selectedActivityType);
                }
            }
        });
    }

    private void setupGoalsRadioGroupListener() {
        radioGroupGoalType.setOnCheckedChangeListener((group, checkedId) -> updateGoalInputsState());
        updateGoalInputsState();
    }

    private void updateGoalInputsState() {
        int checkedId = radioGroupGoalType.getCheckedRadioButtonId();
        boolean isDistanceEnabled = (checkedId == R.id.radioButtonDistance);
        boolean isTimeEnabled = (checkedId == R.id.radioButtonTime);

        textInputLayoutDistance.setEnabled(isDistanceEnabled);
        editTextDistance.setEnabled(isDistanceEnabled);
        textInputLayoutTime.setEnabled(isTimeEnabled);
        editTextTime.setEnabled(isTimeEnabled);

        if (!isDistanceEnabled && textInputLayoutDistance != null) textInputLayoutDistance.setError(null);
        if (!isTimeEnabled && textInputLayoutTime != null) textInputLayoutTime.setError(null);
    }

    private void setupScheduleRadioGroupListener() {
        radioGroupSchedule.setOnCheckedChangeListener((group, checkedId) -> {
            updateScheduleInputsState(checkedId == R.id.radioButtonSchedule);
        });
        if (radioButtonSchedule != null) {
            updateScheduleInputsState(radioButtonSchedule.isChecked());
        }
    }

    private void updateScheduleInputsState(boolean isScheduled) {
        if (editTextDate != null) editTextDate.setEnabled(isScheduled);
        if (editTextScheduleTime != null) editTextScheduleTime.setEnabled(isScheduled);
        if (checkBoxReminder != null) checkBoxReminder.setEnabled(isScheduled);

        if (!isScheduled && checkBoxReminder != null) {
            checkBoxReminder.setChecked(false);
        }
    }
    private void updateDateEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        if (editTextDate != null) editTextDate.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    private void updateTimeEditText() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (editTextScheduleTime != null) editTextScheduleTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }


    private void setupDateTimePickers() {
        if (editTextDate != null) {
            editTextDate.setOnClickListener(v -> {
                if (!editTextDate.isEnabled()) return;
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            selectedDateTime.set(Calendar.YEAR, year);
                            selectedDateTime.set(Calendar.MONTH, month);
                            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateDateEditText();
                        },
                        selectedDateTime.get(Calendar.YEAR),
                        selectedDateTime.get(Calendar.MONTH),
                        selectedDateTime.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            });
        }

        if (editTextScheduleTime != null) {
            editTextScheduleTime.setOnClickListener(v -> {
                if (!editTextScheduleTime.isEnabled()) return;
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {
                            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            selectedDateTime.set(Calendar.MINUTE, minute);
                            selectedDateTime.set(Calendar.SECOND, 0);
                            selectedDateTime.set(Calendar.MILLISECOND, 0);
                            updateTimeEditText();
                        },
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE),
                        true
                ).show();
            });
        }
    }

    private void setupRouteCardsListeners() {
        if (cardViewMap != null) cardViewMap.setOnClickListener(v -> Toast.makeText(this, "Route planning coming soon!", Toast.LENGTH_SHORT).show());
        if (cardViewRouteLibrary != null) cardViewRouteLibrary.setOnClickListener(v -> Toast.makeText(this, "Your saved routes coming soon!", Toast.LENGTH_SHORT).show());
        if (cardViewPopularRoutes != null) cardViewPopularRoutes.setOnClickListener(v -> Toast.makeText(this, "Popular routes coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void setupStartButtonListener() {
        if (buttonStartActivity != null) {
            buttonStartActivity.setOnClickListener(v -> {
                if (currentUserId == -1) {
                    Toast.makeText(this, "User not identified. Cannot save activity.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!validateInputs()) {
                    return;
                }

                String confirmationTitle;
                String confirmationMessage;
                if (radioButtonStartNow.isChecked()) {
                    confirmationTitle = "Start activity now?";
                    confirmationMessage = "Your '" + selectedActivityType + "' activity will be logged to start now.";
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm", Locale.getDefault());
                    String scheduledTime = format.format(selectedDateTime.getTime());
                    confirmationTitle = "Schedule activity?";
                    confirmationMessage = "Your '" + selectedActivityType + "' activity will be scheduled for " + scheduledTime + ".";
                }
                showStartActivityConfirmation(confirmationTitle, confirmationMessage);
            });
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if (radioButtonDistance.isChecked() && TextUtils.isEmpty(editTextDistance.getText().toString().trim())) {
            if (textInputLayoutDistance != null) textInputLayoutDistance.setError("Distance cannot be empty");
            isValid = false;
        } else if (textInputLayoutDistance != null) {
            textInputLayoutDistance.setError(null);
        }

        if (radioButtonTime.isChecked() && TextUtils.isEmpty(editTextTime.getText().toString().trim())) {
            if (textInputLayoutTime != null) textInputLayoutTime.setError("Time cannot be empty");
            isValid = false;
        } else if (textInputLayoutTime != null) {
            textInputLayoutTime.setError(null);
        }

        if (radioButtonSchedule.isChecked()) {
            Calendar now = Calendar.getInstance();
            if (selectedDateTime.getTimeInMillis() < (now.getTimeInMillis() - (5 * 1000)) ) { // Toleransi 5 detik
                Calendar selectedCalNoSeconds = (Calendar) selectedDateTime.clone();
                selectedCalNoSeconds.set(Calendar.SECOND, 0);
                selectedCalNoSeconds.set(Calendar.MILLISECOND, 0);

                Calendar nowCalNoSeconds = (Calendar) now.clone();
                nowCalNoSeconds.set(Calendar.SECOND, 0);
                nowCalNoSeconds.set(Calendar.MILLISECOND, 0);

                if (selectedCalNoSeconds.before(nowCalNoSeconds)) {
                    Toast.makeText(this, "Cannot schedule activity in the past.", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    private void showStartActivityConfirmation(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> saveActivityToDatabase())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveActivityToDatabase() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Cannot save: User not identified.", Toast.LENGTH_LONG).show();
            return;
        }

        ActivityLog newActivity = new ActivityLog();
        newActivity.setUserId(currentUserId);
        newActivity.setActivityType(selectedActivityType);
        newActivity.setTitle(selectedActivityType + " (Planned)");
        newActivity.setDescription(""); // Bisa ditambahkan field input untuk deskripsi

        if (radioButtonDistance.isChecked() && editTextDistance != null && !TextUtils.isEmpty(editTextDistance.getText())) {
            try {
                newActivity.setDistanceKm(Double.parseDouble(editTextDistance.getText().toString().trim()));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid distance format, setting to 0", e);
                newActivity.setDistanceKm(0.0);
            }
        } else {
            newActivity.setDistanceKm(0.0);
        }

        if (radioButtonTime.isChecked() && editTextTime != null && !TextUtils.isEmpty(editTextTime.getText())) {
            try {
                long durationMinutes = Long.parseLong(editTextTime.getText().toString().trim());
                newActivity.setDurationMs(TimeUnit.MINUTES.toMillis(durationMinutes));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid time format for goal, setting to 0", e);
                newActivity.setDurationMs(0L);
            }
        } else {
            newActivity.setDurationMs(0L);
        }

        boolean isScheduled = radioButtonSchedule.isChecked();
        if (isScheduled) {
            newActivity.setTimestampMs(selectedDateTime.getTimeInMillis());
            newActivity.setStatus("SCHEDULED");
        } else {
            newActivity.setTimestampMs(System.currentTimeMillis());
            newActivity.setStatus("PLANNED");
        }

        int selectedPrivacyId = radioGroupPrivacy.getCheckedRadioButtonId();
        if (selectedPrivacyId == R.id.radioButtonFriendsOnly) {
            newActivity.setPrivacySetting("Friends Only");
        } else if (selectedPrivacyId == R.id.radioButtonPrivate) {
            newActivity.setPrivacySetting("Private");
        } else { // Default ke Public jika tidak ada yang terpilih atau R.id.radioButtonPublic
            newActivity.setPrivacySetting("Public");
        }
        newActivity.setKudosCount(0);

        Log.d(TAG, "Attempting to save Activity: Type=" + newActivity.getActivityType() +
                ", UserID=" + newActivity.getUserId() +
                ", Timestamp=" + newActivity.getTimestampMs() +
                ", Status=" + newActivity.getStatus() +
                ", DistanceTarget=" + newActivity.getDistanceKm() +
                ", DurationTarget(ms)=" + newActivity.getDurationMs() +
                ", Privacy=" + newActivity.getPrivacySetting());

        databaseWriteExecutor.execute(() -> {
            long newActivityId = -1;
            try {
                newActivityId = appDb.activityLogDao().insertActivityLog(newActivity);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting activity into database", e);
            }
            final long finalNewActivityId = newActivityId;

            runOnUiThread(() -> {
                if (finalNewActivityId > 0) {
                    String statusMessage = isScheduled ? "scheduled" : "planned";
                    Toast.makeText(CreateActivityActivity.this, "Activity '" + newActivity.getTitle() + "' " + statusMessage + " successfully! ID: " + finalNewActivityId, Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Activity saved with ID: " + finalNewActivityId);
                    finish();
                } else {
                    Toast.makeText(CreateActivityActivity.this, "Failed to save activity. Please try again.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to insert activity into database. newActivityId was not positive.");
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseWriteExecutor != null && !databaseWriteExecutor.isShutdown()) {
            databaseWriteExecutor.shutdown();
        }
    }
}