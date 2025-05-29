package com.example.fraga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns; // Untuk validasi email yang lebih baik
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.entity.User;
import com.example.fraga.utils.PasswordUtils; // Impor PasswordUtils
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String PREFS_NAME = "FragaPrefs";
    public static final String KEY_USER_ID = "current_user_id";
    public static final String KEY_USER_EMAIL = "current_user_email";

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;
    private ProgressBar progressBarLogin;

    private AppDatabase appDb;
    private ExecutorService databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseExecutor = Executors.newSingleThreadExecutor();
        appDb = AppDatabase.getInstance(getApplicationContext());

        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        progressBarLogin = findViewById(R.id.progressBarLogin);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1) != -1;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> attemptLogin());

        textViewForgotPassword.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Forgot password feature will be implemented later.", Toast.LENGTH_SHORT).show();
        });

        textViewSignUp.setOnClickListener(v -> {
            // Navigasi ke SignUpActivity (Anda perlu membuat Activity ini)
            // Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            // startActivity(intent);
            // Untuk sekarang, kita bisa tetap memanggil createDummyUser jika SignUpActivity belum ada
            Toast.makeText(LoginActivity.this, "Navigating to Sign Up (or creating test user).", Toast.LENGTH_LONG).show();
            createDummyUserForTestingIfNeeded();
        });
    }

    private void createDummyUserForTestingIfNeeded() {
        databaseExecutor.execute(() -> {
            User existingUser = appDb.userDao().findUserByEmail("test@example.com");
            if (existingUser == null) {
                String plainPassword = "password";
                byte[] salt = PasswordUtils.generateSalt();
                byte[] hashedPassword = PasswordUtils.hashPassword(plainPassword, salt);

                String saltHex = PasswordUtils.bytesToHexString(salt);
                String hashedPasswordHex = PasswordUtils.bytesToHexString(hashedPassword);

                if (saltHex == null || hashedPasswordHex == null) {
                    Log.e(TAG, "Error generating hex for salt or password for dummy user.");
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error creating test user.", Toast.LENGTH_SHORT).show());
                    return;
                }

                User testUser = new User("Test User Fraga", "test@example.com", hashedPasswordHex, saltHex, System.currentTimeMillis());
                appDb.userDao().insertUser(testUser); // Sebaiknya gunakan insertUserIfNotExists jika ada constraint email unik
                Log.i(TAG, "Dummy user 'test@example.com' created with hashed password.");
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Test user (test@example.com/password) created.", Toast.LENGTH_SHORT).show());
            } else {
                Log.i(TAG, "Dummy user 'test@example.com' already exists.");
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Test user (test@example.com/password) already exists.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Reset errors
        textInputLayoutEmail.setError(null);
        textInputLayoutPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError("Email is required");
            focusView = editTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            textInputLayoutEmail.setError("Invalid email address");
            focusView = editTextEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError("Password is required");
            if (focusView == null) focusView = editTextPassword;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) focusView.requestFocus();
            return;
        }

        if (progressBarLogin != null) progressBarLogin.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);
        Log.d(TAG, "Attempting login for: " + email);

        databaseExecutor.execute(() -> {
            User user = appDb.userDao().findUserByEmail(email);
            boolean loginSuccessful = false;
            String errorMessage = "Invalid email or password."; // Pesan error default
            int fetchedUserId = -1;
            String fetchedUserEmail = null;

            if (user != null) {
                byte[] salt = PasswordUtils.hexStringToBytes(user.getSaltHex());
                byte[] storedHash = PasswordUtils.hexStringToBytes(user.getHashedPasswordHex());

                if (salt != null && storedHash != null && PasswordUtils.verifyPassword(password, storedHash, salt)) {
                    loginSuccessful = true;
                    fetchedUserId = user.getId();
                    fetchedUserEmail = user.getEmail();
                } else {
                    errorMessage = "Incorrect password.";
                }
            } else {
                errorMessage = "Email not found.";
            }

            final boolean finalLoginSuccessful = loginSuccessful;
            final String finalErrorMessage = errorMessage;
            final int finalUserId = fetchedUserId;
            final String finalUserEmail = fetchedUserEmail;

            runOnUiThread(() -> {
                if (progressBarLogin != null) progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);

                if (finalLoginSuccessful) {
                    Log.i(TAG, "Login successful for user: " + finalUserEmail + " with ID: " + finalUserId);
                    loginSuccess(finalUserId, finalUserEmail);
                } else {
                    if ("Email not found.".equals(finalErrorMessage)) {
                        textInputLayoutEmail.setError(finalErrorMessage);
                        if(editTextEmail != null) editTextEmail.requestFocus();
                    } else { // Incorrect password atau error lain
                        textInputLayoutPassword.setError(finalErrorMessage);
                        if(editTextPassword != null) editTextPassword.requestFocus();
                        if(editTextPassword != null) editTextPassword.setText(""); // Kosongkan field password
                    }
                    Log.w(TAG, "Login failed: " + finalErrorMessage + " for email: " + email);
                }
            });
        });
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginSuccess(int userId, String userEmail) {
        Toast.makeText(this, "Login successful! Welcome.", Toast.LENGTH_SHORT).show();
        if (userId != -1) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_USER_ID, userId);
            editor.putString(KEY_USER_EMAIL, userEmail);
            editor.apply();
            Log.i(TAG, "User session saved. UserID: " + userId + ", Email: " + userEmail);
        }
        navigateToMainActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseExecutor != null && !databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
        }
    }
}