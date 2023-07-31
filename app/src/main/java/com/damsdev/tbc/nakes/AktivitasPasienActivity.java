package com.damsdev.tbc.nakes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.AktivitasAdapter;
import com.damsdev.tbc.R;
import com.damsdev.tbc.TambahProgramActivity;
import com.damsdev.tbc.databinding.ActivityAktivitasPasienBinding;
import com.damsdev.tbc.databinding.DialogListProgramAktivitas2Binding;
import com.damsdev.tbc.databinding.ItemAktivitasBinding;
import com.damsdev.tbc.databinding.ItemDetailAktivitasBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.model.AktivitasModel;
import com.damsdev.tbc.util.DbReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AktivitasPasienActivity extends AppCompatActivity  {
    private final String LOG = "LOG_AKTIVITAS_PASIEN_ACTIVITY";
    String idPasien, nmPasien;
    private ActivityAktivitasPasienBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefPasien, dbAktivitas,dbDetailAktivitas;
    private List<AktivitasModel> aktivitasModels;
    private List<String> keyAktivitasList;
    private AktivitasAdapter aktivitasAdapter;
    Dialog dialogProgram;
    DialogListProgramAktivitas2Binding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAktivitasPasienBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Aktivitas pasien");
//        getSupportActionBar().setSubtitle("Dokumen Realisasi");

        nmPasien = getIntent().getStringExtra("nmPasien");
        idPasien = getIntent().getStringExtra("idPasien");

        binding.tvNmPasien.setText(nmPasien);

        Glide.with(this)
                .asBitmap()
                .load("")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user)
                .dontAnimate()
                .into(binding.ivPasien);

        binding.cardProgram.setOnClickListener(view -> {
            showProgramDialog();
        });

        dialogProgram = new Dialog(this);
        dialogBinding = DialogListProgramAktivitas2Binding.inflate(getLayoutInflater());
        dialogProgram.setContentView(dialogBinding.getRoot());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);

        dialogBinding.rvProgramAktivitas.setLayoutManager(mLayoutManager);
        dialogBinding.rvProgramAktivitas.setItemAnimator(new DefaultItemAnimator());
        dialogProgram.show();
        dialogProgram.dismiss();

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        binding.rvDetailAktivitas.setLayoutManager(mLayoutManager2);
        binding.rvDetailAktivitas.setItemAnimator(new DefaultItemAnimator());

        aktivitasModels = new ArrayList<>();
        keyAktivitasList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbAktivitas = firebaseDatabase.getReference(DbReference.AKTIVITAS);
        dbDetailAktivitas = firebaseDatabase.getReference(DbReference.DETAIL_AKTIVITAS);

//        getAktivitas(dbAktivitas.orderByChild("idPasien").equalTo(idPasien));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getAktivitas();
    }

    private void getAktivitas() {
        FirebaseRecyclerOptions<AktivitasModel> options = new FirebaseRecyclerOptions.Builder<AktivitasModel>()
                .setQuery(dbAktivitas.orderByChild("idPasien").equalTo(idPasien), AktivitasModel.class).build(); /** Firebase database eke thiyena data Contact class ekata gannawa*/

        FirebaseRecyclerAdapter<AktivitasModel, AktivitasPasienActivity.HolderAktivitas> adapters = new FirebaseRecyclerAdapter<AktivitasModel, AktivitasPasienActivity.HolderAktivitas>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull AktivitasPasienActivity.HolderAktivitas holder, int position, @NonNull AktivitasModel model) {
                holder.aktivitasBinding.tvTgl.setText(model.getTglMulai()+" s/d "+model.getTglSelesai());
                binding.tvProgram.setText(model.getTglMulai() + " s/d " + model.getTglSelesai());
                holder.itemView.setOnClickListener(view -> {
                    getDetailAktivitas(getRef(position).getKey());
                    setDashBoard(model.getTglMulai(), model.getTglSelesai());
                    dialogProgram.dismiss();
                });
            }

            @NonNull
            @Override
            public AktivitasPasienActivity.HolderAktivitas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new AktivitasPasienActivity.HolderAktivitas(ItemAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };

        dialogBinding.rvProgramAktivitas.setAdapter(adapters);
        adapters.startListening();
        showProgramDialog();

    }

    public static class HolderAktivitas extends RecyclerView.ViewHolder {
        ItemAktivitasBinding aktivitasBinding;

        public HolderAktivitas(ItemAktivitasBinding aktivitasBinding) {
            super(aktivitasBinding.getRoot());
            this.aktivitasBinding = aktivitasBinding;
        }
    }

    private void showProgramDialog() {
        dialogBinding.chipTambahAktivitas.setOnClickListener(view -> {
            Intent intent = new Intent(AktivitasPasienActivity.this, TambahProgramActivity.class);
            intent.putExtra("idPasien", idPasien);
            startActivity(intent);
        });

        Objects.requireNonNull(dialogProgram.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogProgram.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogProgram.show();
    }

    private void setDashBoard(String tglMulai, String tglSelesai) {
        binding.tvProgram.setText(tglMulai+" s/d "+tglSelesai);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        Date firstDate = null;
        Date secondDate = null;
        Date hariIni = new Date();
        String strHariIni = sdf.format(hariIni);
        try {
            firstDate = sdf.parse(tglMulai);
            secondDate = sdf.parse(tglSelesai);
            hariIni = sdf.parse(strHariIni);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        binding.tvLabelProgram.setText("Program pengobatan ");
        binding.tvKepatuhan.setText(  diff + " hari");

        long diffInMillies2 = Math.abs(secondDate.getTime() - hariIni.getTime());
        long diff2 = TimeUnit.DAYS.convert(diffInMillies2, TimeUnit.MILLISECONDS);

        binding.tvSisa.setText(diff2 + " hari");
//        String strKepatuhan = String.valueOf((diff2/diff) * 100);
//        binding.tvKepatuhan.setText(strKepatuhan+"%");
    }

    private void getDetailAktivitas(String key) {
        Log.d(LOG,"KEY = "+key);
        FirebaseRecyclerOptions<AktivitasDetailModel> options = new FirebaseRecyclerOptions.Builder<AktivitasDetailModel>()
                .setQuery(dbDetailAktivitas.orderByChild("idAktivitas").equalTo(key), AktivitasDetailModel.class).build();
        FirebaseRecyclerAdapter<AktivitasDetailModel, HolderDetailAktivitas> adapters = new FirebaseRecyclerAdapter<AktivitasDetailModel, HolderDetailAktivitas>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HolderDetailAktivitas holder, int position, @NonNull AktivitasDetailModel model) {
                holder.detailAktivitasBinding.tvTgl.setText(model.getTgl());
                holder.itemView.setOnClickListener(view -> {

                });
                Log.d(LOG,"TGL = "+model.getTgl());
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Log.d(LOG,"DatabaseError = "+error.getMessage());
            }

            @NonNull
            @Override
            public HolderDetailAktivitas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HolderDetailAktivitas(ItemDetailAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };

        binding.rvDetailAktivitas.setAdapter(adapters);
        adapters.startListening();
        Log.d(LOG,"Listen");

    }

    public static class HolderDetailAktivitas extends RecyclerView.ViewHolder {
        ItemDetailAktivitasBinding detailAktivitasBinding;

        public HolderDetailAktivitas(ItemDetailAktivitasBinding detailAktivitasBinding) {
            super(detailAktivitasBinding.getRoot());
            this.detailAktivitasBinding = detailAktivitasBinding;
        }
    }
}