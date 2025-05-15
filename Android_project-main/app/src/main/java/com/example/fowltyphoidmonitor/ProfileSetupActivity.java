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

public class ProfileSetupActivity extends AppCompatActivity {

    private TextInputLayout farmNameLayout, locationLayout, flockSizeLayout;
    private TextInputEditText farmNameInput, locationInput, flockSizeInput;
    private MaterialButton btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        farmNameLayout = findViewById(R.id.farmNameLayout);
        locationLayout = findViewById(R.id.locationLayout);
        flockSizeLayout = findViewById(R.id.flockSizeLayout);

        farmNameInput = findViewById(R.id.farmNameInput);
        locationInput = findViewById(R.id.locationInput);
        flockSizeInput = findViewById(R.id.flockSizeInput);

        btnComplete = findViewById(R.id.btnComplete);
    }

    private void setupClickListeners() {
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Save profile info and navigate to dashboard
                    saveProfileAndNavigate();
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String farmName = farmNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String flockSizeStr = flockSizeInput.getText().toString().trim();

        // Validate farm name
        if (TextUtils.isEmpty(farmName)) {
            farmNameLayout.setError("Please enter your farm name");
            isValid = false;
        } else {
            farmNameLayout.setError(null);
        }

        // Validate location
        if (TextUtils.isEmpty(location)) {
            locationLayout.setError("Please enter your farm location");
            isValid = false;
        } else {
            locationLayout.setError(null);
        }

        // Validate flock size
        if (TextUtils.isEmpty(flockSizeStr)) {
            flockSizeLayout.setError("Please enter your flock size");
            isValid = false;
        } else {
            try {
                int flockSize = Integer.parseInt(flockSizeStr);
                if (flockSize <= 0) {
                    flockSizeLayout.setError("Flock size must be greater than 0");
                    isValid = false;
                } else {
                    flockSizeLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                flockSizeLayout.setError("Please enter a valid number");
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveProfileAndNavigate() {
        // Get farm information
        String farmName = farmNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        int flockSize = Integer.parseInt(flockSizeInput.getText().toString().trim());

        // Save farm data to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("FowlTyphoidMonitorPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("farmName", farmName);
        editor.putString("location", location);
        editor.putInt("flockSize", flockSize);
        editor.putBoolean("profileCompleted", true);

        editor.apply();

        Toast.makeText(this, "Profile setup complete!", Toast.LENGTH_SHORT).show();

        // Navigate to the dashboard/main activity
        Intent intent = new Intent(ProfileSetupActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
}