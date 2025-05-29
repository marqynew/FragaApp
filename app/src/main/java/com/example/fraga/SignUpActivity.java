package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.User;
import com.example.fraga.utils.PasswordUtils; // Impor kelas utilitas kita
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private TextInputLayout textInputLayoutName, textInputLayoutEmailSignUp,
            textInputLayoutPasswordSignUp, textInputLayoutConfirmPasswordSignUp;
    private TextInputEditText editTextName, editTextEmailSignUp,
            editTextPasswordSignUp, editTextConfirmPasswordSignUp;
    private Button buttonSignUp;
    private ProgressBar progressBarSignUp;
    private TextView textViewLoginLink;

    private AppDatabase appDb;
    private ExecutorService databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        appDb = AppDatabase.getInstance(getApplicationContext());
        databaseExecutor = Executors.newSingleThreadExecutor();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        textInputLayoutName = findViewById(R.id.textInputLayoutNameSignUp);
        editTextName = findViewById(R.id.editTextNameSignUp);
        textInputLayoutEmailSignUp = findViewById(R.id.textInputLayoutEmailSignUp);
        editTextEmailSignUp = findViewById(R.id.editTextEmailSignUp);
        textInputLayoutPasswordSignUp = findViewById(R.id.textInputLayoutPasswordSignUp);
        editTextPasswordSignUp = findViewById(R.id.editTextPasswordSignUp);
        textInputLayoutConfirmPasswordSignUp = findViewById(R.id.textInputLayoutConfirmPasswordSignUp);
        editTextConfirmPasswordSignUp = findViewById(R.id.editTextConfirmPasswordSignUp);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBarSignUp = findViewById(R.id.progressBarSignUp);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);
    }

    private void setupListeners() {
        buttonSignUp.setOnClickListener(v -> attemptSignUp());
        textViewLoginLink.setOnClickListener(v -> {
            // Kembali ke LoginActivity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup SignUpActivity
        });
    }

    private void attemptSignUp() {
        // Reset errors
        textInputLayoutName.setError(null);
        textInputLayoutEmailSignUp.setError(null);
        textInputLayoutPasswordSignUp.setError(null);
        textInputLayoutConfirmPasswordSignUp.setError(null);

        String name = editTextName.getText().toString().trim();
        String email = editTextEmailSignUp.getText().toString().trim();
        String password = editTextPasswordSignUp.getText().toString().trim();
        String confirmPassword = editTextConfirmPasswordSignUp.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            textInputLayoutName.setError("Name is required");
            focusView = editTextName;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmailSignUp.setError("Email is required");
            focusView = editTextEmailSignUp;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayoutEmailSignUp.setError("Invalid email address");
            focusView = editTextEmailSignUp;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            textInputLayoutPasswordSignUp.setError("Password is required");
            focusView = editTextPasswordSignUp;
            cancel = true;
        } else if (password.length() < 6) { // Minimal 6 karakter
            textInputLayoutPasswordSignUp.setError("Password too short (min 6 characters)");
            focusView = editTextPasswordSignUp;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            textInputLayoutConfirmPasswordSignUp.setError("Confirm password is required");
            focusView = editTextConfirmPasswordSignUp;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            textInputLayoutConfirmPasswordSignUp.setError("Passwords do not match");
            focusView = editTextConfirmPasswordSignUp;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
            return;
        }

        if (progressBarSignUp != null) progressBarSignUp.setVisibility(View.VISIBLE);
        buttonSignUp.setEnabled(false);

        databaseExecutor.execute(() -> {
            // Cek apakah email sudah ada
            User existingUser = appDb.userDao().findUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> {
                    if (progressBarSignUp != null) progressBarSignUp.setVisibility(View.GONE);
                    buttonSignUp.setEnabled(true);
                    textInputLayoutEmailSignUp.setError("Email already registered");
                    editTextEmailSignUp.requestFocus();
                });
                return;
            }

            // Buat salt dan hash password
            byte[] salt = PasswordUtils.generateSalt();
            byte[] hashedPassword = PasswordUtils.hashPassword(password, salt);

            String saltHex = PasswordUtils.bytesToHexString(salt);
            String hashedPasswordHex = PasswordUtils.bytesToHexString(hashedPassword);

            User newUser = new User(name, email, hashedPasswordHex, saltHex, System.currentTimeMillis());
            // Anda bisa set field lain seperti location jika ada inputnya

            long newUserId = appDb.userDao().insertUserIfNotExists(newUser); // Gunakan insertUserIfNotExists

            runOnUiThread(() -> {
                if (progressBarSignUp != null) progressBarSignUp.setVisibility(View.GONE);
                buttonSignUp.setEnabled(true);
                if (newUserId > 0) {
                    Toast.makeText(SignUpActivity.this, "Sign up successful! Please login.", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "New user created with ID: " + newUserId + ", Email: " + email);
                    // Navigasi ke LoginActivity atau langsung login dan ke MainActivity
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (newUserId == -1) {
                    Toast.makeText(SignUpActivity.this, "Email already registered. Please login.", Toast.LENGTH_LONG).show();
                    textInputLayoutEmailSignUp.setError("Email already registered");
                    editTextEmailSignUp.requestFocus();
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to insert new user into database.");
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseExecutor != null && !databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
        }
    }
}