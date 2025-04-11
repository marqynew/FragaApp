package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCreateActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            selectedFragment = new FriendsFragment();
        } else if (itemId == R.id.navigation_challenges) {
            selectedFragment = new PersonalGoalsFragment();
        } else if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        }
        
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
        
        return false;
    }
}