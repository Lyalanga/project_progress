package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "FowlTyphoidMonitorPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";
    private static final String KEY_USER_ROLE = "userRole";  // new key for user role

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, redirecting to main screen");
            redirectToMain();
            return; // Stop executing onCreate if redirecting
        }

        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Set up click listeners
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to registration activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish(); // Close LoginActivity to prevent going back
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to forgot password activity
                try {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to ForgotPasswordActivity: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Password recovery feature coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validateCredentials(username, password)) {
            // Detect role based on username pattern (simple logic)
            String role;
            if (username.toLowerCase().startsWith("vet")) {
                role = "vet";
            } else {
                role = "farmer";
            }

            saveLoginState(username, password, role);

            redirectToMain();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateCredentials(String username, String password) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");

        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            return username.equals(savedUsername) && password.equals(savedPassword);
        }

        // Accept any credentials if none saved (for demo only)
        return true;
    }

    // Modified method to save user role as well
    private void saveLoginState(String username, String password, String role) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ROLE, role);  // save role here

        if (!prefs.contains(KEY_PROFILE_COMPLETE)) {
            editor.putBoolean(KEY_PROFILE_COMPLETE, false);
        }

        editor.apply();

        Log.d(TAG, "User logged in successfully: " + username + " with role: " + role);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        Log.d(TAG, "isUserLoggedIn check: " + isLoggedIn);
        return isLoggedIn;
    }

    // Redirect based on saved role
    private void redirectToMain() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String role = prefs.getString(KEY_USER_ROLE, "farmer"); // default to farmer

        Intent intent;
        if (role.equals("vet")) {
            intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
