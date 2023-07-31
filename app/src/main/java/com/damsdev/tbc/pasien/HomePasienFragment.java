package com.damsdev.tbc.pasien;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.AktivitasAdapter;
import com.damsdev.tbc.AktivitasDetailAdapter;
import com.damsdev.tbc.PengingatActivity;
import com.damsdev.tbc.R;
import com.damsdev.tbc.RequestActivity;
import com.damsdev.tbc.databinding.DialogListProgramAktivitasBinding;
import com.damsdev.tbc.databinding.DialogSimpanDetailAktivitasBinding;
import com.damsdev.tbc.databinding.FragmentHomePasienBinding;
import com.damsdev.tbc.databinding.ItemDetailAktivitasBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.model.AktivitasModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.model.RequestModel;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HomePasienFragment extends Fragment implements AktivitasAdapter.IAktivitasClick, AktivitasDetailAdapter.IAktivitasDetailClick {
    FragmentHomePasienBinding binding;
    String LOG = "LOG_HOME_FRAGMENT";
    Dialog dialogProgram;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbAktivitas, dbDetailAktivitas, dbPasien;
    private List<AktivitasModel> aktivitasModels;
    private List<AktivitasDetailModel> aktivitasDetailModels;
    private List<String> keyAktivitasList;
    private List<String> keyDetailAktivitasList;
    private String keyAktivitasSelected;
    private AktivitasAdapter aktivitasAdapter;
    private SharedPrefManager sharedPrefManager;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String today;

    public HomePasienFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomePasienBinding.inflate(inflater, container, false);
        binding.cardProgram.setOnClickListener(view -> {
            showProgramDialog();
        });
        binding.addFab.setOnClickListener(view -> {
            showTambahDialog();
        });
        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.tvNama.setText(user != null ? user.getDisplayName() : "-");
//        binding.tvNomor.setText(user != null ? user.getEmail() : "-");
        Glide.with(requireActivity())
                .asBitmap()
                .load(user != null ? user.getPhotoUrl() : "")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user)
                .dontAnimate()
//                .apply(requestOptions)
                .into(binding.ivProfil);

        dialogProgram = new Dialog(requireActivity());

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        today = dateFormat.format(calendar.getTime());

        sharedPrefManager = new SharedPrefManager(requireActivity());
        keyAktivitasList = new ArrayList<>();
        aktivitasModels = new ArrayList<>();
        aktivitasDetailModels = new ArrayList<>();
        keyDetailAktivitasList = new ArrayList<>();
        aktivitasAdapter = new AktivitasAdapter(aktivitasModels, requireActivity(), this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        binding.rvDetailAktivitas.setLayoutManager(mLayoutManager);
        binding.rvDetailAktivitas.setItemAnimator(new DefaultItemAnimator());


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbAktivitas = firebaseDatabase.getReference(DbReference.AKTIVITAS);
        dbDetailAktivitas = firebaseDatabase.getReference(DbReference.DETAIL_AKTIVITAS);
        dbPasien = firebaseDatabase.getReference(DbReference.PASIEN);

        if (cekSudahInput()) {
            binding.addFab.setVisibility(View.GONE);
        } else {
            binding.addFab.setVisibility(View.VISIBLE);
        }

        getAktivitas(dbAktivitas.orderByChild("idPasien").equalTo(firebaseUser.getUid()));
        getPasein(dbPasien.orderByChild("idPasien").equalTo(firebaseUser.getUid()));

        return binding.getRoot();
    }

    private void getPasein(Query query) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "Queryyy value: " + snapshot.getValue());
                } else {
                  for (DataSnapshot data: snapshot.getChildren()){
                      PasienModel model = data.getValue(PasienModel.class);
                      binding.tvNama.setText(model != null ? model.getNama() : "-");
                  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG, "Query: " + error.getDetails());
            }
        };

        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDashBoard();
        getDetailAktivitas();
    }

    private void getDetailAktivitas() {
        FirebaseRecyclerOptions<AktivitasDetailModel> options = new FirebaseRecyclerOptions.Builder<AktivitasDetailModel>()
                .setQuery(dbDetailAktivitas.orderByChild("idAktivitas").equalTo(sharedPrefManager.getSpIdAktivitas()), AktivitasDetailModel.class).build(); /** Firebase database eke thiyena data Contact class ekata gannawa*/

        FirebaseRecyclerAdapter<AktivitasDetailModel, Holder> adapters = new FirebaseRecyclerAdapter<AktivitasDetailModel, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull AktivitasDetailModel model) {
                holder.detailAktivitasBinding.tvTgl.setText(model.getTgl());
                holder.itemView.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setMessage("Aktivitas " + model.getTgl() + "")
                            .setPositiveButton("Hapus aktivitas", (dialog, id) -> {
                                hapusAktivitas(getRef(position).getKey());
                                dialog.cancel();
                                Toast.makeText(requireActivity(), "Menghapus...", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Tutup", (dialog, id) -> dialog.cancel());
                    builder.create();
                    builder.show();
                });
            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new Holder(ItemDetailAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };

        binding.rvDetailAktivitas.setAdapter(adapters);
        adapters.startListening();

    }

    private boolean cekSudahInput() {
        return sharedPrefManager.getSpTerakhirPost().equals(today);
    }

    private void getAktivitas(Query dbref) {
        aktivitasModels.clear();
        keyAktivitasList.clear();
        aktivitasAdapter.notifyDataSetChanged();
        // List nakes
        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG, "Child Added, " + snapshot.getValue());
                AktivitasModel model = snapshot.getValue(AktivitasModel.class);
                aktivitasModels.add(model);
                aktivitasAdapter.notifyDataSetChanged();
                keyAktivitasList.add(snapshot.getKey());
                // TODO: 25/07/23 tampilkan tvProgram, jika takada jangan
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                aktivitasAdapter.notifyDataSetChanged();
                Log.d(LOG, "onChildChanged, ");
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                aktivitasAdapter.notifyDataSetChanged();
                Log.d(LOG, "onChildRemoved, ");
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                aktivitasAdapter.notifyDataSetChanged();
                Log.d(LOG, "onChildMoved, ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG, "onCancelled, " + error.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showProgramDialog() {
        DialogListProgramAktivitasBinding dialogBinding = DialogListProgramAktivitasBinding.inflate(getLayoutInflater());
        dialogProgram.setContentView(dialogBinding.getRoot());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        dialogBinding.rvProgramAktivitas.setLayoutManager(mLayoutManager);
        dialogBinding.rvProgramAktivitas.setItemAnimator(new DefaultItemAnimator());
        dialogBinding.rvProgramAktivitas.setAdapter(aktivitasAdapter);
        aktivitasAdapter.notifyDataSetChanged();

        Objects.requireNonNull(dialogProgram.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogProgram.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogProgram.show();
    }

    private void showTambahDialog() {
        Dialog dialog = new Dialog(requireActivity());
        DialogSimpanDetailAktivitasBinding dialogBinding = DialogSimpanDetailAktivitasBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.tvTanggal.setText(today);
        dialogBinding.btnSimpan.setOnClickListener(view -> {
            postAktivitas();
            dialog.dismiss();
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void postAktivitas() {
        if (!cekSudahInput()) {
            Toast.makeText(requireActivity(), "Menyimpan...", Toast.LENGTH_SHORT).show();
            AktivitasDetailModel model = new AktivitasDetailModel(sharedPrefManager.getSpIdAktivitas(), "", today, "y");
            dbDetailAktivitas.push().setValue(model).addOnSuccessListener(requireActivity(), new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    sharedPrefManager.saveString(SharedPrefManager.SP_TERAKHIR_POST, today);
                    binding.addFab.setVisibility(View.GONE);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemAktivitasClick(int position) {
        keyAktivitasSelected = keyAktivitasList.get(position);
        sharedPrefManager.saveString(SharedPrefManager.SP_ID_AKTIVITAS, keyAktivitasSelected);
        dialogProgram.dismiss();

        AktivitasModel model = aktivitasModels.get(position);
        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_MULAI, model.getTglMulai());
        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_SELESAI, model.getTglSelesai());

        setDashBoard();

        Log.d(LOG, keyAktivitasList.get(position));
        getDetailAktivitas();
    }

    @SuppressLint("SetTextI18n")
    private void setDashBoard() {
        if (!sharedPrefManager.getSpIdAktivitas().matches("")) {
            binding.tvProgram.setText(sharedPrefManager.getSpTglMulai() + " s/d " + sharedPrefManager.getSpTglSelesai());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            Date firstDate = null;
            Date secondDate = null;
            Date hariIni = new Date();
            String strHariIni = sdf.format(hariIni);
            try {
                firstDate = sdf.parse(sharedPrefManager.getSpTglMulai());
                secondDate = sdf.parse(sharedPrefManager.getSpTglSelesai());
                hariIni = sdf.parse(strHariIni);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            binding.tvLabelProgram.setText("Program pengobatan ");
            binding.tvKepatuhan.setText(diff + " hari");

            long diffInMillies2 = Math.abs(secondDate.getTime() - hariIni.getTime());
            long diff2 = TimeUnit.DAYS.convert(diffInMillies2, TimeUnit.MILLISECONDS);

            binding.tvSisa.setText(diff2 + " hari");
        } else {
            binding.addFab.setVisibility(View.GONE);
        }
//        String strKepatuhan = String.valueOf((diff2/diff) * 100);
//        binding.tvKepatuhan.setText(strKepatuhan+"%");
    }

    @Override
    public void onItemDetailAktivitasClick(int position) {
        AktivitasDetailModel model = aktivitasDetailModels.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Hapus aktivitas " + model.getTgl() + " ?")
                .setPositiveButton("Hapus aktivitas", (dialog, id) -> {
                    hapusAktivitas(keyDetailAktivitasList.get(position));
                    dialog.cancel();
                    Toast.makeText(requireActivity(), "Menghapus...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Tutup", (dialog, id) -> dialog.cancel());
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void hapusAktivitas(String key) {
        dbDetailAktivitas.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.addFab.setVisibility(View.VISIBLE);
                sharedPrefManager.saveString(SharedPrefManager.SP_TERAKHIR_POST, "");
            }
        });
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemDetailAktivitasBinding detailAktivitasBinding;

        public Holder(ItemDetailAktivitasBinding detailAktivitasBinding) {
            super(detailAktivitasBinding.getRoot());
            this.detailAktivitasBinding = detailAktivitasBinding;
        }
    }
}