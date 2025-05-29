package com.example.fraga;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.UserProfileRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    
    private ImageView imageViewProfilePic;
    private MaterialButton buttonChangePhoto;
    private TextInputLayout textInputLayoutFullName;
    private TextInputLayout textInputLayoutLocation;
    private TextInputLayout textInputLayoutBio;
    private TextInputEditText editTextFullName;
    private TextInputEditText editTextLocation;
    private TextInputEditText editTextBio;
    private MaterialButton buttonSave;
    
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private UserProfileRepository profileRepository;
    private Uri selectedImageUri;
    
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(imageViewProfilePic);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        profileRepository = new UserProfileRepository();
        
        // Initialize views
        initializeViews();
        
        // Load current profile
        loadCurrentProfile();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void initializeViews() {
        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        buttonChangePhoto = findViewById(R.id.buttonChangePhoto);
        textInputLayoutFullName = findViewById(R.id.textInputLayoutFullName);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        textInputLayoutBio = findViewById(R.id.textInputLayoutBio);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextBio = findViewById(R.id.editTextBio);
        buttonSave = findViewById(R.id.buttonSave);
    }
    
    private void loadCurrentProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        profileRepository.getProfile(userId, task -> {
            if (task.isSuccessful()) {
                UserProfile profile = task.getResult();
                if (profile != null) {
                    editTextFullName.setText(profile.getFullName());
                    editTextLocation.setText(profile.getLocation());
                    editTextBio.setText(profile.getBio());
                    
                    if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
                        Glide.with(this)
                            .load(profile.getProfileImageUrl())
                            .circleCrop()
                            .into(imageViewProfilePic);
                    }
                }
            } else {
                Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupClickListeners() {
        buttonChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
        
        buttonSave.setOnClickListener(v -> saveProfile());
    }
    
    private void saveProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        String fullName = editTextFullName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String bio = editTextBio.getText().toString().trim();
        
        if (fullName.isEmpty()) {
            textInputLayoutFullName.setError("Full name is required");
            return;
        }
        
        buttonSave.setEnabled(false);
        buttonSave.setText("Saving...");
        
        // Update profile without changing image
        updateProfile(userId, fullName, location, bio, null);
    }
    
    private void updateProfile(String userId, String fullName, String location, String bio, String imageUrl) {
        try {
            UserProfile profile = new UserProfile(userId, fullName);
            profile.setLocation(location);
            profile.setBio(bio);
            if (imageUrl != null) {
                profile.setProfileImageUrl(imageUrl);
            }
            
            Log.d(TAG, "Updating profile for user: " + userId);
            
            // Update profile in Firestore
            profileRepository.updateProfile(profile, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Profile updated successfully");
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to update profile", task.getException());
                    String errorMessage = "Failed to update profile: ";
                    if (task.getException() != null) {
                        errorMessage += task.getException().getMessage();
                    } else {
                        errorMessage += "Unknown error";
                    }
                    Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    buttonSave.setText("Save Changes");
                    buttonSave.setEnabled(true);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception during profile update", e);
            Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            buttonSave.setText("Save Changes");
            buttonSave.setEnabled(true);
        }
    }
} 