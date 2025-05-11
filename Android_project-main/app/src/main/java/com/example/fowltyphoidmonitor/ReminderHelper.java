package com.example.fowltyphoidmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderHelper {
    public static void setReminder(Context context, long triggerTime) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}
