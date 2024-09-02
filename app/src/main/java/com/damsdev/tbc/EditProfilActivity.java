package com.damsdev.tbc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.databinding.ActivityEditProfilBinding;
import com.damsdev.tbc.databinding.DialogDatePickerBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.util.DbReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfilActivity extends AppCompatActivity {
    static final String LOG = "LOG_REGISTER_ACTIVITY";
    boolean isRadioSebagaiChecked, isRadioKelaminChecked;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefUser, dbRefPasien, dbRefNakes;
    String dipilih = "";
    String kelamin = "";
    ActivityEditProfilBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private String nama, tglLahir, alamat, noHp, email, pendidikan, pekerjaan, mulaiPengobatan = "";
    String key = "";
    PasienModel pasienModel;
    NakesModel nakesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profil");

        dipilih = getIntent().getStringExtra("sebagai");
        key = getIntent().getStringExtra("key");

        assert dipilih != null;
        if (dipilih.equals("nakes")) {
            nakesModel = getIntent().getParcelableExtra("model");

            binding.linear1.setVisibility(View.VISIBLE);
            binding.linear2.setVisibility(View.GONE);

            binding.etNama.setText(nakesModel.getNama());
            binding.etTglLahir.setText(nakesModel.getTglLahir());
            binding.etAlamat.setText(nakesModel.getAlamat());
            kelamin = nakesModel.getKelamin();
            if (kelamin.equals("L")) {
                binding.rbLakiLaki.setChecked(true);
            } else if (kelamin.equals("P")) {
                binding.rbPerempuan.setChecked(true);
            }
            isRadioKelaminChecked = true;
            binding.etNoHp.setText(nakesModel.getNoHp());
        } else if (dipilih.equals("pasien")) {
            pasienModel = getIntent().getParcelableExtra("model");

            binding.linear1.setVisibility(View.VISIBLE);
            binding.linear2.setVisibility(View.VISIBLE);

            binding.etNama.setText(pasienModel.getNama());
            binding.etTglLahir.setText(pasienModel.getTglLahir());
            binding.etAlamat.setText(pasienModel.getAlamat());
            kelamin = pasienModel.getKelamin();
            if (kelamin.equals("L")) {
                binding.rbLakiLaki.setChecked(true);
            } else if (kelamin.equals("P")) {
                binding.rbPerempuan.setChecked(true);
            }
            isRadioKelaminChecked = true;
            binding.etNoHp.setText(pasienModel.getNoHp());
            binding.etPendidikan.setText(pasienModel.getPendidikan());
            binding.etPekerjaan.setText(pasienModel.getPekerjaan());
            binding.etMulaiPengobatan.setText(pasienModel.getMulaiPengobatan());
        }


        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profil");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        dbRefUser = firebaseDatabase.getReference(DbReference.USER);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);
        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);

//        binding.btnSimpan.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

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
                sendConfirm();
            }
        });

        getCurrentUser(firebaseUser);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        dialogDatePickerBinding.datePicker.setMaxDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
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

//        if (!isRadioSebagaiChecked) {
//            Toast.makeText(this, "Pilih salah satu, sebagai pasien atau nakes", Toast.LENGTH_SHORT).show();
//            return false;
//        }

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

    private void sendConfirm() {
        dbRefPasien = dbRefPasien.child(key);
        dbRefNakes = dbRefNakes.child(key);

        Map<String, Object> mapPasien = new HashMap<>();
        mapPasien.put("nama", nama);
        mapPasien.put("kelamin", kelamin);
        mapPasien.put("tglLahir", tglLahir);
        mapPasien.put("alamat", alamat);
        mapPasien.put("noHp", noHp);
        mapPasien.put("email", email);
        mapPasien.put("pendidikan", pendidikan);
        mapPasien.put("pekerjaan", pekerjaan);
        mapPasien.put("mulaiPengobatan", mulaiPengobatan);

        Map<String, Object> mapNakes = new HashMap<>();
        mapNakes.put("nama", nama);
        mapNakes.put("kelamin", kelamin);
        mapNakes.put("tglLahir", tglLahir);
        mapNakes.put("alamat", alamat);
        mapNakes.put("noHp", noHp);
        mapNakes.put("email", email);

        if (dipilih.equals("pasien")) {
            dbRefPasien.updateChildren(mapPasien);
            dbRefPasien.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(LOG, "Updated : " + snapshot.getValue());
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG, "Uodated error");
                    Toast.makeText(EditProfilActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (dipilih.equals("nakes")) {
            dbRefNakes.updateChildren(mapNakes);
            dbRefNakes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(LOG, "Updated : " + snapshot.getValue());
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG, "Uodated error");
                    Toast.makeText(EditProfilActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}