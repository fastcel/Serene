package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnCreateAccount;
    TextView tvLogin;

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
        tvLogin = findViewById(R.id.tvLogin1);

        btnCreateAccount.setOnClickListener(v -> registerUser());

        String text = "Already have an account? Login";

        SpannableString spannable = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        };

        spannable.setSpan(clickableSpan,
                text.indexOf("Login"),
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Make "Login" purple (or any color)
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#A020F0")),
                text.indexOf("Login"),
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLogin.setText(spannable);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setHighlightColor(Color.TRANSPARENT);
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

                        String uid = auth.getCurrentUser().getUid();

                        // SAVE TO SQLITE
                        DBHelper dbHelper = new DBHelper(Signup.this);
                        dbHelper.insertUser(uid, username, email);

                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

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