package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton btnLogin;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initializeViews();
        setupClickListeners();

        // Check if user is already logged in
        checkLoginStatus();
    }

    private void initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    performLogin();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to register activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("FowlTyphoidMonitorPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Navigate directly to dashboard
            navigateToDashboard();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(email)) {
            emailInputLayout.setError("Please enter a valid email");
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Please enter your password");
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Verify credentials against stored data
        if (verifyCredentials(email, password)) {
            // Login successful
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Update login status
            SharedPreferences preferences = getSharedPreferences("FowlTyphoidMonitorPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            // Navigate to dashboard
            navigateToDashboard();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verifyCredentials(String email, String password) {
        SharedPreferences preferences = getSharedPreferences("FowlTyphoidMonitorPrefs", MODE_PRIVATE);
        String storedEmail = preferences.getString("userEmail", "");
        String storedPassword = preferences.getString("userPassword", "");

        return email.equals(storedEmail) && password.equals(storedPassword);
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Close login activity
    }
}