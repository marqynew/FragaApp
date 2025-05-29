package com.example.fraga;

import android.content.Context; // Untuk SharedPreferences
import android.content.Intent;
import android.content.SharedPreferences; // Untuk SharedPreferences
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
// import com.google.android.material.floatingactionbutton.FloatingActionButton; // Tidak digunakan di MainActivity
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    // private FloatingActionButton fabCreateActivity; // FAB ini ada di FeedFragment, bukan di MainActivity secara langsung

    private int currentUserId = -1; // Untuk menyimpan ID pengguna yang login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ambil ID pengguna dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        String currentUserEmail = prefs.getString(LoginActivity.KEY_USER_EMAIL, "Not Logged In");

        if (currentUserId == -1 && !currentUserEmail.equals("test@example.com")) { // test@example.com mungkin tidak menyimpan sesi
            Log.e(TAG, "User not properly logged in, redirecting to LoginActivity.");
            // Jika tidak ada user ID yang valid (dan bukan user test), mungkin kembali ke Login
            // Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // startActivity(intent);
            // finish();
            // return;
            // Untuk sekarang, kita biarkan lanjut tapi log warning.
            Log.w(TAG, "Proceeding without a valid saved user ID (currentUserId = -1). User: " + currentUserEmail);
        } else {
            Log.i(TAG, "Current User ID: " + currentUserId + ", Email: " + currentUserEmail);
        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this);
        } else {
            Log.e(TAG, "Bottom navigation view not found!");
        }


        // Set default fragment to tracking
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_track);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.navigation_track) {
            selectedFragment = new TrackingFragment();
        } else if (itemId == R.id.navigation_feed) {
            selectedFragment = new FeedFragment();
        } else if (itemId == R.id.navigation_social) {
            // Di SocialActivity, FriendsFragment digunakan. Jika ini tab social di main,
            // mungkin Anda ingin SocialFragment atau FriendsFragment di sini.
            // Berdasarkan kode Anda sebelumnya, FriendsFragment digunakan di SocialActivity.
            // Untuk BottomNav di MainActivity, kita bisa gunakan SocialFragment jika ada,
            // atau tetap FriendsFragment jika itu yang dimaksud.
            // Mari asumsikan FriendsFragment untuk konsistensi dengan navigasi bottom bar di activity_main.xml.
            selectedFragment = new FriendsFragment(); // Menggunakan FriendsFragment seperti di XML
        } else if (itemId == R.id.navigation_challenges) {
            // Di ChallengesActivity, PersonalGoalsFragment digunakan sebagai salah satu tab.
            // Untuk BottomNav di MainActivity, kita bisa gunakan ChallengesFragment (yang menjadi container)
            // atau langsung PersonalGoalsFragment. Mari gunakan PersonalGoalsFragment untuk kesederhanaan di sini.
            selectedFragment = new PersonalGoalsFragment(); // Langsung ke Personal Goals
        } else if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            // Anda bisa meneruskan currentUserId ke fragmen jika diperlukan
            // Bundle args = new Bundle();
            // args.putInt("CURRENT_USER_ID", currentUserId);
            // selectedFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }

        return false;
    }

    // Metode untuk mendapatkan ID pengguna saat ini (bisa dipanggil oleh fragmen jika perlu)
    public int getCurrentUserId() {
        return currentUserId;
    }
}