package com.example.fraga;

import android.content.Context; // Untuk SharedPreferences
import android.content.Intent;
import android.content.SharedPreferences; // Untuk SharedPreferences
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView; // Untuk contoh update UI
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// import com.example.fraga.db.entity.User;
// import com.example.fraga.EditProfileActivity; // Contoh Activity untuk edit profil

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

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

    // Untuk menampilkan pilihan saat ini di UI
    private TextView textViewDistanceUnitValue;
    private TextView textViewWeightUnitValue;
    private TextView textViewLanguageValue;

    // private AppDatabase appDb;
    private int currentUserId = -1;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    // Kunci SharedPreferences
    public static final String KEY_DISTANCE_UNIT = "distance_unit";
    public static final String KEY_WEIGHT_UNIT = "weight_unit";
    public static final String KEY_LANGUAGE = "language_pref";
    public static final String KEY_NOTIF_ACTIVITY_REMINDERS = "notif_activity_reminders";
    public static final String KEY_NOTIF_KUDOS = "notif_kudos";
    public static final String KEY_NOTIF_FRIEND_ACTIVITIES = "notif_friend_activities";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        // Dapatkan instance database (akan diaktifkan nanti jika diperlukan untuk settings tertentu)
        // appDb = AppDatabase.getInstance(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Toolbar navigation click (kembali)
            toolbar.setNavigationOnClickListener(v -> finish()); // Menggunakan finish() untuk kembali
        }


        initializeViews();
        loadCurrentSettings(); // Muat pengaturan saat ini ke UI
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

        // TextViews untuk menampilkan nilai pengaturan saat ini (dari layout activity_settings.xml)
        textViewDistanceUnitValue = findViewById(R.id.textViewDistanceUnit);
        textViewWeightUnitValue = findViewById(R.id.textViewWeightUnit);
        textViewLanguageValue = findViewById(R.id.textViewLanguage);
    }

    private void loadCurrentSettings() {
        // Muat dan tampilkan pengaturan unit dari SharedPreferences
        textViewDistanceUnitValue.setText(prefs.getString(KEY_DISTANCE_UNIT, "Kilometers (km)"));
        textViewWeightUnitValue.setText(prefs.getString(KEY_WEIGHT_UNIT, "Kilograms (kg)"));
        textViewLanguageValue.setText(prefs.getString(KEY_LANGUAGE, "English (US)"));

        // Muat dan set status Switch notifikasi
        switchActivityReminders.setChecked(prefs.getBoolean(KEY_NOTIF_ACTIVITY_REMINDERS, true));
        switchKudosNotifications.setChecked(prefs.getBoolean(KEY_NOTIF_KUDOS, true));
        switchFriendActivities.setChecked(prefs.getBoolean(KEY_NOTIF_FRIEND_ACTIVITIES, true));
    }

    private void setupListeners() {
        layoutEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show(); //
            // TODO: Navigasi ke EditProfileActivity, mungkin dengan currentUserId
            // Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            // intent.putExtra("USER_ID", currentUserId);
            // startActivity(intent);
        });

        layoutChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password feature coming soon!", Toast.LENGTH_SHORT).show(); //
            // TODO: Tampilkan dialog atau activity untuk ubah password, interaksi DB untuk User.password
        });

        layoutLinkedAccounts.setOnClickListener(v -> {
            Toast.makeText(this, "Linked Accounts feature coming soon!", Toast.LENGTH_SHORT).show(); //
        });

        layoutDistanceUnit.setOnClickListener(v -> showDistanceUnitDialog());
        layoutWeightUnit.setOnClickListener(v -> showWeightUnitDialog());
        layoutLanguage.setOnClickListener(v -> showLanguageDialog());

        layoutPrivacyControls.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Controls feature coming soon!", Toast.LENGTH_SHORT).show(); //
            // TODO: Navigasi ke layar kontrol privasi, update field di User entity
        });

        layoutLogout.setOnClickListener(v -> showLogoutConfirmation()); //

        switchActivityReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsEditor.putBoolean(KEY_NOTIF_ACTIVITY_REMINDERS, isChecked).apply();
            String message = isChecked ? "Activity reminders enabled" : "Activity reminders disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); //
            // TODO: Jika perlu, simpan juga ke DB UserSettings untuk sinkronisasi
        });

        switchKudosNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsEditor.putBoolean(KEY_NOTIF_KUDOS, isChecked).apply();
            String message = isChecked ? "Kudos notifications enabled" : "Kudos notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); //
        });

        switchFriendActivities.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsEditor.putBoolean(KEY_NOTIF_FRIEND_ACTIVITIES, isChecked).apply();
            String message = isChecked ? "Friend activity notifications enabled" : "Friend activity notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); //
        });
    }

    private void showDistanceUnitDialog() {
        final String[] distanceUnits = {"Kilometers (km)", "Miles (mi)"}; //
        String currentUnit = prefs.getString(KEY_DISTANCE_UNIT, distanceUnits[0]);
        int checkedItem = currentUnit.equals(distanceUnits[1]) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Select Distance Unit")
                .setSingleChoiceItems(distanceUnits, checkedItem, (dialog, which) -> {
                    prefsEditor.putString(KEY_DISTANCE_UNIT, distanceUnits[which]).apply();
                    textViewDistanceUnitValue.setText(distanceUnits[which]);
                    Toast.makeText(this, "Distance unit set to " + distanceUnits[which], Toast.LENGTH_SHORT).show(); //
                    // TODO: Jika setting ini ada di User entity, update DB:
                    // if (appDb != null && currentUserId != -1) {
                    //    new Thread(() -> appDb.userDao().updateDistanceUnit(currentUserId, distanceUnits[which])).start();
                    // }
                    dialog.dismiss();
                })
                .show();
    }

    private void showWeightUnitDialog() {
        final String[] weightUnits = {"Kilograms (kg)", "Pounds (lb)"}; //
        String currentUnit = prefs.getString(KEY_WEIGHT_UNIT, weightUnits[0]);
        int checkedItem = currentUnit.equals(weightUnits[1]) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Select Weight Unit")
                .setSingleChoiceItems(weightUnits, checkedItem, (dialog, which) -> {
                    prefsEditor.putString(KEY_WEIGHT_UNIT, weightUnits[which]).apply();
                    textViewWeightUnitValue.setText(weightUnits[which]);
                    Toast.makeText(this, "Weight unit set to " + weightUnits[which], Toast.LENGTH_SHORT).show(); //
                    // TODO: Update DB jika perlu
                    dialog.dismiss();
                })
                .show();
    }

    private void showLanguageDialog() {
        final String[] languages = {"English (US)", "Spanish", "French", "German", "Japanese"}; //
        String currentLang = prefs.getString(KEY_LANGUAGE, languages[0]);
        int checkedItem = 0;
        for(int i=0; i<languages.length; i++){
            if(currentLang.equals(languages[i])) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    prefsEditor.putString(KEY_LANGUAGE, languages[which]).apply();
                    textViewLanguageValue.setText(languages[which]);
                    Toast.makeText(this, "Language set to " + languages[which] + " (App restart may be required)", Toast.LENGTH_LONG).show(); //
                    // TODO: Implementasi perubahan bahasa runtime atau minta restart aplikasi
                    // TODO: Update DB jika perlu
                    dialog.dismiss();
                })
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Hapus sesi dari SharedPreferences
                    prefsEditor.remove(LoginActivity.KEY_USER_ID);
                    prefsEditor.remove(LoginActivity.KEY_USER_EMAIL);
                    // Hapus info lain yang mungkin disimpan terkait sesi
                    prefsEditor.apply();

                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show(); //

                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show(); //
    }
}