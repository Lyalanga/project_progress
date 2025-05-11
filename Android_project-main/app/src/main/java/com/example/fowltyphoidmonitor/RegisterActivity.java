package com.example.fowltyphoidmonitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private MaterialButton btnRegister;
    private TextView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Perform registration
                    performRegistration();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to login activity
                onBackPressed();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError("Please enter your name");
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

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
            passwordInputLayout.setError("Please enter a password");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordInputLayout.setError(null);
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performRegistration() {
        // For now, just show a success message and navigate to login activity
        Toast.makeText(this, "Registration successful! Please login with your credentials", Toast.LENGTH_LONG).show();

        // Return to login screen
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the register activity
    }
}