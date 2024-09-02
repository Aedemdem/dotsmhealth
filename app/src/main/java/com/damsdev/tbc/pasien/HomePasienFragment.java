package com.damsdev.tbc.pasien;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.R;
import com.damsdev.tbc.databinding.DialogListProgramAktivitasBinding;
import com.damsdev.tbc.databinding.DialogSimpanDetailAktivitasBinding;
import com.damsdev.tbc.databinding.FragmentHomePasienBinding;
import com.damsdev.tbc.databinding.ItemAktivitasBinding;
import com.damsdev.tbc.databinding.ItemDetailAktivitasBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.model.AktivitasModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//implements AktivitasDetailAdapter.IAktivitasDetailClick
public class HomePasienFragment extends Fragment {
    FragmentHomePasienBinding binding;
    String LOG = "LOG_HOME_FRAGMENT";
    Dialog dialogProgram;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbAktivitas, dbDetailAktivitas, dbPasien, dbNakes;
    //    private List<AktivitasModel> aktivitasModels;
//    private List<AktivitasDetailModel> aktivitasDetailModels;
//    private List<String> keyAktivitasList;
//    private List<String> keyDetailAktivitasList;
    private String keyAktivitasSelected;
    //    private AktivitasAdapter aktivitasAdapter;
    private SharedPrefManager sharedPrefManager;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String today;
    DialogListProgramAktivitasBinding dialogBinding;

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
        dialogBinding = DialogListProgramAktivitasBinding.inflate(getLayoutInflater());
        binding.fab.setOnClickListener(view -> {
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
//        keyAktivitasList = new ArrayList<>();
//        aktivitasModels = new ArrayList<>();
//        aktivitasDetailModels = new ArrayList<>();
//        keyDetailAktivitasList = new ArrayList<>();
//        aktivitasAdapter = new AktivitasAdapter(aktivitasModels, requireActivity(), this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        binding.rvDetailAktivitas.setLayoutManager(mLayoutManager);
        binding.rvDetailAktivitas.setItemAnimator(new DefaultItemAnimator());


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbAktivitas = firebaseDatabase.getReference(DbReference.AKTIVITAS);
        dbDetailAktivitas = firebaseDatabase.getReference(DbReference.DETAIL_AKTIVITAS);
        dbPasien = firebaseDatabase.getReference(DbReference.PASIEN);
        dbNakes = firebaseDatabase.getReference(DbReference.NAKES);
        binding.addFab.setVisibility(View.VISIBLE);

        getNakes();
        getAktivitas(dbAktivitas.orderByChild("idPasien").equalTo(firebaseUser.getUid()));
        getPasein(dbPasien.orderByChild("idPasien").equalTo(firebaseUser.getUid()));

        return binding.getRoot();
    }

    private void getNakes() {
        dbNakes.orderByChild("idNakes").equalTo(sharedPrefManager.getSpIdNakes()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data nakes", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    private void getPasein(Query query) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "Queryyy value: " + snapshot.getValue());
                } else {
                    for (DataSnapshot data : snapshot.getChildren()) {
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
                .setQuery(dbDetailAktivitas.orderByChild("idAktivitas").equalTo(sharedPrefManager.getSpIdAktivitas()), AktivitasDetailModel.class).build();

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

    public static class Holder extends RecyclerView.ViewHolder {
        ItemDetailAktivitasBinding detailAktivitasBinding;

        public Holder(ItemDetailAktivitasBinding detailAktivitasBinding) {
            super(detailAktivitasBinding.getRoot());
            this.detailAktivitasBinding = detailAktivitasBinding;
        }
    }

    private void cekSudahInput() {
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
                    } else {
                        Toast.makeText(requireActivity(), "Anda sudah mengisi pada hari ini", Toast.LENGTH_LONG).show();
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

    private void getAktivitas(Query dbref) {
//        aktivitasModels.clear();
//        keyAktivitasList.clear();
//        aktivitasAdapter.notifyDataSetChanged();
        // List nakes
        FirebaseRecyclerOptions<AktivitasModel> options = new FirebaseRecyclerOptions.Builder<AktivitasModel>()
                .setQuery(dbref, AktivitasModel.class).build();
        FirebaseRecyclerAdapter<AktivitasModel, HomePasienFragment.HolderAktivitas> adapters = new FirebaseRecyclerAdapter<AktivitasModel, HomePasienFragment.HolderAktivitas>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull HomePasienFragment.HolderAktivitas holder, int position, @NonNull AktivitasModel model) {
                holder.itemAktivitasBinding.tvTgl.setText(model.getTglMulai() + " s/d " + model.getTglSelesai());
                holder.itemView.setOnClickListener(view -> {
                    keyAktivitasSelected = getRef(position).getKey();
                    sharedPrefManager.saveString(SharedPrefManager.SP_ID_AKTIVITAS, keyAktivitasSelected);
                    dialogProgram.dismiss();

                    sharedPrefManager.saveString(SharedPrefManager.SP_TGL_MULAI, model.getTglMulai());
                    sharedPrefManager.saveString(SharedPrefManager.SP_TGL_SELESAI, model.getTglSelesai());

                    setDashBoard();
                    getDetailAktivitas();
                });

//                keyAktivitasList.add(getRef(position).getKey());
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Log.d(LOG, "DatabaseError = " + error.getMessage());
            }

            @NonNull
            @Override
            public HomePasienFragment.HolderAktivitas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HomePasienFragment.HolderAktivitas(ItemAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };

        dialogBinding.rvProgramAktivitas.setAdapter(adapters);
        adapters.startListening();

    }

    public static class HolderAktivitas extends RecyclerView.ViewHolder {
        ItemAktivitasBinding itemAktivitasBinding;

        public HolderAktivitas(ItemAktivitasBinding itemAktivitasBinding) {
            super(itemAktivitasBinding.getRoot());
            this.itemAktivitasBinding = itemAktivitasBinding;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showProgramDialog() {

        dialogProgram.setContentView(dialogBinding.getRoot());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        dialogBinding.rvProgramAktivitas.setLayoutManager(mLayoutManager);
        dialogBinding.rvProgramAktivitas.setItemAnimator(new DefaultItemAnimator());
//        dialogBinding.rvProgramAktivitas.setAdapter(aktivitasAdapter);
//        aktivitasAdapter.notifyDataSetChanged();

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
            cekSudahInput();
            dialog.dismiss();
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void postAktivitas() {
        Toast.makeText(requireActivity(), "Menyimpan...", Toast.LENGTH_SHORT).show();
        AktivitasDetailModel model = new AktivitasDetailModel(sharedPrefManager.getSpIdAktivitas(), "", today, "y");
        dbDetailAktivitas.push().setValue(model).addOnSuccessListener(requireActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sharedPrefManager.saveString(SharedPrefManager.SP_TERAKHIR_POST, today);
//                binding.addFab.setVisibility(View.GONE);
            }
        });
    }

//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onItemAktivitasClick(int position) {
//        keyAktivitasSelected = keyAktivitasList.get(position);
//        sharedPrefManager.saveString(SharedPrefManager.SP_ID_AKTIVITAS, keyAktivitasSelected);
//        dialogProgram.dismiss();
//
//        AktivitasModel model = aktivitasModels.get(position);
//        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_MULAI, model.getTglMulai());
//        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_SELESAI, model.getTglSelesai());
//
//        setDashBoard();
//
//        Log.d(LOG, keyAktivitasList.get(position));
//        getDetailAktivitas();
//    }

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
            // TODO: 08/11/23 on progress
            binding.addFab.setVisibility(View.VISIBLE);
        } else {
            binding.addFab.setVisibility(View.GONE);
        }
//        String strKepatuhan = String.valueOf((diff2/diff) * 100);
//        binding.tvKepatuhan.setText(strKepatuhan+"%");
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


}