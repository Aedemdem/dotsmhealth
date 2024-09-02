package com.damsdev.tbc.nakes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.R;
import com.damsdev.tbc.databinding.FragmentHomeBinding;
import com.damsdev.tbc.databinding.ItemPasienBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.model.PasienModel;
import com.damsdev.tbc.util.DbReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    String LOG = "LOG_HOME_FRAGMENT";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefPasien, dbRefNakes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        binding.tvNama.setText(user != null ? user.getDisplayName() : "-");
//        binding.tvNomor.setText(user != null ? user.getEmail() : "-");

        Glide.with(requireActivity())
                .asBitmap()
                .load(user != null ? user.getPhotoUrl() : "")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user)
                .dontAnimate()
                .into(binding.ivProfil);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPasien.setLayoutManager(mLayoutManager);
        binding.rvPasien.setItemAnimator(new DefaultItemAnimator());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);
        dbRefPasien = firebaseDatabase.getReference(DbReference.PASIEN);

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        getPasien();
        getNakes(dbRefNakes.orderByChild("idNakes").equalTo(firebaseUser.getUid()));
    }

    private void getPasien() {
        FirebaseRecyclerOptions<PasienModel> options = new FirebaseRecyclerOptions.Builder<PasienModel>()
                .setQuery(dbRefPasien.orderByChild("idNakes").equalTo(firebaseUser.getUid()), PasienModel.class).build();

        FirebaseRecyclerAdapter<PasienModel, HomeFragment.Holder> adapters = new FirebaseRecyclerAdapter<PasienModel, HomeFragment.Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.Holder holder, int position, @NonNull PasienModel model) {
                holder.itemPasienBinding.tvNama.setText(model.getNama());
                holder.itemPasienBinding.tvAlamat.setText(model.getAlamat());

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(requireActivity(), AktivitasPasienActivity.class);
                    intent.putExtra("nmPasien", model.getNama());
                    intent.putExtra("idPasien", model.getIdPasien());
                    startActivity(intent);
                });

                Glide.with(requireActivity())
                        .asBitmap()
                        .load("")
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.user)
                        .dontAnimate()
                        .into(holder.itemPasienBinding.imageView3);
            }

            @NonNull
            @Override
            public HomeFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HomeFragment.Holder(ItemPasienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
            }
        };

        binding.rvPasien.setAdapter(adapters);
        adapters.startListening();
    }

    private void getNakes(Query query) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "Queryyy value: " + snapshot.getValue());
                } else {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        NakesModel model = data.getValue(NakesModel.class);
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
    public static class Holder extends RecyclerView.ViewHolder {
        ItemPasienBinding itemPasienBinding;

        public Holder(ItemPasienBinding itemPasienBinding) {
            super(itemPasienBinding.getRoot());
            this.itemPasienBinding = itemPasienBinding;
        }
    }

}