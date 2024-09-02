package com.damsdev.tbc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.databinding.ActivityRegisterBinding;
import com.damsdev.tbc.databinding.DialogDatePickerBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.model.UserModel;
import com.damsdev.tbc.util.DbReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    static final String LOG = "LOG_REGISTER_ACTIVITY";
    boolean isRadioSebagaiChecked, isRadioKelaminChecked;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefUser, dbRefPasien, dbRefNakes;
    String dipilih = "";
    String kelamin = "";
    ActivityRegisterBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private String nama, tglLahir, alamat, noHp, email, pendidikan, pekerjaan, mulaiPengobatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Registrasi");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        dbRefUser = firebaseDatabase.getReference(DbReference.USER);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);
        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);

        binding.btnSimpan.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

        binding.linear1.setVisibility(View.GONE);
        binding.linear2.setVisibility(View.GONE);

        binding.etTglLahir.setOnClickListener(view -> {
            showDatePicker();
        });

        binding.etMulaiPengobatan.setOnClickListener(view -> {
            showDatePickerPengobatan();
        });

        String[] arrayListPendidikan = getResources().getStringArray(R.array.pendidikan);
        ArrayAdapter pendidikan = new ArrayAdapter(this, R.layout.dropdown_pendidikan, arrayListPendidikan);
        binding.etPendidikan.setAdapter(pendidikan);

        binding.btnSimpan.setOnClickListener(view -> {
            if (isValid()) {
                binding.btnSimpan.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                simpan();
//                Toast.makeText(this, "Valid", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(this, "Tidak valid", Toast.LENGTH_SHORT).show();
            }
//            Intent intent = new Intent(RegisterActivity.this, RequestActivity.class);
//            Log.d(LOG, "Intent to ReequestActivity");
//            startActivity(intent);
        });

        getCurrentUser(firebaseUser);
    }

    private void getCurrentUser(FirebaseUser user) {
        email = user.getEmail();
        binding.tvEmail.setText(email);
        Glide.with(this)
                .asBitmap()
                .load(user.getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user)
                .dontAnimate()
                .into(binding.ivProfil);
    }

    private void showDatePicker() {
        Dialog dialog = new Dialog(this);
        DialogDatePickerBinding dialogDatePickerBinding = DialogDatePickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogDatePickerBinding.getRoot());
        dialogDatePickerBinding.datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        dialogDatePickerBinding.btnAtur.setOnClickListener(new View.OnClickListener() {
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

                binding.etTglLahir.setText(date);

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

    private void showDatePickerPengobatan() {
        Dialog dialog = new Dialog(this);
        DialogDatePickerBinding dialogDatePickerBinding = DialogDatePickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogDatePickerBinding.getRoot());
        dialogDatePickerBinding.datePicker.setMaxDate(System.currentTimeMillis()+24*60*60*1000);
        dialogDatePickerBinding.btnAtur.setOnClickListener(new View.OnClickListener() {
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

                binding.etMulaiPengobatan.setText(date);

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


    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonKelaminClicked(View view) {
        isRadioKelaminChecked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbLakiLaki:
                if (isRadioKelaminChecked) {
                    kelamin = "L";
                }
                break;
            case R.id.rbPerempuan:
                if (isRadioKelaminChecked) {
                    kelamin = "P";
                }
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        isRadioSebagaiChecked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbNakes:
                if (isRadioSebagaiChecked) {
                    dipilih = "nakes";
                    binding.linear1.setVisibility(View.VISIBLE);
                    binding.linear2.setVisibility(View.GONE);
                }
                break;
            case R.id.rbPasien:
                if (isRadioSebagaiChecked) {
                    dipilih = "pasien";
                    binding.linear1.setVisibility(View.VISIBLE);
                    binding.linear2.setVisibility(View.VISIBLE);
                }
                break;
        }
        binding.btnSimpan.setVisibility(View.VISIBLE);
    }

    private boolean isValid() {
        try {
            nama = Objects.requireNonNull(binding.etNama.getText()).toString();
            tglLahir = Objects.requireNonNull(binding.etTglLahir.getText()).toString();
            alamat = Objects.requireNonNull(binding.etAlamat.getText()).toString();
            noHp = Objects.requireNonNull(binding.etNoHp.getText()).toString();
            pendidikan = Objects.requireNonNull(binding.etPendidikan.getText()).toString();
            pekerjaan = Objects.requireNonNull(binding.etPekerjaan.getText()).toString();
            mulaiPengobatan = Objects.requireNonNull(binding.etMulaiPengobatan.getText()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isRadioSebagaiChecked) {
            Toast.makeText(this, "Pilih salah satu, sebagai pasien atau nakes", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (nama.isEmpty()) {
            binding.etNama.setError("Wajib diisi");
            return false;
        }
        binding.etNama.setError(null);

        if (tglLahir.isEmpty()) {
            binding.etTglLahir.setError("Wajib diisi");
            return false;
        }
        binding.etTglLahir.setError(null);
        if (alamat.isEmpty()) {
            binding.etAlamat.setError("Wajib diisi");
            return false;
        }
        binding.etAlamat.setError(null);
        if (noHp.isEmpty()) {
            binding.etNoHp.setError("Wajib diisi");
            return false;
        }
        binding.etNoHp.setError(null);

        if (dipilih.equals("pasien")) {
            if (pendidikan.isEmpty()) {
                binding.etPendidikan.setError("Wajib diisi");
                return false;
            }
            binding.etPendidikan.setError(null);

            if (pekerjaan.isEmpty()) {
                binding.etPekerjaan.setError("Wajib diisi");
                return false;
            }
            binding.etPekerjaan.setError(null);

            if (mulaiPengobatan.isEmpty()) {
                binding.etMulaiPengobatan.setError("Wajib diisi");
                return false;
            }
            binding.etMulaiPengobatan.setError(null);
        }

        if (!isRadioKelaminChecked) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void simpan() {
        PasienModel pasienModel = new PasienModel(firebaseUser.getUid(), nama, kelamin, tglLahir, alamat, noHp, email, "", pendidikan, pekerjaan, mulaiPengobatan);
        NakesModel nakesModel = new NakesModel(firebaseUser.getUid(), nama, kelamin, tglLahir, alamat, email, noHp);

        if (dipilih.equals("pasien")) {
            Log.d(LOG, "pasien dipilih");
            dbRefPasien.push().setValue(pasienModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(LOG, "Registrasi pasien berhasil");
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    binding.btnSimpan.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    registrasiPasienUser();
                }
            });

        } else if (dipilih.equals("nakes")) {
            Log.d(LOG, "nakes dipilih");
            dbRefNakes.push().setValue(nakesModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(LOG, "Registrasi berhasil");
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    binding.btnSimpan.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    registrasiNakesUser();
                }
            });
        }
    }

    private void registrasiNakesUser() {
        UserModel userModel = new UserModel(firebaseUser.getUid(), firebaseUser.getEmail(), "nakes");
        dbRefUser.push().setValue(userModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(LOG, "Registrasi user nakes berhasil");
                keMain();
            }
        });
    }

    private void registrasiPasienUser() {
        UserModel userModel = new UserModel(firebaseUser.getUid(), firebaseUser.getEmail(), "pasien");
        dbRefUser.push().setValue(userModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(LOG, "Registrasi user pasien berhasil");
                keRequest(binding.getRoot());
            }
        });
    }

    private void keMain() {
        Intent intentM = new Intent(RegisterActivity.this, MainActivity.class);
        intentM.putExtra("level", "nakes");
        startActivity(intentM);
        finish();
    }

    public void keRequest(View view) {
        Intent intentR = new Intent(RegisterActivity.this, RequestActivity.class);
        Log.d(LOG, "Intent to ReequestActivity");
        intentR.putExtra("menu", "registrasi");
        startActivity(intentR);
        finish();
    }
}