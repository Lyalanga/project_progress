package com.example.fowltyphoidmonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderActivity extends AppCompatActivity {
    private MaterialButton addReminderBtn;
    private ListView reminderListView;
    private ArrayList<String> reminderList;
    private ArrayAdapter<String> adapter;

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
        TimePicker timePicker = dialogView.findViewById(R.id.timePickerReminder);

        // Set 24-hour format
        timePicker.setIs24HourView(true);

        new AlertDialog.Builder(this)
                .setTitle("Ongeza Kikumbusho")
                .setView(dialogView)
                .setPositiveButton("Hifadhi", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();

                    if (!title.isEmpty()) {
                        // Get time from TimePicker
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        // Create calendar for today with selected time
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        // If time has passed today, schedule for tomorrow
                        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        long triggerTime = calendar.getTimeInMillis();

                        // Set reminder using ReminderHelper
                        int requestCode = (int) System.currentTimeMillis(); // Unique request code
                        ReminderHelper.setReminder(this, triggerTime, title, requestCode);

                        // Add to list display
                        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        String reminderText = title + " - " + timeStr;
                        reminderList.add(reminderText);
                        adapter.notifyDataSetChanged();

                        // Format display message
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String displayTime = sdf.format(new Date(triggerTime));

                        Toast.makeText(this, "Kikumbusho kimewekwa: " + title + "\nMuda: " + displayTime,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Tafadhali jaza jina la kikumbusho", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Ghairi", null)
                .show();
    }

    private void setupReminderList() {
        reminderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderList);
        reminderListView.setAdapter(adapter);

        // Add sample reminder to show how it works
        reminderList.add("Mfano: Chanjo ya kwanza - 08:00");
        adapter.notifyDataSetChanged();
    }
}