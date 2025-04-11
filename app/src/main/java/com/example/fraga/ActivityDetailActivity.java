package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetailMap;
    private ImageView imageViewDetailUserAvatar;
    private TextView textViewDetailUsername;
    private TextView textViewDetailActivityTime;
    private TextView textViewDetailActivityTitle;
    private TextView textViewDetailActivityDescription;
    private TextView textViewDetailDistance;
    private TextView textViewDetailDuration;
    private TextView textViewDetailPace;
    private TextView textViewDetailElevation;
    private TextView textViewDetailCalories;
    private TextView textViewDetailAvgHR;
    private TextView textViewDetailMaxHR;
    private EditText editTextComment;
    private ImageButton buttonSendComment;
    private FloatingActionButton fabBack;
    private ExtendedFloatingActionButton fabShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        initializeViews();
        
        // Set up data from intent (would typically come from a database or server)
        setupActivityData();
        
        // Set up click listeners
        setupListeners();
    }
    
    private void initializeViews() {
        imageViewDetailMap = findViewById(R.id.imageViewDetailMap);
        imageViewDetailUserAvatar = findViewById(R.id.imageViewDetailUserAvatar);
        textViewDetailUsername = findViewById(R.id.textViewDetailUsername);
        textViewDetailActivityTime = findViewById(R.id.textViewDetailActivityTime);
        textViewDetailActivityTitle = findViewById(R.id.textViewDetailActivityTitle);
        textViewDetailActivityDescription = findViewById(R.id.textViewDetailActivityDescription);
        textViewDetailDistance = findViewById(R.id.textViewDetailDistance);
        textViewDetailDuration = findViewById(R.id.textViewDetailDuration);
        textViewDetailPace = findViewById(R.id.textViewDetailPace);
        textViewDetailElevation = findViewById(R.id.textViewDetailElevation);
        textViewDetailCalories = findViewById(R.id.textViewDetailCalories);
        textViewDetailAvgHR = findViewById(R.id.textViewDetailAvgHR);
        textViewDetailMaxHR = findViewById(R.id.textViewDetailMaxHR);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSendComment = findViewById(R.id.buttonSendComment);
        fabBack = findViewById(R.id.fabBack);
        fabShare = findViewById(R.id.fabShare);
    }
    
    private void setupActivityData() {
        // In a real app, we would get this data from the intent extras or from a database
        // For now, we'll just use the sample data already in the layout
        
        // Example of how we might get data from intent:
        // String activityId = getIntent().getStringExtra("activity_id");
        // then fetch activity data from database using this ID
    }
    
    private void setupListeners() {
        fabBack.setOnClickListener(v -> finish());
        
        buttonSendComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // In a real app, we would send this comment to a server
                Toast.makeText(this, "Comment added: " + commentText, Toast.LENGTH_SHORT).show();
                editTextComment.setText("");
            }
        });
        
        fabShare.setOnClickListener(v -> {
            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my activity on Fraga!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                    "I just completed a " + textViewDetailDistance.getText() + 
                    " run in " + textViewDetailDuration.getText() + 
                    " with Fraga! Check it out: http://fraga.app/activity/123");
            
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }
} 