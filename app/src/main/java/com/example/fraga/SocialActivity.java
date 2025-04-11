package com.example.fraga;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SocialActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAddFriend;
    private Toolbar toolbar;
    
    // Tab titles
    private final String[] tabTitles = new String[]{"Friends", "Discover", "Leaderboard"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabAddFriend = findViewById(R.id.fabAddFriend);
        
        // Set up toolbar
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Set up toolbar navigation click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Set up view pager with fragments
        SocialPagerAdapter pagerAdapter = new SocialPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
        
        // Set up floating action button
        setupFab();
    }
    
    private void setupFab() {
        fabAddFriend.setOnClickListener(v -> {
            // Show dialog based on current tab
            switch (viewPager.getCurrentItem()) {
                case 0: // Friends tab
                    showAddFriendDialog();
                    break;
                case 1: // Discover tab
                    Toast.makeText(this, "Find people coming soon!", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // Leaderboard tab
                    Toast.makeText(this, "Filter leaderboard coming soon!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        // Change FAB icon/behavior based on tab selection
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: // Friends
                        fabAddFriend.setImageResource(android.R.drawable.ic_menu_add);
                        fabAddFriend.setContentDescription("Add Friend");
                        fabAddFriend.show();
                        break;
                    case 1: // Discover
                        fabAddFriend.setImageResource(android.R.drawable.ic_menu_search);
                        fabAddFriend.setContentDescription("Find People");
                        fabAddFriend.show();
                        break;
                    case 2: // Leaderboard
                        fabAddFriend.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                        fabAddFriend.setContentDescription("Filter");
                        fabAddFriend.show();
                        break;
                }
            }
        });
    }
    
    private void showAddFriendDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("Add Friend")
            .setView(dialogView)
            .setPositiveButton("Send Request", (dialog, which) -> {
                String email = editTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    Toast.makeText(SocialActivity.this, 
                            "Friend request sent to: " + email, 
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SocialActivity.this, 
                            "Please enter an email address", 
                            Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Adapter class for the ViewPager2 to handle fragments
     */
    private static class SocialPagerAdapter extends FragmentStateAdapter {
        
        public SocialPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        
        @Override
        public int getItemCount() {
            return 3; // Three tabs: Friends, Discover, Leaderboard
        }
        
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FriendsFragment();
                case 1:
                    return new DiscoverFragment();
                case 2:
                    return new LeaderboardFragment();
                default:
                    return new FriendsFragment();
            }
        }
    }
} 