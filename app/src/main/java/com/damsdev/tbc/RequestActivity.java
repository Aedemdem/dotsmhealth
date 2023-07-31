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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damsdev.tbc.databinding.ActivityReequestBinding;
import com.damsdev.tbc.databinding.DialogRequestBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.model.RequestModel;
import com.damsdev.tbc.util.DbReference;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity implements NakesAdapter.INakesClick {
    ActivityReequestBinding binding;
    String LOG = "LOG_REQUEST_ACTIVITY";
    String nmPasien, alamatPasien;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefRequest, dbRefNakes, dbRefPasien;
    private List<NakesModel> models;
    //    private ArrayList<String> keyList;
    private NakesAdapter adapter;

    // move to on success
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        models = new ArrayList<>();
        adapter = new NakesAdapter(models, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rvNakes.setLayoutManager(mLayoutManager);
        binding.rvNakes.setItemAnimator(new DefaultItemAnimator());
        binding.rvNakes.setAdapter(adapter);

        binding.constraintMenungguKonfirmasi.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRefRequest = firebaseDatabase.getReference(DbReference.REQUEST);
        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);

        getDetailPasien(dbRefPasien.orderByChild("email").equalTo(firebaseUser.getEmail()));

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
//                    Toast.makeText(RequestActivity.this, "Data pasien ditemukan", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(LOG, "Pasien value: " + snapshot.getValue());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PasienModel model = dataSnapshot.getValue(PasienModel.class);
                        nmPasien = model != null ? model.getNama() : "-";
                        alamatPasien = model != null ? model.getAlamat() : "-";
                        Log.d(LOG, "Pasien value: nmPasien" + nmPasien);
                        Log.d(LOG, "Pasien value: alamatPasien" + alamatPasien);
                    }

//                    Log.d(LOG, "Pasien value: nmPasien" + snapshot.child("pasien").child("nama").getValue());
                    getRequest(dbRefRequest.orderByChild("idPasien").equalTo(firebaseUser.getUid()));
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
                    Log.d(LOG, "Queryyy value: " + snapshot.getValue());
                    getNakes(dbRefNakes);
                    binding.rvNakes.setVisibility(View.VISIBLE);
                    binding.constraintMenungguKonfirmasi.setVisibility(View.GONE);
                } else {
//                    Toast.makeText(RequestActivity.this, "Ditemukan", Toast.LENGTH_SHORT).show();
                    Log.d(LOG, "Queryyy: " + snapshot.getValue());

//                    RequestModel requestModel = snapshot.child("request").getValue(RequestModel.class);
//                    List<RequestModel> rm = new ArrayList<>();
//                    rm.add(requestModel);
                    Log.d(LOG, "AYDI ");
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RequestModel model = dataSnapshot.getValue(RequestModel.class);
                        if (model != null) {
                            binding.tvNama.setText(model.getNmPasien());
                            binding.tvAlamat.setText(model.getAlamatPasien());
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
        models.clear();
        // List nakes
        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG, "Child Added, " + snapshot.getValue());
                NakesModel nakesModel = snapshot.getValue(NakesModel.class);
                models.add(nakesModel);
                adapter.notifyDataSetChanged();
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemNakesClick(int position) {
        showReqDialog(position);
    }

    private void showReqDialog(int position) {
        Dialog dialog = new Dialog(this);
        DialogRequestBinding dialogRequestBinding = DialogRequestBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogRequestBinding.getRoot());

        NakesModel nakesModel = models.get(position);

        dialogRequestBinding.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(firebaseUser.getUid(), nakesModel.getIdNakes());
//                Toast.makeText(RequestActivity.this, "Oke - " + nakesModel.getNama() + "Key - ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void sendRequest(String idPasien, String idNakes) {
        RequestModel requestModel = new RequestModel(idPasien, idNakes, nmPasien, alamatPasien);
        dbRefRequest.push().setValue(requestModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Permintaan terkirim", Toast.LENGTH_SHORT).show();
            }
        });
    }
}