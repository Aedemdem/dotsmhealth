package com.damsdev.tbc;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.damsdev.tbc.databinding.ActivityAlarmBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.util.AlarmReceiver;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ActivityAlarmBinding binding;

    //
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbDetailAktivitas;
    private SharedPrefManager sharedPrefManager;

    //
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    private static String LOG = "LOG_ALARM_ACTIVITY";

    //
    private AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent alarmIntent;
    private static final int REQ_CODE_ALARM = 0;
    private static final int REQ_CODE_ALARM_2 = 0;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        sharedPrefManager = new SharedPrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbDetailAktivitas = firebaseDatabase.getReference(DbReference.DETAIL_AKTIVITAS);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        date = dateFormat.format(calendar.getTime());

        alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            Log.e(LOG, "Error setting alarm: " + e.getMessage());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, REQ_CODE_ALARM, alarmIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, REQ_CODE_ALARM, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        playMedia();

        binding.tvSudah.setOnClickListener(view -> {
            mediaPlayer.stop();
            cekSudahInput();
        });

        binding.tvBelum.setOnClickListener(view -> {
            mediaPlayer.stop();
            alarmManager.cancel(pendingIntent);
            finish();
        });
    }

    private void postAktivitas() {
        AktivitasDetailModel model = new AktivitasDetailModel(sharedPrefManager.getSpIdAktivitas(), "", date, "y");
        dbDetailAktivitas.push().setValue(model).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Tersimpan", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cekSudahInput() {
        Calendar myCalendar = Calendar.getInstance();
        DateFormat myDateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String today = myDateFormat.format(myCalendar.getTime());

        Query query = dbDetailAktivitas.orderByChild("idAktivitas").equalTo(sharedPrefManager.getSpIdAktivitas());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listTgl = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    Log.d(LOG, "CEK INPUT : Id Ditemukan");
                    for (DataSnapshot dst : dataSnapshot.getChildren()) {
                        Log.d(LOG, "Tgl : " + dst.child("tgl").getValue());
                        listTgl.add(Objects.requireNonNull(dst.child("tgl").getValue()).toString());
                    }

                    if (!listTgl.contains(today)) {
                        Log.d(LOG, "Tgl : !contain today");
                        postAktivitas();
                    }

                } else {
                    // No similar data found, proceed to push
                    Log.d(LOG, "CEK INPUT : Tidak ada gas input");
                    Log.d(LOG, "idAktivitas tdk ditemukan");
                    postAktivitas();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void playMedia() {
        mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.alarm_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

}