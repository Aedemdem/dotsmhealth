package com.damsdev.tbc;

import static android.icu.text.MessagePattern.Part.Type.ARG_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.damsdev.tbc.databinding.ActivityMainBinding;
import com.damsdev.tbc.nakes.HomeFragment;
import com.damsdev.tbc.nakes.PermintaanFragment;
import com.damsdev.tbc.nakes.ProfilNakesFragment;
import com.damsdev.tbc.pasien.HomePasienFragment;
import com.damsdev.tbc.pasien.PengingatFragment;
import com.damsdev.tbc.pasien.ProfilPasienFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    final FragmentManager fm = getSupportFragmentManager();
    String level = "";
    String LOG = "LOG_MAIN_ACTIVITY";
    Context mContext;
    //    SharedPrefManager sharedPrefManager;
    Fragment active;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;
    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_diskusi:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;
            case R.id.navigation_materi:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;
            case R.id.navigation_tugas:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    };

    private ActivityMainBinding binding;

    private static final String ARG_NAME = "username";
    public static void startActivity(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_NAME, username);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        mContext = this;
        level = getIntent().getStringExtra("level");

        if (level.equals("pasien")) {
            fragment1 = new HomePasienFragment();
            fragment2 = new PengingatFragment();
            fragment3 = new ProfilPasienFragment();

            binding.navViewBotomNakes.setVisibility(View.GONE);
            binding.navViewBotom.setVisibility(View.VISIBLE);
            binding.navViewBotom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        } else if (level.equals("nakes")) {
            fragment1 = new HomeFragment();
            fragment2 = new PermintaanFragment();
            fragment3 = new ProfilNakesFragment();

            binding.navViewBotomNakes.setVisibility(View.VISIBLE);
            binding.navViewBotom.setVisibility(View.GONE);
            binding.navViewBotomNakes.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

        active = fragment1;

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

    }
}

// TODO: 11/10/23 Pendamping required
