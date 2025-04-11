package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout layoutEditProfile;
    private LinearLayout layoutChangePassword;
    private LinearLayout layoutLinkedAccounts;
    private LinearLayout layoutDistanceUnit;
    private LinearLayout layoutWeightUnit;
    private LinearLayout layoutLanguage;
    private LinearLayout layoutPrivacyControls;
    private LinearLayout layoutLogout;
    private SwitchMaterial switchActivityReminders;
    private SwitchMaterial switchKudosNotifications;
    private SwitchMaterial switchFriendActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        initializeViews();
        
        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        layoutEditProfile = findViewById(R.id.layoutEditProfile);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        layoutLinkedAccounts = findViewById(R.id.layoutLinkedAccounts);
        layoutDistanceUnit = findViewById(R.id.layoutDistanceUnit);
        layoutWeightUnit = findViewById(R.id.layoutWeightUnit);
        layoutLanguage = findViewById(R.id.layoutLanguage);
        layoutPrivacyControls = findViewById(R.id.layoutPrivacyControls);
        layoutLogout = findViewById(R.id.layoutLogout);
        switchActivityReminders = findViewById(R.id.switchActivityReminders);
        switchKudosNotifications = findViewById(R.id.switchKudosNotifications);
        switchFriendActivities = findViewById(R.id.switchFriendActivities);
    }

    private void setupListeners() {
        // Account settings
        layoutEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        layoutChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        layoutLinkedAccounts.setOnClickListener(v -> {
            Toast.makeText(this, "Linked Accounts feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Units & preferences
        layoutDistanceUnit.setOnClickListener(v -> {
            showDistanceUnitDialog();
        });

        layoutWeightUnit.setOnClickListener(v -> {
            showWeightUnitDialog();
        });

        layoutLanguage.setOnClickListener(v -> {
            showLanguageDialog();
        });

        // Privacy
        layoutPrivacyControls.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Controls feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Logout
        layoutLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });

        // Notification toggles
        switchActivityReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // In a real app, we would save this preference
            String message = isChecked ? "Activity reminders enabled" : "Activity reminders disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        switchKudosNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // In a real app, we would save this preference
            String message = isChecked ? "Kudos notifications enabled" : "Kudos notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        switchFriendActivities.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // In a real app, we would save this preference
            String message = isChecked ? "Friend activity notifications enabled" : "Friend activity notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDistanceUnitDialog() {
        final String[] distanceUnits = {"Kilometers (km)", "Miles (mi)"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Distance Unit")
                .setItems(distanceUnits, (dialog, which) -> {
                    // Save the preference and update the UI
                    Toast.makeText(this, "Distance unit set to " + distanceUnits[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showWeightUnitDialog() {
        final String[] weightUnits = {"Kilograms (kg)", "Pounds (lb)"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Weight Unit")
                .setItems(weightUnits, (dialog, which) -> {
                    // Save the preference and update the UI
                    Toast.makeText(this, "Weight unit set to " + weightUnits[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showLanguageDialog() {
        final String[] languages = {"English (US)", "Spanish", "French", "German", "Japanese"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    // Save the preference and update the UI
                    Toast.makeText(this, "Language set to " + languages[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // In a real app, we would clear the user session
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                    
                    // Navigate back to login screen
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
} 