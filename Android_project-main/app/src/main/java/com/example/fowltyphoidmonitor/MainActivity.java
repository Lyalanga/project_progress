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

/**
 * MainActivity for Farmers - Fowl Typhoid Monitor App
 *
 * This activity serves as the main dashboard for farmers to:
 * - View their profile and farm statistics
 * - Track symptoms and disease information
 * - Manage reminders and consultations
 * - Receive notifications and alerts
 * - Navigate to various app features
 */
public class MainActivity extends AppCompatActivity implements AppNotificationManager.NotificationListener {

    private static final String TAG = "MainActivity";

    // ========== CONSTANTS ==========
    private static final String PREFS_NAME = "FowlTyphoidMonitorPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";
    private static final int REQUEST_CODE_EDIT_PROFILE = 1001;

    // ========== NOTIFICATION SYSTEM ==========
    private NotificationHelper notificationHelper;
    private AppNotificationManager notificationManager;
    private ImageView notificationBell;
    private TextView notificationBadge;
    private LinearLayout alertsContainer;

    // ========== PROFILE SECTION ==========
    private TextView txtUsername;
    private TextView txtLocation;
    private TextView txtFarmSize;
    private TextView txtTotalChickens;

    // ========== NAVIGATION ==========
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Authentication check
        if (!isUserAuthenticated()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize components in logical order
        initializeNotificationSystem();
        initializeViews();
        setupClickListeners();
        setupBottomNavigation();

        // Load and display data
        loadUserData();
        setupNotificationAlerts();
        updateNotificationBadge();

        // Handle incomplete profile
        handleIncompleteProfile();

        // Add sample notifications for testing
        addSampleNotifications();

        Log.d(TAG, "MainActivity created successfully");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isUserAuthenticated()) {
            redirectToLogin();
            return;
        }

        // Refresh data when returning to activity
        loadUserData();
        updateNotificationBadge();
        Log.d(TAG, "User data reloaded in onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            handleProfileUpdateResult(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupNotificationSystem();
    }

    // ========== AUTHENTICATION METHODS ==========

    private boolean isUserAuthenticated() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        Log.d(TAG, "Authentication check: " + isLoggedIn);
        return isLoggedIn;
    }

    private boolean isProfileComplete() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_PROFILE_COMPLETE, false);
    }

    private void redirectToLogin() {
        Log.d(TAG, "Redirecting to login screen");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleIncompleteProfile() {
        if (!isProfileComplete()) {
            try {
                Log.d(TAG, "Profile incomplete, redirecting to profile setup");
                navigateToActivityForResult(FarmerProfileEditActivity.class, "ProfileEdit", REQUEST_CODE_EDIT_PROFILE);
            } catch (Exception e) {
                Log.e(TAG, "ProfileEditActivity may not exist yet: " + e.getMessage());
            }
        }
    }

    private void handleProfileUpdateResult(Intent data) {
        if (data != null && data.getBooleanExtra(FarmerProfileEditActivity.EXTRA_PROFILE_UPDATED, false)) {
            Log.d(TAG, "Profile updated, reloading data");
            loadUserData();
            Toast.makeText(this, "Wasifu umesasishwa", Toast.LENGTH_SHORT).show();
        }
    }

    // ========== NOTIFICATION SYSTEM ==========

    private void initializeNotificationSystem() {
        try {
            notificationHelper = new NotificationHelper(this);
            notificationManager = AppNotificationManager.getInstance();
            notificationManager.addListener(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing notification system: " + e.getMessage());
            notificationHelper = null;
            notificationManager = null;
        }
    }

    private void cleanupNotificationSystem() {
        if (notificationManager != null) {
            notificationManager.removeListener(this);
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

        if (notifications.isEmpty()) {
            showNoNotificationsMessage();
            return;
        }

        for (NotificationItem notification : notifications) {
            View alertView = createAlertView(notification);
            alertsContainer.addView(alertView);
        }
    }

    private void showNoNotificationsMessage() {
        TextView noNotificationsText = new TextView(this);
        noNotificationsText.setText("Hakuna tahadhari za hivi karibuni");
        noNotificationsText.setTextColor(getColor(android.R.color.darker_gray));
        noNotificationsText.setTextSize(14);
        noNotificationsText.setPadding(32, 24, 32, 24);
        noNotificationsText.setGravity(android.view.Gravity.CENTER);
        alertsContainer.addView(noNotificationsText);
    }

    private View createAlertView(NotificationItem notification) {
        LinearLayout alertLayout = new LinearLayout(this);
        alertLayout.setOrientation(LinearLayout.HORIZONTAL);
        alertLayout.setPadding(32, 24, 32, 24);
        alertLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Set layout parameters
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 20);
        alertLayout.setLayoutParams(layoutParams);

        // Set background based on alert type
        setAlertBackground(alertLayout, notification.getType());

        // Add components
        alertLayout.addView(createAlertIcon(notification.getType()));
        alertLayout.addView(createAlertText(notification.getMessage()));
        alertLayout.addView(createCloseButton(notification.getId()));

        return alertLayout;
    }

    private void setAlertBackground(LinearLayout layout, NotificationItem.AlertType type) {
        switch (type) {
            case CRITICAL:
                layout.setBackgroundColor(0xFFFFEBEE); // Light red
                break;
            case INFO:
                layout.setBackgroundColor(0xFFE3F2FD); // Light blue
                break;
            case WARNING:
                layout.setBackgroundColor(0xFFFFF3E0); // Light orange
                break;
            case SUCCESS:
                layout.setBackgroundColor(0xFFE8F5E8); // Light green
                break;
        }
        layout.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
    }

    private ImageView createAlertIcon(NotificationItem.AlertType type) {
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(64, 64);
        icon.setLayoutParams(iconParams);

        switch (type) {
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

        return icon;
    }

    private TextView createAlertText(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(0xFF1F2937); // Dark gray

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        textParams.setMargins(32, 0, 32, 0);
        textView.setLayoutParams(textParams);

        return textView;
    }

    private ImageView createCloseButton(int notificationId) {
        ImageView closeButton = new ImageView(this);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(48, 48);
        closeButton.setLayoutParams(closeParams);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(0xFF6B7280); // Gray
        closeButton.setPadding(8, 8, 8, 8);
        closeButton.setOnClickListener(v -> {
            notificationManager.dismissNotification(notificationId);
        });

        return closeButton;
    }

    private void openNotificationsPanel() {
        if (notificationManager == null) return;

        // Mark all notifications as read
        List<NotificationItem> unread = notificationManager.getUnreadNotifications();
        for (NotificationItem notification : unread) {
            notificationManager.markAsRead(notification.getId());
        }

        Toast.makeText(this, "Tahadhari zote zimesomwa", Toast.LENGTH_SHORT).show();
    }

    private void updateNotificationBadge() {
        if (notificationManager == null) return;

        int unreadCount = notificationManager.getUnreadCount();

        // Update badge visibility and text
        if (notificationBadge != null) {
            if (unreadCount > 0) {
                notificationBadge.setVisibility(View.VISIBLE);
                notificationBadge.setText(String.valueOf(unreadCount > 99 ? "99+" : unreadCount));
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }

        // Update bell color
        if (notificationBell != null) {
            int color = unreadCount > 0 ? 0xFFF59E0B : 0xFFFFFFFF; // Orange or White
            notificationBell.setColorFilter(color);
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

    private void addSampleNotifications() {
        if (notificationManager == null) return;

        // Add sample notifications for testing
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

    // ========== DATA LOADING METHODS ==========

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String username = prefs.getString("username", "User");
        String location = prefs.getString("location", "Unknown");
        int farmSize = prefs.getInt("farmSize", 0);

        setUserData(username, location, farmSize);

        Log.d(TAG, "Loaded user data - Username: " + username +
                ", Location: " + location + ", Farm Size: " + farmSize);
    }

    private void setUserData(String username, String location, int farmSize) {
        if (txtUsername != null) txtUsername.setText(username);
        if (txtLocation != null) txtLocation.setText("Eneo: " + location);
        if (txtFarmSize != null) txtFarmSize.setText("Idadi ya kuku: " + farmSize);
        if (txtTotalChickens != null) txtTotalChickens.setText(String.valueOf(farmSize));
    }

    // ========== VIEW INITIALIZATION ==========

    private void initializeViews() {
        // Profile section
        txtUsername = findViewById(R.id.txtUsername);
        txtLocation = findViewById(R.id.txtLocation);
        txtFarmSize = findViewById(R.id.txtFarmSize);

        // Stats section
        initializeTotalChickensView();

        // Notification views
        initializeNotificationViews();

        // Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void initializeTotalChickensView() {
        try {
            txtTotalChickens = findViewById(R.id.txtTotalChickens);
            if (txtTotalChickens == null) {
                Log.w(TAG, "txtTotalChickens not found - add android:id=\"@+id/txtTotalChickens\" to your XML layout");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error finding txtTotalChickens: " + e.getMessage());
            txtTotalChickens = null;
        }
    }

    private void initializeNotificationViews() {
        try {
            notificationBell = findViewById(R.id.notificationBell);
        } catch (Exception e) {
            Log.w(TAG, "notificationBell not found: " + e.getMessage());
            notificationBell = null;
        }

        try {
            notificationBadge = findViewById(R.id.notificationBadge);
        } catch (Exception e) {
            Log.w(TAG, "notificationBadge not found: " + e.getMessage());
            notificationBadge = null;
        }

        try {
            CardView alertsCard = findViewById(R.id.alertsCard);
            if (alertsCard != null) {
                alertsContainer = alertsCard.findViewById(R.id.alertsContainer);
            }
        } catch (Exception e) {
            Log.w(TAG, "alertsCard not found: " + e.getMessage());
            alertsContainer = null;
        }
    }

    // ========== CLICK LISTENERS SETUP ==========

    private void setupClickListeners() {
        setupProfileClickListeners();
        setupFeatureClickListeners();
        setupNavigationClickListeners();
    }

    private void setupProfileClickListeners() {
        MaterialButton btnEditProfile = findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                try {
                    Intent editIntent = new Intent(MainActivity.this, FarmerProfileEditActivity.class);
                    startActivityForResult(editIntent, REQUEST_CODE_EDIT_PROFILE);
                } catch (Exception e) {
                    Log.e(TAG, "ProfileEditActivity may not exist yet: " + e.getMessage());
                }
            });
        }
    }

    private void setupFeatureClickListeners() {
        // Symptom tracking
        MaterialButton btnSymptoms = findViewById(R.id.btnSymptoms);
        if (btnSymptoms != null) {
            btnSymptoms.setOnClickListener(v -> {
                createSampleHealthAlert();
                navigateToActivity(SymptomTrackerActivity.class, "SymptomTracker");
            });
        }

        // Disease information
        MaterialButton btnDiseaseInfo = findViewById(R.id.btnDiseaseInfo);
        if (btnDiseaseInfo != null) {
            btnDiseaseInfo.setOnClickListener(v -> {
                navigateToActivity(DiseaseInfoActivity.class, "DiseaseInfo");
            });
        }

        // Reminders
        MaterialButton btnReminders = findViewById(R.id.btnReminders);
        if (btnReminders != null) {
            btnReminders.setOnClickListener(v -> {
                createSampleVaccinationReminder();
                navigateToActivity(ReminderActivity.class, "Reminder");
            });
        }

        // Vet consultation
        MaterialButton btnConsultVet = findViewById(R.id.btnConsultVet);
        if (btnConsultVet != null) {
            btnConsultVet.setOnClickListener(v -> {
                createSampleVetResponse();
                navigateToActivity(VetConsultationActivity.class, "VetConsultation");
            });
        }

        // Report symptoms
        MaterialButton btnReport = findViewById(R.id.btnReport);
        if (btnReport != null) {
            btnReport.setOnClickListener(v -> {
                navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
            });
        }
    }

    private void setupNavigationClickListeners() {
        // Notification bell
        if (notificationBell != null) {
            notificationBell.setOnClickListener(v -> openNotificationsPanel());
        }

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Logout button
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    // ========== SAMPLE NOTIFICATION CREATORS ==========

    private void createSampleHealthAlert() {
        if (notificationHelper != null && notificationManager != null) {
            try {
                notificationHelper.sendHealthAlert(
                        "Dalili za harara zimeonekana katika kundi C. Angalia hali yao haraka.",
                        NotificationHelper.AlertType.DISEASE_OUTBREAK
                );

                notificationManager.addNotification(new NotificationItem(
                        generateId(),
                        "Tahadhari ya Ugonjwa",
                        "Dalili za harara zimeonekana katika kundi C",
                        NotificationItem.AlertType.WARNING
                ));
            } catch (Exception e) {
                Log.e(TAG, "Error creating health alert: " + e.getMessage());
            }
        }
    }

    private void createSampleVaccinationReminder() {
        if (notificationHelper != null && notificationManager != null) {
            try {
                notificationHelper.sendVaccinationReminder("Kundi D", 3);

                notificationManager.addNotification(new NotificationItem(
                        generateId(),
                        "Chanjo Inahitajika",
                        "Chanjo inahitajika kwa Kundi D baada ya siku 3",
                        NotificationItem.AlertType.CRITICAL
                ));
            } catch (Exception e) {
                Log.e(TAG, "Error creating vaccination reminder: " + e.getMessage());
            }
        }
    }

    private void createSampleVetResponse() {
        if (notificationHelper != null && notificationManager != null) {
            try {
                notificationHelper.sendVetResponse("Mwalimu",
                        "Tumia dawa za antibiotics kwa siku 5. Pia hakikisha chakula ni safi.");

                notificationManager.addNotification(new NotificationItem(
                        generateId(),
                        "Jibu la Daktari",
                        "Dkt. Mwalimu amejibu swali lako",
                        NotificationItem.AlertType.INFO
                ));
            } catch (Exception e) {
                Log.e(TAG, "Error creating vet response: " + e.getMessage());
            }
        }
    }

    // ========== BOTTOM NAVIGATION SETUP ==========

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    return true; // Already on home screen
                } else if (itemId == R.id.navigation_report) {
                    navigateToActivity(ReportSymptomsActivity.class, "ReportSymptoms");
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    try {
                        navigateToActivity(ProfileActivity.class, "Profile");
                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "ProfileActivity may not exist yet: " + e.getMessage());
                    }
                } else if (itemId == R.id.navigation_settings) {
                    try {
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

    // ========== UTILITY METHODS ==========

    private void logout() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();

            Log.d(TAG, "User logged out successfully");

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

    private void navigateToActivity(Class<?> targetActivity, String activityName) {
        try {
            startActivity(new Intent(MainActivity.this, targetActivity));
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to " + activityName + ": " + e.getMessage());
        }
    }

    private void navigateToActivityForResult(Class<?> targetActivity, String activityName, int requestCode) {
        try {
            Intent intent = new Intent(MainActivity.this, targetActivity);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to " + activityName + ": " + e.getMessage());
        }
    }
}