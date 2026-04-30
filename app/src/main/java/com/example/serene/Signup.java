package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Bind views (IMPORTANT: IDs must exist in XML)
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // VALIDATION
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Enter username");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be 6+ characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // FIREBASE SIGNUP
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                        // go to login OR home
                        Intent intent = new Intent(Signup.this, Login.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}