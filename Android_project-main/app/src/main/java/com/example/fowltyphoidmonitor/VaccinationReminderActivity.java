package com.example.fowltyphoidmonitor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class VaccinationReminderActivity extends AppCompatActivity {

    EditText edtDate, edtTime;
    Button btnSetReminder;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccination_reminder);

        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        btnSetReminder = findViewById(R.id.btnSetReminder);

        calendar = Calendar.getInstance();

        edtDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this, (view, y, m, d) -> {
                calendar.set(Calendar.YEAR, y);
                calendar.set(Calendar.MONTH, m);
                calendar.set(Calendar.DAY_OF_MONTH, d);
                edtDate.setText(d + "/" + (m+1) + "/" + y);
            }, year, month, day).show();
        });

        edtTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(this, (view, h, m) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                edtTime.setText(h + ":" + m);
            }, hour, minute, true).show();
        });

        btnSetReminder.setOnClickListener(v -> {
            ReminderHelper.setReminder(this, calendar.getTimeInMillis());
            Toast.makeText(this, "Kumbusho la chanjo limehifadhiwa!", Toast.LENGTH_SHORT).show();
        });
    }
}
