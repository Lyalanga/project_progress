package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    // Using MaterialButton to match the layout
    private MaterialButton btnSymptoms, btnDiseaseInfo, btnReminders, btnConsultVet;
    private MaterialButton btnLogout, btnReport, btnRegister, btnLogin;

    // Authentication constants
    private static final String PREFS_NAME = "FowlTyphoidMonitorPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check authentication before showing the main screen
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return; // Important: stop executing onCreate if redirecting
        }

        setContentView(R.layout.activity_main);

        // Initialize buttons from the layout
        initializeViews();

        // Set up all click listeners
        setupClickListeners();

        // Update UI based on login state
        updateAuthenticationUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication when returning to this activity
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Update UI if needed
        updateAuthenticationUI();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private boolean isProfileComplete() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_PROFILE_COMPLETE, false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity so they can't go back without logging in
    }

    private void updateAuthenticationUI() {
        // If user is logged in, hide login/register buttons
        if (isUserLoggedIn()) {
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            // This should never execute as we redirect in onCreate/onResume
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        btnSymptoms = findViewById(R.id.btnSymptoms);
        btnDiseaseInfo = findViewById(R.id.btnDiseaseInfo);
        btnReminders = findViewById(R.id.btnReminders);
        btnConsultVet = findViewById(R.id.btnConsultVet);
        btnLogout = findViewById(R.id.btnLogout);
        btnReport = findViewById(R.id.btnReport);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupClickListeners() {
        // Set up navigation for symptom tracking
        btnSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(SymptomTrackerActivity.class, "SymptomTracker");
            }
        });

        // Set up navigation for disease information
        btnDiseaseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(DiseaseInfoActivity.class, "DiseaseInfo");
            }
        });

        // Set up navigation for reminders
        btnReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(ReminderActivity.class, "Reminder");
            }
        });

        // Set up navigation for vet consultation
        btnConsultVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(VetConsultationActivity.class, "VetConsultation");
            }
        });

        // Set up navigation for reporting symptoms
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
            }
        });

        // Set up navigation for login - in a completed login system, this would likely be removed
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(LoginActivity.class, "Login");
            }
        });

        // Set up navigation for registration - in a completed login system, this would likely be removed
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(RegisterActivity.class, "Register");
            }
        });

        // Set up logout function
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        try {
            // Clear login state
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();

            // Redirect to login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Error during logout: " + e.getMessage());
        }
    }

    /**
     * Helper method to navigate to different activities
     * @param targetActivity The class of the activity to navigate to
     * @param activityName Name for logging purposes
     */
    private void navigateToActivity(Class<?> targetActivity, String activityName) {
        try {
            startActivity(new Intent(MainActivity.this, targetActivity));
        } catch (Exception e) {
            Log.e("MainActivity", "Error navigating to " + activityName + ": " + e.getMessage());
        }
    }
}