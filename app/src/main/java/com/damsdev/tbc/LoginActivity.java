package com.damsdev.tbc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.damsdev.tbc.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    String email,password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.progressBar.setVisibility(View.GONE);
        binding.btnGoogleLogin.setOnClickListener(view -> createSignInIntent());
        binding.linearBuatAkun.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginEmail.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (isValid()) {
                signEmail(email, password);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//            Toast.makeText(this, "Anda sudah login : "+ mAuth.getCurrentUser(), Toast.LENGTH_SHORT).show();
            mAuth.getCurrentUser();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
//            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show();
        }

        getVersionName();
    }
    boolean isValid() {
        try {
            email = Objects.requireNonNull(binding.etUsername.getText()).toString();
            password = Objects.requireNonNull(binding.etPassword.getText()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (email.isEmpty()) {
            binding.etUsername.setError("Masukan email");
            return false;
        }
        binding.etUsername.setError(null);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            binding.etUsername.setError("Email tidak valid");
            return false;
        }
        binding.etUsername.setError(null);

        if (password.isEmpty()) {
            binding.etPassword.setError("Masukan password");
            return false;
        }
        binding.etPassword.setError(null);

        return true;
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            Toast.makeText(this, "Success : "+ (user != null ? user.getDisplayName() : "-"), Toast.LENGTH_SHORT).show();

            toSplashScreen();
            // ...
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void toSplashScreen() {
        Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    void signEmail(String email, String password){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        toSplashScreen();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Gagal login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getVersionName() {
        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pkgInfo != null;
        String ver = pkgInfo.versionName;
        binding.tvVersi.setText("Versi " + ver);
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(LoginActivity.this, "Anda logout", Toast.LENGTH_SHORT).show();
                    }
                });
        // [END auth_fui_signout]
    }

}