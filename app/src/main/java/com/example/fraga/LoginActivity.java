package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        // Set up listeners
        buttonLogin.setOnClickListener(v -> attemptLogin());
        
        textViewForgotPassword.setOnClickListener(v -> {
            // Show forgot password dialog or navigate to forgot password screen
            Toast.makeText(LoginActivity.this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        textViewSignUp.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Reset errors
        textInputLayoutEmail.setError(null);
        textInputLayoutPassword.setError(null);

        // Get values
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

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
            // There was an error; don't attempt login and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // For demo purposes, we'll just show a success message and proceed to main activity
            // In a real app, you would authenticate with a server
            loginSuccess();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void loginSuccess() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        
        // Navigate to the main activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the login activity so user can't go back to it using back button
    }
} 