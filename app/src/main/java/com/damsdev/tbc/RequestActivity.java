package com.damsdev.tbc;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damsdev.tbc.databinding.ActivityReequestBinding;
import com.damsdev.tbc.databinding.DialogRequestBinding;
import com.damsdev.tbc.databinding.ItemNakesBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.model.RequestModel;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity{
    ActivityReequestBinding binding;
    String LOG = "LOG_REQUEST_ACTIVITY";
    String nmPasien, alamatPasien;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefRequest, dbRefNakes, dbRefPasien;
    String nmNakes;

    SharedPrefManager sharedPrefManager;

    // move to on success
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPrefManager = new SharedPrefManager(this);

        if (getIntent().getStringExtra("menu") != null) {
            if (getIntent().getStringExtra("menu").equals("registrasi")) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            } else {
                Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
            }
        } else {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle("Permintaan pendampingan");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rvNakes.setLayoutManager(mLayoutManager);
        binding.rvNakes.setItemAnimator(new DefaultItemAnimator());

        binding.constraintMenungguKonfirmasi.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRefRequest = firebaseDatabase.getReference(DbReference.REQUEST);
        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);

        getDetailPasien(dbRefPasien.orderByChild("email").equalTo(firebaseUser.getEmail()));

    }

    private void cekStatusPendampingan(Query query) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "cekStatusPendampingan: == null" + snapshot.getValue());
                } else {
                    Log.d(LOG, "cekStatusPendampingan: != null" + snapshot.getValue());
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(RequestActivity.this, "Gagal " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(LOG, "Query: " + error.getDetails());
            }
        };

        query.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
//        if (getIntent().getStringExtra("menu") != null) {
//            if (getIntent().getStringExtra("menu").equals("registrasi")) {
//                finish();
//            } else {
//                onBackPressed();
//            }
//        } else {
//
//        }
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getDetailPasien(Query query) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "getDetailPasien: == null " + snapshot.getValue());
//                    Toast.makeText(RequestActivity.this, "Data pasien ditemukan", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(LOG, "getDetailPasien: != null " + snapshot.getValue());
//                    Log.d(LOG, "Pasien value: " + snapshot.getValue());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PasienModel model = dataSnapshot.getValue(PasienModel.class);
                        nmPasien = model != null ? model.getNama() : "-";
                        alamatPasien = model != null ? model.getAlamat() : "-";
                        Log.d(LOG, "Pasien value: nmPasien" + nmPasien);
                        Log.d(LOG, "Pasien value: alamatPasien" + alamatPasien);
                        sharedPrefManager.saveString(SharedPrefManager.SP_ID_NAKES,model != null ? model.getIdNakes():"");
                    }

//                    Log.d(LOG, "Pasien value: nmPasien" + snapshot.child("pasien").child("nama").getValue());
                    getRequest(dbRefRequest.orderByChild("idPasien").equalTo(firebaseUser.getUid()));
                    // TODO: 08/11/23 cek status pendampingan
                    cekStatusPendampingan(dbRefNakes.orderByChild("idNakes").equalTo(sharedPrefManager.getSpIdNakes() != null ? sharedPrefManager.getSpIdNakes() : ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getRequest(Query dbref) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
//                    Toast.makeText(RequestActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    Log.d(LOG, "getRequest: == null " + snapshot.getValue());
                    getNakes(dbRefNakes);
                    binding.rvNakes.setVisibility(View.VISIBLE);
                    binding.constraintMenungguKonfirmasi.setVisibility(View.GONE);

                } else {
//                    Toast.makeText(RequestActivity.this, "Ditemukan", Toast.LENGTH_SHORT).show();
                    Log.d(LOG, "getRequest: != null " + snapshot.getValue());

//                    RequestModel requestModel = snapshot.child("request").getValue(RequestModel.class);
//                    List<RequestModel> rm = new ArrayList<>();
//                    rm.add(requestModel);
                    Log.d(LOG, "AYDI ");
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RequestModel model = dataSnapshot.getValue(RequestModel.class);
                        if (model != null) {
                            binding.tvNama.setText(model.getNmNakes());
                            binding.btnBatalkan.setOnClickListener(view -> {
                                batalkanRequest(dataSnapshot.getKey());
                            });
                        }
                    }

                    binding.constraintMenungguKonfirmasi.setVisibility(View.VISIBLE);
                    binding.rvNakes.setVisibility(View.GONE);
//                    binding.tvNama.setText(requestModel.getIdNakes());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestActivity.this, "Gagal Req" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(LOG, "Query: " + error.getDetails());
            }
        };

        dbref.addValueEventListener(valueEventListener);


    }

    private void batalkanRequest(String key) {
        dbRefRequest.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getNakes(dbRefNakes);
                Toast.makeText(RequestActivity.this, "Permintaan dibatalkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNakes(DatabaseReference dbref) {
        Log.d(LOG, "getNakes");
        FirebaseRecyclerOptions<NakesModel> options = new FirebaseRecyclerOptions.Builder<NakesModel>()
                .setQuery(dbref.orderByChild("nama"), NakesModel.class).build();

        FirebaseRecyclerAdapter<NakesModel, RequestActivity.Holder> adapters = new FirebaseRecyclerAdapter<NakesModel, RequestActivity.Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestActivity.Holder holder, int position, @NonNull NakesModel model) {
                holder.itemBinding.tvNama.setText(model.getNama());
                holder.itemBinding.tvAlamat.setText(model.getAlamat());

                holder.itemBinding.btnTambahkan.setOnClickListener(view -> {
                    showReqDialog(model.getIdNakes());
                    nmNakes = model.getNama();
                });
            }

            @NonNull
            @Override
            public RequestActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RequestActivity.Holder(ItemNakesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
            }
        };

        binding.rvNakes.setAdapter(adapters);
        adapters.startListening();
    }


    private void showReqDialog(String idNakes) {
        Dialog dialog = new Dialog(this);
        DialogRequestBinding dialogRequestBinding = DialogRequestBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogRequestBinding.getRoot());

//        NakesModel nakesModel = models.get(position);

        dialogRequestBinding.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(firebaseUser.getUid(), idNakes);
//                Toast.makeText(RequestActivity.this, "Oke - " + nakesModel.getNama() + "Key - ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void sendRequest(String idPasien, String idNakes) {
        RequestModel requestModel = new RequestModel(idPasien, idNakes, nmPasien, alamatPasien, nmNakes);
        dbRefRequest.push().setValue(requestModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Permintaan terkirim", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemNakesBinding itemBinding;

        public Holder(ItemNakesBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}