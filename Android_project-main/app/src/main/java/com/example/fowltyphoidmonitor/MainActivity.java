package com.example.fowltyphoidmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AppNotificationManager.NotificationListener {

    // Notification system components
    private NotificationHelper notificationHelper;
    private AppNotificationManager notificationManager;
    private ImageView notificationBell;
    private LinearLayout alertsContainer;
    private TextView notificationBadge;

    // Profile section views - keep as fields since they're accessed in setUserData()
    private TextView txtUsername, txtLocation, txtFarmSize;

    // ADDED: TextView for the dynamic chicken count in the stats card
    private TextView txtTotalChickens;

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

        // Initialize notification components
        initializeNotificationSystem();

        // Initialize views from the layout
        initializeViews();

        // Set up all click listeners
        setupClickListeners();

        // Set up bottom navigation
        setupBottomNavigation();

        // Load user data from SharedPreferences
        loadUserData();

        // Setup notification alerts
        setupNotificationAlerts();
        updateNotificationBadge();

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

        // Add some sample notifications for testing
        addSampleNotifications();
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
        updateNotificationBadge(); // Update badge when returning to activity
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationManager != null) {
            notificationManager.removeListener(this);
        }
    }

    // Notification System Methods
    private void initializeNotificationSystem() {
        try {
            notificationHelper = new NotificationHelper(this);
            notificationManager = AppNotificationManager.getInstance();
            notificationManager.addListener(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing notification system: " + e.getMessage());
            // Continue without notification system if classes don't exist
            notificationHelper = null;
            notificationManager = null;
        }
    }

    private void setupNotificationAlerts() {
        if (alertsContainer != null && notificationManager != null) {
            displayNotificationAlerts();
        }
    }

    private void displayNotificationAlerts() {
        if (alertsContainer == null || notificationManager == null) return;

        alertsContainer.removeAllViews();
        List<NotificationItem> notifications = notificationManager.getUnreadNotifications();

        // If no notifications, show a message
        if (notifications.isEmpty()) {
            TextView noNotificationsText = new TextView(this);
            noNotificationsText.setText("Hakuna tahadhari za hivi karibuni");
            noNotificationsText.setTextColor(getColor(android.R.color.darker_gray));
            noNotificationsText.setTextSize(14);
            noNotificationsText.setPadding(32, 24, 32, 24);
            noNotificationsText.setGravity(android.view.Gravity.CENTER);
            alertsContainer.addView(noNotificationsText);
            return;
        }

        for (NotificationItem notification : notifications) {
            View alertView = createAlertView(notification);
            alertsContainer.addView(alertView);
        }
    }

    private View createAlertView(NotificationItem notification) {
        // Create the alert layout programmatically
        LinearLayout alertLayout = new LinearLayout(this);
        alertLayout.setOrientation(LinearLayout.HORIZONTAL);
        alertLayout.setPadding(32, 24, 32, 24);
        alertLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 20);
        alertLayout.setLayoutParams(layoutParams);

        // Set background based on alert type using colors instead of drawable resources
        switch (notification.getType()) {
            case CRITICAL:
                alertLayout.setBackgroundColor(0xFFFFEBEE); // Light red background
                break;
            case INFO:
                alertLayout.setBackgroundColor(0xFFE3F2FD); // Light blue background
                break;
            case WARNING:
                alertLayout.setBackgroundColor(0xFFFFF3E0); // Light orange background
                break;
            case SUCCESS:
                alertLayout.setBackgroundColor(0xFFE8F5E8); // Light green background
                break;
        }

        // Set corner radius programmatically
        alertLayout.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));

        // Add icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(64, 64);
        icon.setLayoutParams(iconParams);

        switch (notification.getType()) {
            case CRITICAL:
                icon.setImageResource(android.R.drawable.ic_dialog_alert);
                icon.setColorFilter(0xFFDC2626); // Red
                break;
            case INFO:
                icon.setImageResource(android.R.drawable.ic_dialog_info);
                icon.setColorFilter(0xFF2563EB); // Blue
                break;
            case WARNING:
                icon.setImageResource(android.R.drawable.ic_dialog_alert);
                icon.setColorFilter(0xFFF59E0B); // Orange
                break;
            case SUCCESS:
                icon.setImageResource(android.R.drawable.checkbox_on_background);
                icon.setColorFilter(0xFF10B981); // Green
                break;
        }

        // Add text
        TextView textView = new TextView(this);
        textView.setText(notification.getMessage());
        textView.setTextSize(14);
        textView.setTextColor(0xFF1F2937); // Dark gray
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        textParams.setMargins(32, 0, 32, 0);
        textView.setLayoutParams(textParams);

        // Add close button
        ImageView closeButton = new ImageView(this);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(48, 48);
        closeButton.setLayoutParams(closeParams);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(0xFF6B7280); // Gray
        closeButton.setPadding(8, 8, 8, 8);
        closeButton.setOnClickListener(v -> {
            notificationManager.dismissNotification(notification.getId());
        });

        alertLayout.addView(icon);
        alertLayout.addView(textView);
        alertLayout.addView(closeButton);

        return alertLayout;
    }

    private void openNotificationsPanel() {
        if (notificationManager == null) return;

        // Mark all notifications as read when opening the panel
        List<NotificationItem> unread = notificationManager.getUnreadNotifications();
        for (NotificationItem notification : unread) {
            notificationManager.markAsRead(notification.getId());
        }

        // You can also create a dedicated NotificationsActivity here
        Toast.makeText(this, "Tahadhari zote zimesomwa", Toast.LENGTH_SHORT).show();
    }

    private void updateNotificationBadge() {
        if (notificationManager == null) return;

        int unreadCount = notificationManager.getUnreadCount();

        // Update the notification badge
        if (notificationBadge != null) {
            if (unreadCount > 0) {
                notificationBadge.setVisibility(View.VISIBLE);
                notificationBadge.setText(String.valueOf(unreadCount > 99 ? "99+" : unreadCount));
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }

        // Change notification bell color based on unread count
        if (notificationBell != null) {
            if (unreadCount > 0) {
                notificationBell.setColorFilter(0xFFF59E0B); // Orange/Yellow when there are notifications
            } else {
                notificationBell.setColorFilter(0xFFFFFFFF); // White when no notifications
            }
        }

        Log.d(TAG, "Notification badge updated: " + unreadCount + " unread notifications");
    }

    @Override
    public void onNotificationsChanged() {
        runOnUiThread(() -> {
            if (notificationManager != null) {
                displayNotificationAlerts();
                updateNotificationBadge();
            }
        });
    }

    // Add sample notifications for testing
    private void addSampleNotifications() {
        if (notificationManager == null) return;

        // Add some sample notifications
        notificationManager.addNotification(new NotificationItem(
                generateId(),
                "Chanjo Inahitajika",
                "Chanjo inahitajika kwa Kundi A baada ya siku 2",
                NotificationItem.AlertType.CRITICAL
        ));

        notificationManager.addNotification(new NotificationItem(
                generateId(),
                "Ukumbusho",
                "Ukumbusho wa matibabu kwa Kundi B",
                NotificationItem.AlertType.INFO
        ));

        notificationManager.addNotification(new NotificationItem(
                generateId(),
                "Jibu la Daktari",
                "Dkt. Smith amejibu swali lako",
                NotificationItem.AlertType.INFO
        ));
    }

    // Authentication Methods
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

    // Data Loading Methods
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
        if (txtUsername != null) txtUsername.setText(username);
        if (txtLocation != null) txtLocation.setText("Eneo: " + location);
        if (txtFarmSize != null) txtFarmSize.setText("Idadi ya kuku: " + farmSize);

        // ADDED: Update the total chickens count in the stats card
        if (txtTotalChickens != null) {
            txtTotalChickens.setText(String.valueOf(farmSize));
        }
    }

    // View Initialization Methods
    private void initializeViews() {
        // Profile section - keep these as they're used in setUserData()
        txtUsername = findViewById(R.id.txtUsername);
        txtLocation = findViewById(R.id.txtLocation);
        txtFarmSize = findViewById(R.id.txtFarmSize);

        // ADDED: Initialize the total chickens TextView from the stats card
        try {
            txtTotalChickens = findViewById(R.id.txtTotalChickens);
            if (txtTotalChickens == null) {
                Log.w(TAG, "txtTotalChickens not found - you need to add android:id=\"@+id/txtTotalChickens\" to the TextView showing '450' in your XML layout");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error finding txtTotalChickens: " + e.getMessage());
            txtTotalChickens = null;
        }

        // Notification views - with null checks for optional elements
        try {
            notificationBell = findViewById(R.id.notificationBell);
        } catch (Exception e) {
            Log.w(TAG, "notificationBell not found in layout: " + e.getMessage());
            notificationBell = null;
        }

        try {
            notificationBadge = findViewById(R.id.notificationBadge);
        } catch (Exception e) {
            Log.w(TAG, "notificationBadge not found in layout: " + e.getMessage());
            notificationBadge = null;
        }

        try {
            CardView alertsCard = findViewById(R.id.alertsCard);
            if (alertsCard != null) {
                alertsContainer = alertsCard.findViewById(R.id.alertsContainer);
            }
        } catch (Exception e) {
            Log.w(TAG, "alertsCard not found in layout: " + e.getMessage());
            alertsContainer = null;
        }

        // Bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        // UI elements - converted to local variables since they're only used here
        CircleImageView profileImage = findViewById(R.id.profileImage);
        MaterialButton btnEditProfile = findViewById(R.id.btnEditProfile);
        MaterialButton btnSymptoms = findViewById(R.id.btnSymptoms);
        MaterialButton btnDiseaseInfo = findViewById(R.id.btnDiseaseInfo);
        MaterialButton btnReport = findViewById(R.id.btnReport);
        MaterialButton btnReminders = findViewById(R.id.btnReminders);
        MaterialButton btnConsultVet = findViewById(R.id.btnConsultVet);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Notification bell click listener
        if (notificationBell != null) {
            notificationBell.setOnClickListener(v -> openNotificationsPanel());
        }

        // Back button click listener
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Profile section - ENHANCED: Use startActivityForResult for better communication
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                try {
                    Intent editIntent = new Intent(MainActivity.this, ProfileEditActivity.class);
                    startActivityForResult(editIntent, REQUEST_CODE_EDIT_PROFILE);
                } catch (Exception e) {
                    Log.e(TAG, "ProfileEditActivity may not exist yet: " + e.getMessage());
                }
            });
        }

        // Set up navigation for symptom tracking
        if (btnSymptoms != null) {
            btnSymptoms.setOnClickListener(v -> {
                // Create a sample health alert for testing (only if notification system is available)
                if (notificationHelper != null && notificationManager != null) {
                    try {
                        notificationHelper.sendHealthAlert(
                                "Dalili za harara zimeonekana katika kundi C. Angalia hali yao haraka.",
                                NotificationHelper.AlertType.DISEASE_OUTBREAK
                        );

                        // Add to in-app notifications
                        notificationManager.addNotification(new NotificationItem(
                                generateId(),
                                "Tahadhari ya Ugonjwa",
                                "Dalili za harara zimeonekana katika kundi C",
                                NotificationItem.AlertType.WARNING
                        ));
                    } catch (Exception e) {
                        Log.e(TAG, "Error creating notification: " + e.getMessage());
                    }
                }

                navigateToActivity(SymptomTrackerActivity.class, "SymptomTracker");
            });
        }

        // Set up navigation for disease information
        if (btnDiseaseInfo != null) {
            btnDiseaseInfo.setOnClickListener(v -> {
                navigateToActivity(DiseaseInfoActivity.class, "DiseaseInfo");
            });
        }

        // Set up navigation for reminders
        if (btnReminders != null) {
            btnReminders.setOnClickListener(v -> {
                // Create a vaccination reminder for testing (only if notification system is available)
                if (notificationHelper != null && notificationManager != null) {
                    try {
                        notificationHelper.sendVaccinationReminder("Kundi D", 3);

                        // Add to in-app notifications
                        notificationManager.addNotification(new NotificationItem(
                                generateId(),
                                "Chanjo Inahitajika",
                                "Chanjo inahitajika kwa Kundi D baada ya siku 3",
                                NotificationItem.AlertType.CRITICAL
                        ));
                    } catch (Exception e) {
                        Log.e(TAG, "Error creating notification: " + e.getMessage());
                    }
                }

                navigateToActivity(ReminderActivity.class, "Reminder");
            });
        }

        // Set up navigation for vet consultation
        if (btnConsultVet != null) {
            btnConsultVet.setOnClickListener(v -> {
                // Simulate vet response for testing (only if notification system is available)
                if (notificationHelper != null && notificationManager != null) {
                    try {
                        notificationHelper.sendVetResponse("Mwalimu",
                                "Tumia dawa za antibiotics kwa siku 5. Pia hakikisha chakula ni safi.");

                        // Add to in-app notifications
                        notificationManager.addNotification(new NotificationItem(
                                generateId(),
                                "Jibu la Daktari",
                                "Dkt. Mwalimu amejibu swali lako",
                                NotificationItem.AlertType.INFO
                        ));
                    } catch (Exception e) {
                        Log.e(TAG, "Error creating notification: " + e.getMessage());
                    }
                }

                navigateToActivity(VetConsultationActivity.class, "VetConsultation");
            });
        }

        // Set up navigation for reporting symptoms
        if (btnReport != null) {
            btnReport.setOnClickListener(v -> {
                navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
            });
        }

        // Set up logout function
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                logout();
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
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
    }

    // Utility Methods
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

    private int generateId() {
        return (int) System.currentTimeMillis();
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