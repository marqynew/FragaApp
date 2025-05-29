package com.example.fraga;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fraga.models.UserProfile;
import com.example.fraga.repositories.UserProfileRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutFullName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputEditText editTextFullName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewSignIn;
    
    private FirebaseAuth mAuth;
    private UserProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        profileRepository = new UserProfileRepository();

        // Initialize views
        textInputLayoutFullName = findViewById(R.id.textInputLayoutFullName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        // Set up listeners
        buttonRegister.setOnClickListener(v -> attemptRegistration());
        
        textViewSignIn.setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        // Reset errors
        textInputLayoutFullName.setError(null);
        textInputLayoutEmail.setError(null);
        textInputLayoutPassword.setError(null);
        textInputLayoutConfirmPassword.setError(null);

        // Get values
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid full name
        if (TextUtils.isEmpty(fullName)) {
            textInputLayoutFullName.setError("Full name is required");
            focusView = editTextFullName;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError("Password is required");
            focusView = editTextPassword;
            cancel = true;
        } else if (password.length() < 6) {
            textInputLayoutPassword.setError("Password is too short");
            focusView = editTextPassword;
            cancel = true;
        }

        // Check for password confirmation
        if (!password.equals(confirmPassword)) {
            textInputLayoutConfirmPassword.setError("Passwords do not match");
            focusView = editTextConfirmPassword;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError("Email is required");
            focusView = editTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            textInputLayoutEmail.setError("Invalid email address");
            focusView = editTextEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show loading indicator
            buttonRegister.setEnabled(false);
            
            // Attempt to register with Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Update user profile with full name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();

                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(profileTask -> {
                                if (profileTask.isSuccessful()) {
                                    // Create user profile in Firestore
                                    createUserProfile(fullName, email);
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                        "Failed to update profile: " + profileTask.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                    buttonRegister.setEnabled(true);
                                }
                            });
                    } else {
                        Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                        buttonRegister.setEnabled(true);
                    }
                });
        }
    }

    private void createUserProfile(String fullName, String email) {
        String userId = mAuth.getCurrentUser().getUid();
        UserProfile profile = new UserProfile(userId, fullName);
        profile.setEmail(email); // Add email to profile
        
        profileRepository.createProfile(profile, task -> {
            if (task.isSuccessful()) {
            registerSuccess();
            } else {
                Toast.makeText(RegisterActivity.this,
                    "Failed to create profile: " + task.getException().getMessage(),
                    Toast.LENGTH_SHORT).show();
                buttonRegister.setEnabled(true);
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void registerSuccess() {
        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
        finish();
    }
} 