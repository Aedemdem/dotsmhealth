package com.damsdev.tbc.nakes;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damsdev.tbc.LoginActivity;
import com.damsdev.tbc.R;
import com.damsdev.tbc.RequestActivity;
import com.damsdev.tbc.databinding.DialogNakesBinding;
import com.damsdev.tbc.databinding.FragmentProfilPasienBinding;
import com.damsdev.tbc.model.NakesModel;
import com.damsdev.tbc.util.AlarmReceiver;
import com.damsdev.tbc.util.DbReference;
import com.damsdev.tbc.util.SharedPrefManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfilNakesFragment extends Fragment {
    Intent alarmIntent;
    PendingIntent pendingIntent;
    Context context;
    String LOG = "LOG_PROFIL_NAKES_FRAGMENT";
    private FragmentProfilPasienBinding binding;
    private SharedPrefManager sharedPrefManager;
    private AlarmManager alarmManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefNakes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
        sharedPrefManager = new SharedPrefManager(context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfilPasienBinding.inflate(inflater, container, false);
        binding.btnGoogleLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        binding.linearPendamping.setVisibility(View.GONE);
        binding.cardNakes.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(requireActivity())
                .asBitmap()
                .load(user != null ? user.getPhotoUrl() : "")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user)
                .dontAnimate()
//                .apply(requestOptions)
                .into(binding.ivProfil);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        dbRefNakes = firebaseDatabase.getReference(DbReference.NAKES);
        getNakes(dbRefNakes.orderByChild("idNakes").equalTo(firebaseUser.getUid()));
        return binding.getRoot();
    }

    private void getNakes(Query dbref) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Log.d(LOG, "Queryyy: " + snapshot.getValue());
                } else {
                    Log.d(LOG, "Queryyy: " + snapshot.getValue());

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NakesModel model = dataSnapshot.getValue(NakesModel.class);
                        binding.tvNama.setText(model != null ? model.getNama() : "-");
                        binding.tvTglLahir.setText(model != null ? model.getTglLahir() : "-");
                        binding.tvAlamat.setText(model != null ? model.getAlamat() : "-");
                        binding.tvHp.setText(model != null ? model.getNoHp() : "-");
                        binding.tvEmail.setText(model != null ? model.getEmail() : "-");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(RequestActivity.this, "Gagal " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(LOG, "Query: " + error.getDetails());
            }
        };
        dbref.addValueEventListener(valueEventListener);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(requireActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        sharedPrefManager.saveString(SharedPrefManager.SP_ID_NAKES,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_ID_PASIEN,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_ID_AKTIVITAS,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_TERAKHIR_POST,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_IS_ALARM_AKTIF,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_ALARM_HOUR,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_ALARM_MINUTS,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_MULAI,"");
                        sharedPrefManager.saveString(SharedPrefManager.SP_TGL_SELESAI,"");
                        sharedPrefManager.clearAll();
                        cancelAlarm();

                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                });
    }

    private void cancelAlarm() {
        // If the alarm has been set, cancel it.
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}