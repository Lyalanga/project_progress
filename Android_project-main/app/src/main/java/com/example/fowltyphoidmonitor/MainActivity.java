package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    // Profile section views - keep as fields since they're accessed in setUserData()
    private TextView txtUsername, txtLocation, txtFarmSize;

    // Navigation elements - keep bottomNavigation as it's used in multiple methods
    private BottomNavigationView bottomNavigation;

    // Authentication constants
    private static final String PREFS_NAME = "FowlTyphoidMonitorPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check authentication before showing the main screen
        if (!isUserLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to login screen");
            redirectToLogin();
            return; // Important: stop executing onCreate if redirecting
        }

        setContentView(R.layout.activity_main);

        // Initialize views from the layout
        initializeViews();

        // Set up all click listeners
        setupClickListeners();

        // Set up bottom navigation
        setupBottomNavigation();

        // Set user data - in a real app, you would fetch this from a database or shared preferences
        setUserData("John Lyalanga", "Arusha", 50);

        Log.d(TAG, "MainActivity created successfully");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication when returning to this activity
        if (!isUserLoggedIn()) {
            Log.d(TAG, "User not logged in (onResume), redirecting to login screen");
            redirectToLogin();
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        Log.d(TAG, "isUserLoggedIn check: " + isLoggedIn);
        return isLoggedIn;
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

    private void setUserData(String username, String location, int chickenCount) {
        txtUsername.setText(username);
        txtLocation.setText("Eneo: " + location);
        txtFarmSize.setText("Idadi ya kuku: " + chickenCount);
    }

    private void initializeViews() {
        // Profile section - keep these as they're used in setUserData()
        txtUsername = findViewById(R.id.txtUsername);
        txtLocation = findViewById(R.id.txtLocation);
        txtFarmSize = findViewById(R.id.txtFarmSize);

        // Convert the rest to local variables since they're only used in this method or setupClickListeners()
        CircleImageView profileImage = findViewById(R.id.profileImage);
        MaterialButton btnEditProfile = findViewById(R.id.btnEditProfile);
        MaterialButton btnSymptoms = findViewById(R.id.btnSymptoms);
        MaterialButton btnDiseaseInfo = findViewById(R.id.btnDiseaseInfo);
        MaterialButton btnReport = findViewById(R.id.btnReport);
        MaterialButton btnReminders = findViewById(R.id.btnReminders);
        MaterialButton btnConsultVet = findViewById(R.id.btnConsultVet);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Language, History, and Privacy buttons aren't in the current layout
        // If you want to add them in the future, you'd need to update the XML layout
        ImageButton btnLanguage = null;
        ImageButton btnHistory = null;
        ImageButton btnPrivacy = null;

        // Set up all click listeners right here instead of in a separate method
        setupButtonListeners(btnEditProfile, btnSymptoms, btnDiseaseInfo, btnReminders,
                btnConsultVet, btnReport, btnLogout, btnBack, btnLanguage,
                btnHistory, btnPrivacy);
    }

    private void setupButtonListeners(MaterialButton btnEditProfile, MaterialButton btnSymptoms,
                                      MaterialButton btnDiseaseInfo, MaterialButton btnReminders,
                                      MaterialButton btnConsultVet, MaterialButton btnReport,
                                      MaterialButton btnLogout, ImageButton btnBack,
                                      ImageButton btnLanguage, ImageButton btnHistory,
                                      ImageButton btnPrivacy) {
        // Back button click listener
        btnBack.setOnClickListener(v -> onBackPressed());

        // Icon buttons click listeners - only if they exist
        if (btnLanguage != null) {
            btnLanguage.setOnClickListener(v -> {
                navigateToActivity(LanguageSettingsActivity.class, "LanguageSettings");
            });
        }

        // Only set these click listeners if the buttons exist and are initialized
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> {
                navigateToActivity(HistoryActivity.class, "History");
            });
        }

        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v -> {
                navigateToActivity(PrivacySettingsActivity.class, "PrivacySettings");
            });
        }

        // Profile section
        btnEditProfile.setOnClickListener(v -> {
            try {
                // Navigate to profile editing screen
                navigateToActivity(ProfileEditActivity.class, "ProfileEdit");
            } catch (Exception e) {
                Log.e(TAG, "ProfileEditActivity may not exist yet: " + e.getMessage());
            }
        });

        // Set up navigation for symptom tracking
        btnSymptoms.setOnClickListener(v -> {
            navigateToActivity(SymptomTrackerActivity.class, "SymptomTracker");
        });

        // Set up navigation for disease information
        btnDiseaseInfo.setOnClickListener(v -> {
            navigateToActivity(DiseaseInfoActivity.class, "DiseaseInfo");
        });

        // Set up navigation for reminders
        btnReminders.setOnClickListener(v -> {
            navigateToActivity(ReminderActivity.class, "Reminder");
        });

        // Set up navigation for vet consultation
        btnConsultVet.setOnClickListener(v -> {
            navigateToActivity(VetConsultationActivity.class, "VetConsultation");
        });

        // Set up navigation for reporting symptoms
        btnReport.setOnClickListener(v -> {
            navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
        });

        // Set up logout function
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void setupClickListeners() {
        // This method is now empty since we're handling click listeners in initializeViews
        // You can remove this method, but I'm keeping it here to maintain the same structure
        // as your original code for easier comparison
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                // Already on home screen
                return true;
            } else if (itemId == R.id.navigation_report) {
                navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
                return true;
            } else if (itemId == R.id.navigation_profile) {
                try {
                    // Navigate to profile screen - replace with your actual profile activity
                    navigateToActivity(ProfileActivity.class, "Profile");
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "ProfileActivity may not exist yet: " + e.getMessage());
                }
            } else if (itemId == R.id.navigation_settings) {
                try {
                    // Navigate to settings screen - replace with your actual settings activity
                    navigateToActivity(SettingsActivity.class, "Settings");
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "SettingsActivity may not exist yet: " + e.getMessage());
                }
            }

            return false;
        });
    }

    private void logout() {
        try {
            // Clear login state
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();

            Log.d(TAG, "User logged out successfully");

            // Redirect to login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
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
            Log.e(TAG, "Error navigating to " + activityName + ": " + e.getMessage());
        }
    }
}