package com.example.fraga;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateActivityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialCardView cardViewMap;
    private MaterialCardView cardViewRouteLibrary;
    private MaterialCardView cardViewPopularRoutes;
    private MaterialButton buttonStartActivity;
    
    private RadioButton radioButtonDistance;
    private RadioButton radioButtonTime;
    private RadioButton radioButtonNoGoal;
    private TextInputLayout textInputLayoutDistance;
    private TextInputLayout textInputLayoutTime;
    
    private RadioButton radioButtonStartNow;
    private RadioButton radioButtonSchedule;
    private LinearLayout layoutDateTime;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextScheduleTime;
    private TextInputLayout textInputLayoutDate;
    private TextInputLayout textInputLayoutTimeSchedule;
    private CheckBox checkBoxReminder;
    
    private Calendar selectedDateTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        
        // Initialize views
        initializeViews();
        
        // Set up toolbar
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Set up navigation click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Set up interactions
        setupChipGroup();
        setupGoalsRadioGroup();
        setupScheduleRadioGroup();
        setupRouteCards();
        setupStartButton();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        cardViewMap = findViewById(R.id.cardViewMap);
        cardViewRouteLibrary = findViewById(R.id.cardViewRouteLibrary);
        cardViewPopularRoutes = findViewById(R.id.cardViewPopularRoutes);
        buttonStartActivity = findViewById(R.id.buttonStartActivity);
        
        // Goals section
        radioButtonDistance = findViewById(R.id.radioButtonDistance);
        radioButtonTime = findViewById(R.id.radioButtonTime);
        radioButtonNoGoal = findViewById(R.id.radioButtonNoGoal);
        textInputLayoutDistance = findViewById(R.id.textInputLayoutDistance);
        textInputLayoutTime = findViewById(R.id.textInputLayoutTime);
        
        // Schedule section
        radioButtonStartNow = findViewById(R.id.radioButtonStartNow);
        radioButtonSchedule = findViewById(R.id.radioButtonSchedule);
        layoutDateTime = findViewById(R.id.layoutDateTime);
        editTextDate = findViewById(R.id.editTextDate);
        editTextScheduleTime = findViewById(R.id.editTextScheduleTime);
        checkBoxReminder = findViewById(R.id.checkBoxReminder);
        
        // Initialize calendar
        selectedDateTime = Calendar.getInstance();
    }
    
    private void setupChipGroup() {
        Chip chipRunning = findViewById(R.id.chipRunning);
        Chip chipCycling = findViewById(R.id.chipCycling);
        Chip chipWalking = findViewById(R.id.chipWalking);
        Chip chipHiking = findViewById(R.id.chipHiking);
        Chip chipSwimming = findViewById(R.id.chipSwimming);
        
        View.OnClickListener chipClickListener = v -> {
            // Update UI or settings based on selected activity type
            String activityType = "Activity";
            
            if (v.getId() == R.id.chipRunning) {
                activityType = "Running";
            } else if (v.getId() == R.id.chipCycling) {
                activityType = "Cycling";
            } else if (v.getId() == R.id.chipWalking) {
                activityType = "Walking";
            } else if (v.getId() == R.id.chipHiking) {
                activityType = "Hiking";
            } else if (v.getId() == R.id.chipSwimming) {
                activityType = "Swimming";
            }
            
            Toast.makeText(this, activityType + " selected", Toast.LENGTH_SHORT).show();
        };
        
        chipRunning.setOnClickListener(chipClickListener);
        chipCycling.setOnClickListener(chipClickListener);
        chipWalking.setOnClickListener(chipClickListener);
        chipHiking.setOnClickListener(chipClickListener);
        chipSwimming.setOnClickListener(chipClickListener);
    }
    
    private void setupGoalsRadioGroup() {
        RadioGroup radioGroupGoalType = findViewById(R.id.radioGroupGoalType);
        
        radioGroupGoalType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonDistance) {
                textInputLayoutDistance.setEnabled(true);
                textInputLayoutTime.setEnabled(false);
            } else if (checkedId == R.id.radioButtonTime) {
                textInputLayoutDistance.setEnabled(false);
                textInputLayoutTime.setEnabled(true);
            } else if (checkedId == R.id.radioButtonNoGoal) {
                textInputLayoutDistance.setEnabled(false);
                textInputLayoutTime.setEnabled(false);
            }
        });
    }
    
    private void setupScheduleRadioGroup() {
        RadioGroup radioGroupSchedule = findViewById(R.id.radioGroupSchedule);
        
        radioGroupSchedule.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isScheduled = (checkedId == R.id.radioButtonSchedule);
            layoutDateTime.setEnabled(isScheduled);
            
            // Enable/disable date and time input fields
            for (int i = 0; i < layoutDateTime.getChildCount(); i++) {
                View child = layoutDateTime.getChildAt(i);
                child.setEnabled(isScheduled);
            }
            
            checkBoxReminder.setEnabled(isScheduled);
        });
        
        // Set up date picker
        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                        editTextDate.setText(dateFormat.format(selectedDateTime.getTime()));
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Set up time picker
        editTextScheduleTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        editTextScheduleTime.setText(timeFormat.format(selectedDateTime.getTime()));
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });
        
        // Set up reminder checkbox
        checkBoxReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Reminder will be set", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupRouteCards() {
        // Map card for route planning
        cardViewMap.setOnClickListener(v -> {
            Toast.makeText(this, "Route planning coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // Route library card
        cardViewRouteLibrary.setOnClickListener(v -> {
            Toast.makeText(this, "Your saved routes coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // Popular routes card
        cardViewPopularRoutes.setOnClickListener(v -> {
            Toast.makeText(this, "Popular routes coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupStartButton() {
        buttonStartActivity.setOnClickListener(v -> {
            if (radioButtonStartNow.isChecked()) {
                // Start activity now
                showStartActivityConfirmation("Start activity now?", "Your activity will begin immediately.");
            } else {
                // Schedule for later
                SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm", Locale.getDefault());
                String scheduledTime = format.format(selectedDateTime.getTime());
                
                showStartActivityConfirmation(
                        "Schedule activity?",
                        "Your activity will be scheduled for " + scheduledTime + "."
                );
            }
        });
    }
    
    private void showStartActivityConfirmation(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // In a real app, this would start the activity tracking or schedule it
                    Toast.makeText(this, "Activity set up successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
} 