package com.damsdev.tbc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.damsdev.tbc.databinding.ActivityLoginEmailBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginEmail extends AppCompatActivity {

    ActivityLoginEmailBinding binding;
    String email, password, confirmPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Buat Akun");

        auth = FirebaseAuth.getInstance();

        binding.progressBar.setVisibility(View.GONE);
        binding.textInputConfirPassword.setEnabled(false);
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i>6) {
                    binding.textInputConfirPassword.setEnabled(true);
                    binding.textInputPassword.setError(null);
                } else {
                    binding.textInputConfirPassword.setEnabled(false);
                    binding.textInputPassword.setError("Password minimal 8 karakter");
                }
                Log.d("HAHAHAHAHA", i + " | " + i1 + " | " + i2);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.btnLogin.setOnClickListener(view -> {
            if (isValid()) {
                simpan();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    boolean isValid() {
        try {
            email = Objects.requireNonNull(binding.etEmail.getText()).toString();
            password = Objects.requireNonNull(binding.etPassword.getText()).toString();
            confirmPassword = Objects.requireNonNull(binding.etPasswordConfirm.getText()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (email.isEmpty()) {
            binding.textInputEmail.setError("Masukan email");
            return false;
        }
        binding.textInputEmail.setError(null);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            binding.textInputEmail.setError("Email tidak valid");
            return false;
        }
        binding.textInputEmail.setError(null);

        if (password.isEmpty()) {
            binding.etPassword.setError("Masukan password");
            return false;
        }
        binding.etPassword.setError(null);

        if (!password.equals(confirmPassword)) {
            binding.etPasswordConfirm.setError("Password tidak cocok");
            return false;
        }
        binding.etPasswordConfirm.setError(null);

        return true;
    }

    private void simpan() {
        binding.btnLogin.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sukses: pengguna berhasil terdaftar
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(LoginEmail.this, SplashScreen.class);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    } else {
                        binding.btnLogin.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Gagal mendaftar akun : " + email, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}