package com.example.fraga;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fraga.adapters.UserAdapter;
import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.UserProfileRepository;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private UserProfileRepository userProfileRepository;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Initialize repository
        userProfileRepository = new UserProfileRepository();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Users");

        // Initialize RecyclerView
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, new ArrayList<>());
        usersRecyclerView.setAdapter(userAdapter);

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        userProfileRepository.getAllUsers()
                .addOnSuccessListener(users -> {
                    userAdapter.updateUsers(users);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 