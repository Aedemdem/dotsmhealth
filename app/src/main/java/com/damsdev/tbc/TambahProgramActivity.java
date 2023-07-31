package com.damsdev.tbc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.damsdev.tbc.databinding.ActivityTambahProgramBinding;
import com.damsdev.tbc.databinding.DialogDatePickerBinding;
import com.damsdev.tbc.model.AktivitasModel;
import com.damsdev.tbc.util.DbReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class TambahProgramActivity extends AppCompatActivity {
    String LOG = "LOG_TAMBAH_ACTIVITY";
    String idPasien, tglMulai, tglSelesai = "";
    private ActivityTambahProgramBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefPasien, dbAktivitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTambahProgramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idPasien = getIntent().getStringExtra("idPasien");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbAktivitas = firebaseDatabase.getReference(DbReference.AKTIVITAS);

        binding.etTglMulai.setOnClickListener(view -> {
            showDatePicker("1");
        });

        binding.etTglSelesai.setOnClickListener(view -> {
            showDatePicker("2");
        });

        binding.progressBar.setVisibility(View.GONE);
        binding.btnSimpan.setOnClickListener(view -> {
            if (isValid()) {
                binding.btnSimpan.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                tambahAktivitas();
            }
        });
    }

    private void showDatePicker(String s) {
        Dialog dialog = new Dialog(this);
        DialogDatePickerBinding dialogDatePickerBinding = DialogDatePickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogDatePickerBinding.getRoot());
        dialogDatePickerBinding.datePicker.setMinDate(System.currentTimeMillis() - 1000);
        dialogDatePickerBinding.btnAtur.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                String date = dialogDatePickerBinding.datePicker.getYear() + "/" + (dialogDatePickerBinding.datePicker.getMonth() + 1) + "/" + dialogDatePickerBinding.datePicker.getDayOfMonth();
                SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd");
                Date newDate = null;
                try {
                    newDate = spf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd MMMM yyyy");
                date = spf.format(newDate != null ? newDate : "");

                SimpleDateFormat sdfSend = new SimpleDateFormat("dd-MM-yyyy");
                String sendDate = "";
                sendDate = sdfSend.format(newDate != null ? newDate : "");

                if (s.equals("1")) {
                    tglMulai = sendDate;
                    binding.etTglMulai.setText(date);
                } else if (s.equals("2")) {
                    tglSelesai = sendDate;
                    binding.etTglSelesai.setText(date);
                }

                dialog.cancel();
            }
        });
        dialogDatePickerBinding.btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    private boolean isValid() {
//        try {
//            tglMulai = Objects.requireNonNull(binding.etTglMulai.getText()).toString();
//            tglSelesai = Objects.requireNonNull(binding.etTglSelesai.getText()).toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (tglMulai.isEmpty()) {
            binding.etTglMulai.setError("Wajib diisi");
            return false;
        }

        if (tglSelesai.isEmpty()) {
            binding.etTglSelesai.setError("Wajib diisi");
            return false;
        }

        return true;
    }

    private void tambahAktivitas() {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
//        Date firstDate = null;
//        Date secondDate = null;
//        try {
//            firstDate = sdf.parse(tglMulai);
//            secondDate = sdf.parse(tglSelesai);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
//        long total = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        AktivitasModel aktivitasModel = new AktivitasModel(idPasien, "", tglMulai, tglSelesai);
        dbAktivitas.push().setValue(aktivitasModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(LOG, "Post aktivitas suksess");
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahProgramActivity.this, "Gagal menyimpan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}