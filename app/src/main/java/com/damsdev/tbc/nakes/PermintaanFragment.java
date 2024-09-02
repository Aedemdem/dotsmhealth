package com.damsdev.tbc.nakes;

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

import com.damsdev.tbc.databinding.DialogConfirmBinding;
import com.damsdev.tbc.databinding.FragmentPermintaanBinding;
import com.damsdev.tbc.databinding.ItemPasienBinding;
import com.damsdev.tbc.model.RequestModel;
import com.damsdev.tbc.util.DbReference;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PermintaanFragment extends Fragment {
    FragmentPermintaanBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefRequest, dbRefPasien;

    private String LOG = "LOG_PERMINTAAN_FRAGMENT";
    boolean isDialogOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPermintaanBinding.inflate(inflater, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPasien.setLayoutManager(mLayoutManager);
        binding.rvPasien.setItemAnimator(new DefaultItemAnimator());
//        binding.rvPasien.setAdapter(adapter);

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

        // List nakes
        FirebaseRecyclerOptions<RequestModel> options = new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(dbref, RequestModel.class).build();

        FirebaseRecyclerAdapter<RequestModel, PermintaanFragment.Holder> adapters = new FirebaseRecyclerAdapter<RequestModel, PermintaanFragment.Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PermintaanFragment.Holder holder, int position, @NonNull RequestModel model) {
                holder.itemBinding.tvNama.setText(model.getNmPasien());
                holder.itemBinding.tvAlamat.setText(model.getAlamatPasien());

                holder.itemView.setOnClickListener(view -> {
                    sendConirmDialog(model.getIdPasien());
                });
            }

            @NonNull
            @Override
            public PermintaanFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PermintaanFragment.Holder(ItemPasienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
            }
        };

        binding.rvPasien.setAdapter(adapters);
        adapters.startListening();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemPasienBinding itemBinding;

        public Holder(ItemPasienBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

    String keyPasien = "";
    private void getPasien(Query dbref) {
        // dapatkan data sekali
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dst : snapshot.getChildren()) {
                    Log.d(LOG, "DST PASIEN : " + dst.getKey());
                    keyPasien = dst.getKey();
                }
                if (!isDialogOpen) {
                    showDialog(keyPasien);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(String keyPasien) {
        Dialog myDialog = new Dialog(getActivity());
        DialogConfirmBinding dialogConfirmBinding = DialogConfirmBinding.inflate(getLayoutInflater());
        myDialog.setContentView(dialogConfirmBinding.getRoot());

        dialogConfirmBinding.btnConfirm.setOnClickListener(view -> {
            myDialog.dismiss();
            Log.d(LOG, "Detail : ");
            sendConfirm(keyPasien);
            myDialog.cancel();
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();
    }

    private void sendConirmDialog(String idPasien) {
        getPasien(dbRefPasien.orderByChild("idPasien").equalTo(idPasien));
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