package com.example.fraga;

import android.content.Intent; // Import jika Anda akan navigasi ke Activity baru
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// Untuk kelas Activity/Dialog pembuatan goal:
// import com.example.fraga.CreateGoalActivity; // Contoh

public class ChallengesActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ExtendedFloatingActionButton fabCreateGoal;
    private MaterialToolbar toolbar; // Toolbar di layout XML ada, tapi tidak diinisialisasi di sini

    // private AppDatabase appDb; // Akan digunakan jika ada operasi db langsung di Activity ini

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabCreateGoal = findViewById(R.id.fabCreateGoal);
        toolbar = findViewById(R.id.toolbar); // Inisialisasi toolbar dari layout

        // Dapatkan instance database (jika diperlukan di Activity ini)
        // appDb = AppDatabase.getInstance(getApplicationContext());

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set up toolbar navigation click
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Menggunakan lambda untuk kembali

        // Set up view pager with fragments
        ChallengesPagerAdapter pagerAdapter = new ChallengesPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Monthly" : "Personal")
        ).attach();

        // Set up FAB
        fabCreateGoal.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0) { // Tab "Monthly"
                // TODO: Implementasi untuk bergabung dengan tantangan bulanan
                // Ini mungkin melibatkan pembaruan status pengguna untuk tantangan tertentu di database.
                Toast.makeText(ChallengesActivity.this, "Joining a challenge feature coming soon!",
                        Toast.LENGTH_SHORT).show();
            } else { // Tab "Personal"
                // TODO: Implementasi untuk membuat tujuan pribadi baru
                // Ini akan idealnya membuka Activity/Dialog baru untuk input data tujuan,
                // yang kemudian akan menyimpan data ke database.
                Toast.makeText(ChallengesActivity.this, "Create new goal feature coming soon!",
                        Toast.LENGTH_SHORT).show();
                // Contoh navigasi ke Activity untuk membuat goal baru (jika ada):
                // Intent intent = new Intent(ChallengesActivity.this, CreateGoalActivity.class);
                // startActivity(intent);
            }
        });

        // Update FAB text based on selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    fabCreateGoal.setText("Join Challenge");
                    fabCreateGoal.setIconResource(R.drawable.ic_menu_add); // Ganti dengan ikon yang sesuai
                } else {
                    fabCreateGoal.setText("Create Goal");
                    fabCreateGoal.setIconResource(R.drawable.ic_menu_edit); // Ganti dengan ikon yang sesuai
                }
            }
        });
        // Set initial FAB text and icon based on the default selected tab (usually 0)
        if (viewPager.getCurrentItem() == 0) {
            fabCreateGoal.setText("Join Challenge");
            fabCreateGoal.setIconResource(R.drawable.ic_menu_add); // Ganti dengan ikon yang sesuai
        } else {
            fabCreateGoal.setText("Create Goal");
            fabCreateGoal.setIconResource(R.drawable.ic_menu_edit); // Ganti dengan ikon yang sesuai
        }
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

        @NonNull
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