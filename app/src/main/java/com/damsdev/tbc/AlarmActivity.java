package com.damsdev.tbc;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.damsdev.tbc.databinding.ActivityAlarmBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.model.RequestModel;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
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

        playMedia();

        binding.tvSudah.setOnClickListener(view -> {
            mediaPlayer.stop();
            postAktivitas();
        });

        binding.tvBelum.setOnClickListener(view -> {
            mediaPlayer.stop();
            finish();
        });
    }

    private void postAktivitas() {
        if (!cekSudahInput()) {
            AktivitasDetailModel model = new AktivitasDetailModel(sharedPrefManager.getSpIdAktivitas(), "", date, "y");
            dbDetailAktivitas.push().setValue(model).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Tersimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "\uD83D\uDC4D", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean cekSudahInput(){
        return sharedPrefManager.getSpTerakhirPost().equals(date);
    }

    private void playMedia() {
        mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.alarm_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

}