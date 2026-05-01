package com.example.serene;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    TextView tvForgot;
    private MaterialButton btnSignIn;
    TextView tvSignup;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;
    MaterialButton btnGoogle;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Bind UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgot = findViewById(R.id.tvForgot);

        // Click listener
        btnSignIn.setOnClickListener(v -> loginUser());
        tvSignup = findViewById(R.id.tvSignup1);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        String text = "No account? Sign Up";

        SpannableString spannable = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        };
        spannable.setSpan(clickableSpan,
                text.indexOf("Sign Up"),
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#A020F0")),
                text.indexOf("Sign Up"),
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignup.setText(spannable);
        tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignup.setHighlightColor(Color.TRANSPARENT);

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });


        tvForgot.setOnClickListener(v -> showForgotPasswordDialog());
    }


    private void showForgotPasswordDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);

        TextInputEditText etResetEmail = dialogView.findViewById(R.id.etResetEmail);
        TextView tvResend = dialogView.findViewById(R.id.tvResend);
        MaterialButton btnSend = dialogView.findViewById(R.id.btnSendReset);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 🔵 SEND BUTTON (main reset)
        btnSend.setOnClickListener(v -> {

            String email = etResetEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etResetEmail.setError("Enter email");
                return;
            }

            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset link sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    "Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 🔵 RESEND CLICKABLE TEXT
        String text = "Haven’t received email? Resend link";

        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf("Resend link");
        int end = text.length();

        ClickableSpan click = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                String email = etResetEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    etResetEmail.setError("Enter email first");
                    return;
                }

                FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(email)
                        .addOnSuccessListener(a ->
                                Toast.makeText(Login.this, "Email resent!", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show()
                        );
            }
        };

        spannable.setSpan(click, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#A020F0")),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvResend.setText(spannable);
        tvResend.setMovementMethod(LinkMovementMethod.getInstance());
        tvResend.setHighlightColor(Color.TRANSPARENT);

        dialog.show();
    }
    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }

        // Firebase login
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // TODO: move to dashboard
                        Intent intent = new Intent(Login.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(Login.this,
                                "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Login Successful", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, UserDashboard.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}