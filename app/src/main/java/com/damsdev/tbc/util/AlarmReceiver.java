package com.damsdev.tbc.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.damsdev.tbc.AlarmActivity;
import com.damsdev.tbc.util.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//        }
        Log.d("ALARM", "Sukses");
        showNotify(context, 0, "Jangan lupa minum obat \uD83D\uDE0A");
    }

    private void showNotify(Context context, int id, String message) {
        NotificationHelper notificationHelper = new NotificationHelper(context, message);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(id, nb.build());
        Log.d("COBA", "Notifikasi dimunculkan");
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

