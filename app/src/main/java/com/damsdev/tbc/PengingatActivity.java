package com.damsdev.tbc;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.damsdev.tbc.databinding.ActivityPengingatBinding;
import com.damsdev.tbc.databinding.DialogTimePickerBinding;
import com.damsdev.tbc.util.AlarmReceiver;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Objects;

public class PengingatActivity extends AppCompatActivity {
    private ActivityPengingatBinding binding;
    private AlarmManager alarmManager;

    Intent alarmIntent;
    PendingIntent pendingIntent;
    Context context;
    private static final String CHANNEL_ID = "Alarm_Channel";
    private static final int NOTIFICATION_ID = 1;
    private int hour, minutes;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPengingatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(PengingatActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(PengingatActivity.this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        activateOnBooting();

        binding.btnAddAlarm.setOnClickListener(view -> {
            showTimePicker();
        });

        binding.btnCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

    }


    private void showTimePicker() {
        Dialog dialog = new Dialog(PengingatActivity.this);
        DialogTimePickerBinding timePickerBinding = DialogTimePickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(timePickerBinding.getRoot());
        timePickerBinding.timePicker.setIs24HourView(true);
        timePickerBinding.btnAtur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = timePickerBinding.timePicker.getHour();
                minutes = timePickerBinding.timePicker.getMinute();
                setAlarm(hour,minutes);
                binding.tvAlarm.setText(hour+":"+minutes);
                dialog.cancel();
            }
        });
        timePickerBinding.btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestNotificationPermission();
    }

    public void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 1);
        }
    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

//        nextAlarm();


        Toast.makeText(PengingatActivity.this, "Alarm set at " + hour + ":" + minute, Toast.LENGTH_SHORT).show();

    }



    private void cancelAlarm(){
        // If the alarm has been set, cancel it.
        if (alarmManager!= null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showAlarmNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Triggered")
                .setContentText("Tap to turn off alarm")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
//                        Toast.makeText(PengingatActivity.this, "Anda logout", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PengingatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        // [END auth_fui_signout]
    }

    private void activateOnBooting(){
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    // Mengaktifkan penerima booting manifest(misalnya, jika pengguna menyetel alarm)
//    ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
//    PackageManager pm = context.getPackageManager();
//
//    pm.setComponentEnabledSetting(receiver,
//    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//    PackageManager.DONT_KILL_APP);

    // mematikan penerima booting manifest, mematikan alarm
//    ComponentName receiver = new ComponentName(context, BootReceiver.class);
//    PackageManager pm = context.getPackageManager();
//
//    pm.setComponentEnabledSetting(receiver,
//    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//    PackageManager.DONT_KILL_APP);
}