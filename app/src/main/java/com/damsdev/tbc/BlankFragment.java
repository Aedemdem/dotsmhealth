package com.damsdev.tbc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.damsdev.tbc.model.AktivitasModel;
import com.damsdev.tbc.util.DbReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BlankFragment extends Fragment {
    static final String LOG = "LOG_FRAGMENT";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRefAktivitas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRefAktivitas = firebaseDatabase.getReference(DbReference.AKTIVITAS);

        // post
//        postAktivitas(dbRefAktivitas);
        // edit
//        editAktivitas(dbRefAktivitas);
        // hapus
//        hapusAktivitas(dbRefAktivitas);

        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    private void hapusAktivitas(DatabaseReference dbRef) {
        String id = "-NZdXwLqwJX7QXdRG-cg";
        dbRef.child(id).removeValue();
    }

    private void editAktivitas(DatabaseReference dbRef) {
        Log.d(LOG, "editAktivitas");
        String id = "-NZdXwLqwJX7QXdRG-cg";
        dbRef = dbRef.child(id);
//        AktivitasModel aktivitasModel = new AktivitasModel(ServerValue.TIMESTAMP, "10", "20-10-2023", false);
        Map<String, Object> map = new HashMap<>();
        map.put("total", "100");
        map.put("tglMulai", "10-10-2022");
        map.put("status", false);

        dbRef.updateChildren(map);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(LOG, "Updated");
                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG, "Uodated error");
                Toast.makeText(getActivity(), "Update Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void postAktivitas(DatabaseReference dbRef) {
            AktivitasModel aktivitasModel = new AktivitasModel( "", "30", "20-07-2023","20-08-2023");
            dbRef.push().setValue(aktivitasModel).addOnSuccessListener(requireActivity(), new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(LOG, "Post aktivitas suksess");
                    Toast.makeText(requireActivity(), "Ke mainmenu", Toast.LENGTH_SHORT).show();
                }
            });
        }
}