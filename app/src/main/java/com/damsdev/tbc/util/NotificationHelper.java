package com.damsdev.tbc.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.damsdev.tbc.LoginActivity;
import com.damsdev.tbc.R;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private final String message;

    private NotificationManager mManager;

    public NotificationHelper(Context base, String message) {
        super(base);
        this.message = message;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
//        AudioAttributes attributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build();

//        Uri path = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.dola);
        long[] pattern = {0, 100, 1000, 400, 50, 1000};

        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setVibrationPattern(pattern);
//        channel.setSound(path, attributes); // This is IMPORTANT
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public NotificationCompat.Builder getChannelNotification() {
        //  Uri path = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.dola);
        long[] pattern = {0, 100, 1000, 400, 50, 1000};

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setVibrate(pattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mengingatkan")
                .setContentText(message)
                // .setSound(path)
                .setAutoCancel(false)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
    }
}
