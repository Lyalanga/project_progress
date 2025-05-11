package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    // Using MaterialButton to match the layout
    private MaterialButton btnSymptoms, btnDiseaseInfo, btnReminders, btnConsultVet;
    private MaterialButton btnLogout, btnReport, btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons from the layout
        initializeViews();

        // Set up all click listeners
        setupClickListeners();
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

        // Set up navigation for login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(LoginActivity.class, "Login");
            }
        });

        // Set up navigation for registration
        // Corrected: Don't finish MainActivity when going to Register
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
                // For logout, we DO want to finish MainActivity
                try {
                    // Consider clearing any user session data here before logging out
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Correctly closes MainActivity for logout
                } catch (Exception e) {
                    Log.e("MainActivity", "Error during logout: " + e.getMessage());
                }
            }
        });
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