package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    // Request code for profile editing
    private static final int REQUEST_CODE_EDIT_PROFILE = 1001;

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

        // Load user data from SharedPreferences
        loadUserData();

        Log.d(TAG, "MainActivity created successfully");

        // Check if profile is complete, if not redirect to profile creation
        if (!isProfileComplete()) {
            try {
                Log.d(TAG, "Profile incomplete, redirecting to profile setup");
                navigateToActivityForResult(ProfileEditActivity.class, "ProfileEdit", REQUEST_CODE_EDIT_PROFILE);
            } catch (Exception e) {
                Log.e(TAG, "ProfileEditActivity may not exist yet: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication when returning to this activity
        if (!isUserLoggedIn()) {
            Log.d(TAG, "User not logged in (onResume), redirecting to login screen");
            redirectToLogin();
            return;
        }

        // IMPORTANT: Reload user data when returning from other activities
        // This ensures profile updates are reflected immediately
        loadUserData();
        Log.d(TAG, "User data reloaded in onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            // Profile was updated, reload the data
            if (data != null && data.getBooleanExtra(ProfileEditActivity.EXTRA_PROFILE_UPDATED, false)) {
                Log.d(TAG, "Profile updated, reloading data");
                loadUserData();
                Toast.makeText(this, "Wasifu umesasishwa", Toast.LENGTH_SHORT).show();
            }
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close MainActivity so they can't go back without logging in
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // FIXED: Use consistent keys with ProfileActivity and ProfileEditActivity
        String username = prefs.getString("username", "User");
        String location = prefs.getString("location", "Unknown");
        int farmSize = prefs.getInt("farmSize", 0); // Changed from "chickenCount" to "farmSize"

        setUserData(username, location, farmSize);

        Log.d(TAG, "Loaded user data - Username: " + username +
                ", Location: " + location + ", Farm Size: " + farmSize);
    }

    private void setUserData(String username, String location, int farmSize) {
        txtUsername.setText(username);
        txtLocation.setText("Eneo: " + location);
        txtFarmSize.setText("Idadi ya kuku: " + farmSize); // farmSize instead of chickenCount
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

        // Set up all click listeners right here instead of in a separate method
        setupButtonListeners(btnEditProfile, btnSymptoms, btnDiseaseInfo, btnReminders,
                btnConsultVet, btnReport, btnLogout, btnBack);
    }

    private void setupButtonListeners(MaterialButton btnEditProfile, MaterialButton btnSymptoms,
                                      MaterialButton btnDiseaseInfo, MaterialButton btnReminders,
                                      MaterialButton btnConsultVet, MaterialButton btnReport,
                                      MaterialButton btnLogout, ImageButton btnBack) {
        // Back button click listener
        btnBack.setOnClickListener(v -> onBackPressed());

        // Profile section - ENHANCED: Use startActivityForResult for better communication
        btnEditProfile.setOnClickListener(v -> {
            try {
                Intent editIntent = new Intent(MainActivity.this, ProfileEditActivity.class);
                startActivityForResult(editIntent, REQUEST_CODE_EDIT_PROFILE);
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

    /**
     * Helper method to navigate to different activities with result
     * @param targetActivity The class of the activity to navigate to
     * @param activityName Name for logging purposes
     * @param requestCode Request code for the activity result
     */
    private void navigateToActivityForResult(Class<?> targetActivity, String activityName, int requestCode) {
        try {
            Intent intent = new Intent(MainActivity.this, targetActivity);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to " + activityName + ": " + e.getMessage());
        }
    }
}