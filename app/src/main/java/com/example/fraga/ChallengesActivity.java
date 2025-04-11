package com.example.fraga;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChallengesActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ExtendedFloatingActionButton fabCreateGoal;
    private MaterialToolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        
        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabCreateGoal = findViewById(R.id.fabCreateGoal);
       // toolbar = findViewById(R.id.toolbar);
        
        // Set up toolbar
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Set up toolbar navigation click
        //toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Set up view pager with fragments
        ChallengesPagerAdapter pagerAdapter = new ChallengesPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Monthly" : "Personal")
        ).attach();
        
        // Set up FAB
        fabCreateGoal.setOnClickListener(v -> {
            // Show different actions based on current tab
            if (viewPager.getCurrentItem() == 0) {
                Toast.makeText(ChallengesActivity.this, "Joining a challenge coming soon!", 
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChallengesActivity.this, "Create new goal coming soon!", 
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        // Update FAB text based on selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    fabCreateGoal.setText("Join Challenge");
                } else {
                    fabCreateGoal.setText("Create Goal");
                }
            }
        });
    }
    
    /**
     * Adapter class for the ViewPager2 to handle fragments
     */
    private static class ChallengesPagerAdapter extends FragmentStateAdapter {
        
        public ChallengesPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        
        @Override
        public int getItemCount() {
            return 2; // Monthly and Personal tabs
        }
        
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MonthlyChallengesFragment();
            } else {
                return new PersonalGoalsFragment(); 
            }
        }
    }
} 