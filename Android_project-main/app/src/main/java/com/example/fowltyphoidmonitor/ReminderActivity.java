package com.example.fowltyphoidmonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ReminderActivity extends AppCompatActivity {
    private MaterialButton addReminderBtn;
    private ListView reminderListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize views
        addReminderBtn = findViewById(R.id.addReminderBtn);
        reminderListView = findViewById(R.id.reminderListView);

        // Set click listener for add reminder button
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReminderDialog();
            }
        });

        // Initialize reminder list
        setupReminderList();
    }

    private void showAddReminderDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_reminder, null);

        EditText editTitle = dialogView.findViewById(R.id.editReminderTitle);
        EditText editTime = dialogView.findViewById(R.id.editReminderTime);

        new AlertDialog.Builder(this)
                .setTitle("Ongeza Kikumbusho")
                .setView(dialogView)
                .setPositiveButton("Hifadhi", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String time = editTime.getText().toString().trim();

                    if (!title.isEmpty() && !time.isEmpty()) {
                        Toast.makeText(this, "Kikumbusho: " + title + " saa " + time, Toast.LENGTH_LONG).show();
                        // TODO: Add logic to save the reminder and update the list
                    } else {
                        Toast.makeText(this, "Tafadhali jaza sehemu zote", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Ghairi", null)
                .show();
    }

    private void setupReminderList() {
        // TODO: Initialize your ListView with an adapter
        // Example:
        // ReminderAdapter adapter = new ReminderAdapter(this, yourReminderList);
        // reminderListView.setAdapter(adapter);
    }
}
