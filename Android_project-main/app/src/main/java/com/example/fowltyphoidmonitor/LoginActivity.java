package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton btnLogin;
    private TextView forgotPassword, btnRegister;
    private ImageView googleLogin, facebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        btnRegister = findViewById(R.id.btnRegister);
        googleLogin = findViewById(R.id.googleLogin);
        facebookLogin = findViewById(R.id.facebookLogin);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Perform login operation
                    performLogin();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password
                Toast.makeText(LoginActivity.this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to register activity
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Google login
                Toast.makeText(LoginActivity.this, "Google login feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Facebook login
                Toast.makeText(LoginActivity.this, "Facebook login feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(email)) {
            emailInputLayout.setError("Please enter a valid email");
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }

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
        // For now, just show a success message and navigate to main activity
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish(); // Close the login activity
    }
}