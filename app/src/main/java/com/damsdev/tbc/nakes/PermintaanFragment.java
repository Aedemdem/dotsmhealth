package com.damsdev.tbc.nakes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.damsdev.tbc.RequestAdapter;
import com.damsdev.tbc.databinding.DialogConfirmBinding;
import com.damsdev.tbc.databinding.FragmentPermintaanBinding;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PermintaanFragment extends Fragment implements RequestAdapter.IPasienClick {
    FragmentPermintaanBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefRequest, dbRefPasien;
    private List<RequestModel> requestModels;
    private RequestAdapter adapter;

    private String LOG = "LOG_PERMINTAAN_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPermintaanBinding.inflate(inflater, container, false);

        requestModels =new ArrayList<>();
        adapter = new RequestAdapter(requestModels, requireActivity(), PermintaanFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPasien.setLayoutManager(mLayoutManager);
        binding.rvPasien.setItemAnimator(new DefaultItemAnimator());
        binding.rvPasien.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRefRequest = firebaseDatabase.getReference(DbReference.REQUEST);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);

        Log.d(LOG, "onCreate");

        getRequest(dbRefRequest.orderByChild("idNakes").equalTo(firebaseUser.getUid()));

        return binding.getRoot();
    }
    String keyRequest;
    private void getRequest(Query dbref) {
        Log.d(LOG, "UUID "+firebaseUser.getUid());
        Log.d(LOG, "getRequest");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(LOG, "onDataChange " + snapshot.getValue());

                for (DataSnapshot dst : snapshot.getChildren()) {
                    Log.d(LOG, "DST "+dst.getKey());
                    keyRequest = dst.getKey();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Gagal " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };

        dbref.addValueEventListener(valueEventListener);

        requestModels.clear();
        // List nakes
        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG, "Child Added, " + snapshot.getValue());
                RequestModel requestModel = snapshot.getValue(RequestModel.class);
                requestModels.add(requestModel);
                adapter.notifyDataSetChanged();
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
                Log.d(LOG, "onChildChanged, " );
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
                Log.d(LOG, "onChildRemoved, " );
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
                Log.d(LOG, "onChildMoved, " );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG, "onCancelled, " + error.getMessage());
//                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    String keyPasien = "";
    private void getPasien(Query dbref) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dst : snapshot.getChildren()) {
                    Log.d(LOG, "DST PASIEN : " + dst.getKey());
                    keyPasien = dst.getKey();
                }
                showDialog(keyPasien);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Gagal " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };

        dbref.addValueEventListener(valueEventListener);
    }

    private void showDialog(String keyPasien) {
        Dialog dialog = new Dialog(getActivity());
        DialogConfirmBinding dialogConfirmBinding = DialogConfirmBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogConfirmBinding.getRoot());

        dialogConfirmBinding.btnConfirm.setOnClickListener(view -> {
            Log.d(LOG, "Detail : ");
            sendConfirm(keyPasien);
            dialog.dismiss();
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void onItemPasienClick(int position) {
        sendConirmDialog(position);
    }

    private void sendConirmDialog(int position) {
        RequestModel rm = requestModels.get(position);
        getPasien(dbRefPasien.orderByChild("idPasien").equalTo(rm.getIdPasien()));

    }

    private void sendConfirm(String key) {
        dbRefPasien = dbRefPasien.child(key);
        Map<String, Object> map = new HashMap<>();
        map.put("idNakes", firebaseUser.getUid());

        dbRefPasien.updateChildren(map);
        dbRefPasien.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(LOG, "Updated : " + snapshot.getValue());
//                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                hapusRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG, "Uodated error");
                Toast.makeText(getActivity(), "Gagal dikonfirmasi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusRequest() {
        dbRefRequest.child(keyRequest).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getRequest(dbRefRequest.orderByChild("idNakes").equalTo(firebaseUser.getUid()));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        dbRefRequest.removeEventListener();
    }
}