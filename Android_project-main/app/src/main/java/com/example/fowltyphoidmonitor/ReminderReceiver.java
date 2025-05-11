package com.example.fowltyphoidmonitor;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "vaccination_channel";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelId, "Vaccination Reminders", NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Kumbusho la Chanjo")
                .setContentText("Ni muda wa kuwachanja kuku wako dhidi ya Fowl Typhoid.")
                .setSmallIcon(R.drawable.ic_vaccine)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
