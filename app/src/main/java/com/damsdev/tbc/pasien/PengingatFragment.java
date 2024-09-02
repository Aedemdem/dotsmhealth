package com.damsdev.tbc.pasien;

import static android.Manifest.permission.POST_NOTIFICATIONS;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.damsdev.tbc.MainActivity;
import com.damsdev.tbc.R;
import com.damsdev.tbc.databinding.DialogTimePickerBinding;
import com.damsdev.tbc.databinding.FragmentPengingatBinding;
import com.damsdev.tbc.util.AlarmReceiver;
import com.damsdev.tbc.util.SharedPrefManager;

import java.util.Calendar;
import java.util.Objects;

public class PengingatFragment extends Fragment {
    private static final String CHANNEL_ID = "Alarm_Channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQ_CODE_ALARM = 0;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    Context context;
    SharedPrefManager sharedPrefManager;
    private FragmentPengingatBinding binding;
    private AlarmManager alarmManager;
    private int hour, minutes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
        sharedPrefManager = new SharedPrefManager(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPengingatBinding.inflate(inflater, container, false);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, REQ_CODE_ALARM, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        activateOnBooting();

        cekStatusAlarm();

        binding.mySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                int _hour = Integer.parseInt(sharedPrefManager.getSpAlarmHour());
                int _minutes = Integer.parseInt(sharedPrefManager.getSpAlarmMinuts());
                sharedPrefManager.saveString(SharedPrefManager.SP_IS_ALARM_AKTIF, "true");
                setAlarm(_hour, _minutes);
            } else {
                sharedPrefManager.saveString(SharedPrefManager.SP_IS_ALARM_AKTIF, "false");
                cancelAlarm();
            }
        });

        binding.btnAddAlarm.setOnClickListener(view -> {
            showTimePicker();
        });

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void cekStatusAlarm() {
        if (sharedPrefManager.getSpIsAlarmAktif().equals("true")) {
            binding.tvAlarm.setTextColor(getResources().getColor(R.color.white));
            binding.mySwitch.setText("Aktif");
            binding.mySwitch.setChecked(true);
        } else {
            binding.tvAlarm.setTextColor(getResources().getColor(R.color.inactive));
            binding.mySwitch.setText("Nonaktif");
            binding.mySwitch.setChecked(false);
        }

        binding.tvAlarm.setText(sharedPrefManager.getSpAlarmHour() + ":" + sharedPrefManager.getSpAlarmMinuts());
    }

    @SuppressLint("SetTextI18n")
    private void showTimePicker() {
        Dialog dialog = new Dialog(context);
        DialogTimePickerBinding timePickerBinding = DialogTimePickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(timePickerBinding.getRoot());
        timePickerBinding.timePicker.setIs24HourView(true);
        timePickerBinding.btnAtur.setOnClickListener(view -> {
            hour = timePickerBinding.timePicker.getHour();
            minutes = timePickerBinding.timePicker.getMinute();
            setAlarm(hour, minutes);
            dialog.cancel();
        });
        timePickerBinding.btnBatal.setOnClickListener(view -> dialog.cancel());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestNotificationPermission();
    }

    public void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{POST_NOTIFICATIONS}, 1);
        }
    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

//        nextAlarm();

        sharedPrefManager.saveString(SharedPrefManager.SP_ALARM_HOUR, String.valueOf(hour));
        sharedPrefManager.saveString(SharedPrefManager.SP_ALARM_MINUTS, String.valueOf(minute));
        sharedPrefManager.saveString(SharedPrefManager.SP_IS_ALARM_AKTIF, "true");
        cekStatusAlarm();

        Toast.makeText(context, "Alarm daitur pada " + hour + ":" + minute, Toast.LENGTH_LONG).show();

        Log.d("GARIS_WAKTU", "");

    }

    private void cancelAlarm() {
        // If the alarm has been set, cancel it.
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, "Alarm dinonaktifkan", Toast.LENGTH_SHORT).show();
        }
        cekStatusAlarm();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void activateOnBooting() {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void showAlarmNotification() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Triggered")
                .setContentText("Tap to turn off alarm")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

//    public void signOut() {
//        // [START auth_fui_signout]
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                        Toast.makeText(PengingatActivity.this, "Anda logout", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(PengingatActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//        // [END auth_fui_signout]
//    }

}

