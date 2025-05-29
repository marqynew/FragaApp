package com.example.fraga;

import android.content.Context;
import android.content.Intent; // Pastikan Intent diimpor jika digunakan untuk navigasi
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull; // Pastikan ini diimpor
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.FriendRequest;
import com.example.fraga.db.entity.Friendship;
import com.example.fraga.db.entity.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

// Hapus impor yang tidak terpakai: import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocialActivity extends AppCompatActivity {

    private static final String TAG = "SocialActivity";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAddFriend;
    private Toolbar toolbar;

    private final String[] tabTitles = new String[]{"Friends", "Discover", "Leaderboard"};

    private AppDatabase appDb;
    private ExecutorService databaseExecutor; // Nama field yang konsisten
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        appDb = AppDatabase.getInstance(getApplicationContext());
        databaseExecutor = Executors.newSingleThreadExecutor(); // Inisialisasi field kelas

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Log.w(TAG, "Current User ID not found. Social features might be limited or user will be prompted to login.");
        }

        initializeViews();
        setupToolbar();
        setupViewPagerAndTabs(); // Mengganti nama metode agar lebih deskriptif
        setupFab();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabAddFriend = findViewById(R.id.fabAddFriend);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar); // Toolbar sekarang adalah field kelas
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Judul toolbar bisa diatur di XML atau di sini jika perlu dinamis
            // getSupportActionBar().setTitle(getString(R.string.title_social_activity));
        }
        // Pastikan toolbar tidak null sebelum mengatur listener
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            Log.e(TAG, "Toolbar not found in layout!");
        }
    }

    private void setupViewPagerAndTabs() { // Nama metode diubah
        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "ViewPager or TabLayout not found in layout!");
            return;
        }
        SocialPagerAdapter pagerAdapter = new SocialPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position >= 0 && position < tabTitles.length) { // Pemeriksaan batas array yang lebih aman
                        tab.setText(tabTitles[position]);
                    } else {
                        Log.w(TAG, "Invalid position for tab title: " + position);
                    }
                }
        ).attach();
    }

    private void setupFab() {
        if (fabAddFriend == null || viewPager == null) {
            Log.e(TAG, "FAB or ViewPager not found, cannot setup FAB!");
            return;
        }

        fabAddFriend.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Please login to use social features.", Toast.LENGTH_SHORT).show();
                // Pertimbangkan untuk mengarahkan ke LoginActivity
                // Intent intent = new Intent(SocialActivity.this, LoginActivity.class);
                // startActivity(intent);
                return;
            }

            int currentTabPosition = viewPager.getCurrentItem();
            switch (currentTabPosition) {
                case 0: // Friends tab
                    showAddFriendDialog();
                    break;
                case 1: // Discover tab
                    Toast.makeText(this, "Find people feature coming soon!", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // Leaderboard tab
                    Toast.makeText(this, "Filter leaderboard feature coming soon!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (isFinishing() || isDestroyed() || fabAddFriend == null) return;
                try {
                    switch (position) {
                        case 0: // Friends
                            fabAddFriend.setImageResource(android.R.drawable.ic_menu_add);
                            fabAddFriend.setContentDescription(getString(R.string.add_friend_button_desc));
                            fabAddFriend.show();
                            break;
                        case 1: // Discover
                            fabAddFriend.setImageResource(android.R.drawable.ic_menu_search);
                            fabAddFriend.setContentDescription(getString(R.string.find_people_button_desc));
                            fabAddFriend.show();
                            break;
                        case 2: // Leaderboard
                            fabAddFriend.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                            fabAddFriend.setContentDescription(getString(R.string.filter_leaderboard_button_desc));
                            fabAddFriend.show();
                            break;
                        default:
                            fabAddFriend.hide();
                            break;
                    }
                } catch (IllegalStateException e) { // Menangkap jika getString dipanggil saat fragment tidak attached
                    Log.e(TAG, "Error setting FAB content description: " + e.getMessage());
                }
            }
        });

        // Set ikon FAB awal setelah viewPager memiliki kesempatan untuk layout
        viewPager.post(() -> {
            if (fabAddFriend != null && viewPager != null) { // Periksa lagi karena ini di post
                int initialPosition = viewPager.getCurrentItem();
                // Panggil onPageSelected secara manual untuk mengatur ikon FAB awal
                if (viewPager.getAdapter() != null && initialPosition < viewPager.getAdapter().getItemCount()) {
                    viewPager.getAdapter().notifyItemChanged(initialPosition); // Ini mungkin tidak langsung memanggil onPageSelected
                    // Cara yang lebih pasti:
                    if (isFinishing() || isDestroyed()) return;
                    try {
                        switch (initialPosition) {
                            case 0:
                                fabAddFriend.setImageResource(android.R.drawable.ic_menu_add);
                                fabAddFriend.setContentDescription(getString(R.string.add_friend_button_desc));
                                fabAddFriend.show();
                                break;
                            // Anda bisa menambahkan case lain jika ikon FAB berbeda untuk tab awal selain 0
                            default:
                                // Jika tab awal bukan "Friends", mungkin sembunyikan atau set ikon default
                                // fabAddFriend.hide(); // Atau
                                // fabAddFriend.setImageResource(DEFAULT_ICON_RESOURCE_ID);
                                // fabAddFriend.setContentDescription(getString(DEFAULT_CONTENT_DESC_ID));
                                // fabAddFriend.show();
                                break; // Untuk tab lain, biarkan listener onPageSelected yang menangani jika tab berubah
                        }
                    } catch (IllegalStateException e) { Log.e(TAG, "Error setting initial FAB state: " + e.getMessage()); }
                }
            }
        });
    }

    private void showAddFriendDialog() {
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "Activity is finishing or destroyed, cannot show AddFriendDialog.");
            return;
        }

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_friend, null);
        final EditText editTextEmailFriend = dialogView.findViewById(R.id.editTextEmail);
        final EditText editTextMessage = dialogView.findViewById(R.id.editTextMessage);

        if (editTextEmailFriend == null || editTextMessage == null) {
            Log.e(TAG, "Required EditText views not found in dialog_add_friend.xml. Check IDs: R.id.editTextEmail, R.id.editTextMessage");
            Toast.makeText(this, "Error displaying dialog: Layout views missing.", Toast.LENGTH_LONG).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.add_friend_dialog_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.send_request_button), (dialog, which) -> {
                    String targetIdentifier = editTextEmailFriend.getText().toString().trim();
                    String message = editTextMessage.getText().toString().trim();

                    if (TextUtils.isEmpty(targetIdentifier)) {
                        Toast.makeText(SocialActivity.this, "Please enter friend's email.", Toast.LENGTH_SHORT).show(); // Disederhanakan
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(targetIdentifier).matches()) {
                        Toast.makeText(SocialActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show(); // Disederhanakan
                        return;
                    }
                    sendFriendRequest(targetIdentifier, message);
                })
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }

    private void sendFriendRequest(final String targetEmail, final String message) {
        if (appDb == null || databaseExecutor == null) {
            Toast.makeText(this, "Database service not available. Please try again later.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Database (appDb) or executor (databaseExecutor) not initialized in sendFriendRequest.");
            return;
        }
        if (currentUserId == -1) {
            Toast.makeText(this, "You must be logged in to send friend requests.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Attempted to send friend request but currentUserId is -1.");
            return;
        }

        databaseExecutor.execute(() -> { // Menggunakan field kelas databaseExecutor
            User targetUser = appDb.userDao().findUserByEmail(targetEmail);

            if (targetUser == null) {
                runOnUiThread(() -> Toast.makeText(SocialActivity.this, "User with email '" + targetEmail + "' not found.", Toast.LENGTH_LONG).show());
                return;
            }

            if (targetUser.getId() == currentUserId) {
                runOnUiThread(() -> Toast.makeText(SocialActivity.this, "You cannot send a friend request to yourself.", Toast.LENGTH_LONG).show());
                return;
            }

            Friendship existingFriendship = appDb.friendshipDao().findFriendshipBetweenUsers(currentUserId, targetUser.getId());
            if (existingFriendship != null) {
                final String targetName = targetUser.getName() != null ? targetUser.getName() : targetEmail;
                runOnUiThread(() -> Toast.makeText(SocialActivity.this, "You are already friends with " + targetName + ".", Toast.LENGTH_LONG).show());
                return;
            }

            FriendRequest existingRequest = appDb.friendRequestDao().findPendingRequestBetweenUsers(currentUserId, targetUser.getId());
            if (existingRequest != null) {
                final String targetName = targetUser.getName() != null ? targetUser.getName() : targetEmail;
                runOnUiThread(() -> {
                    if (existingRequest.getFromUserId() == currentUserId) {
                        Toast.makeText(SocialActivity.this, "You have already sent a request to " + targetName + ".", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SocialActivity.this, targetName + " has already sent you a request. Check your friend requests.", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            FriendRequest newRequest = new FriendRequest(currentUserId, targetUser.getId(), message, System.currentTimeMillis());
            long requestId = appDb.friendRequestDao().insertRequest(newRequest);

            runOnUiThread(() -> {
                if (requestId > 0) {
                    Toast.makeText(SocialActivity.this, String.format(getString(R.string.friend_request_sent_toast), targetEmail), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Friend request sent from user " + currentUserId + " to user " + targetUser.getId() + " (email: " + targetEmail + ")");
                } else {
                    Toast.makeText(SocialActivity.this, "Could not send friend request. User may not exist or request already pending.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to insert friend request for target email: " + targetEmail + ". RequestId: " + requestId);
                }
            });
        });
    }

    private static class SocialPagerAdapter extends FragmentStateAdapter {
        public SocialPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new FriendsFragment();
                case 1: return new DiscoverFragment();
                case 2: return new LeaderboardFragment();
                default: return new FriendsFragment();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Menggunakan nama field kelas yang benar
        if (databaseExecutor != null && !databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
            Log.d(TAG, "Database executor shutdown.");
        }
    }
}