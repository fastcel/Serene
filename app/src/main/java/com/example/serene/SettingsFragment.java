package com.example.serene;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    TextView btnEditAvatar, btnChangePassword;
    EditText etUsername, etEmail, etPin;
    Switch switchLock;
    MaterialButton btnSave;

    View loadingOverlay, contentContainer;

    FirebaseAuth auth;
    DatabaseReference userRef;

    String userId;

    boolean isLockEnabled = false;

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AvatarManager.loadInto(view.findViewById(R.id.imgAvatar));

        initViews(view);
        initFirebase();
        setupListeners();

        loadUserData();
    }

    // ---------------- INIT ----------------

    private void initViews(View view) {
        btnEditAvatar = view.findViewById(R.id.btnEditAvatar);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPin = view.findViewById(R.id.etPin);

        switchLock = view.findViewById(R.id.switchLock);
        btnSave = view.findViewById(R.id.btnSave);

        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        contentContainer = view.findViewById(R.id.contentContainer);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);
        }
    }

    // ---------------- LOAD USER ----------------

    private void loadUserData() {
        showLoading();

        if (userRef == null) {
            hideLoading();
            return;
        }

        userRef.get().addOnSuccessListener(snapshot -> {

            hideLoading();

            if (!isAdded()) return;

            String username = snapshot.child("username").getValue(String.class);
            String email = snapshot.child("email").getValue(String.class);
            String pin = snapshot.child("journalPin").getValue(String.class);
            Boolean lock = snapshot.child("journalLock").getValue(Boolean.class);

            etUsername.setText(username != null ? username : "");
            etEmail.setText(email != null ? email : "");

            if (pin != null) etPin.setText(pin);

            isLockEnabled = lock != null && lock;
            switchLock.setChecked(isLockEnabled);

            etPin.setVisibility(isLockEnabled ? View.VISIBLE : View.GONE);

        }).addOnFailureListener(e -> hideLoading());
    }

    // ---------------- LISTENERS ----------------

    private void setupListeners() {

        btnEditAvatar.setOnClickListener(v ->{
                Intent intent = new Intent(getContext(), AvatarSelectionActivity.class);
                intent.putExtra("isEdit", true);
                startActivity(intent);
            }
        );

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isLockEnabled = isChecked;
            etPin.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSave.setOnClickListener(v -> saveSettings());
    }

    // ---------------- SAVE ----------------

    private void saveSettings() {

        String username = etUsername.getText().toString().trim();
        String newPin = etPin.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Enter username");
            return;
        }

        // -------- LOCK LOGIC --------

        if (isLockEnabled) {

            if (newPin.length() != 4) {
                etPin.setError("PIN must be 4 digits");
                return;
            }

            // If PIN already exists → require old PIN
            userRef.child("journalPin").get().addOnSuccessListener(snapshot -> {

                String existingPin = snapshot.getValue(String.class);

                if (existingPin != null) {
                    // 🔒 Changing existing PIN → verify first
                    showVerifyPinDialog(existingPin, newPin, username);
                } else {
                    // 🆕 First time set
                    updateSettings(username, newPin);
                }

            });

        } else {
            // Lock turned OFF → verify before removing
            userRef.child("journalPin").get().addOnSuccessListener(snapshot -> {

                String existingPin = snapshot.getValue(String.class);

                if (existingPin != null) {
                    showVerifyPinDialog(existingPin, null, username);
                } else {
                    updateSettings(username, null);
                }

            });
        }
    }

    // ---------------- PASSWORD ----------------

    private void showChangePasswordDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_change_password, null);

        EditText etOld = dialogView.findViewById(R.id.etOldPass);
        EditText etNew = dialogView.findViewById(R.id.etNewPass);
        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {

            String oldPass = etOld.getText().toString().trim();
            String newPass = etNew.getText().toString().trim();

            if (TextUtils.isEmpty(oldPass)) {
                etOld.setError("Enter current password");
                return;
            }

            if (newPass.length() < 6) {
                etNew.setError("Min 6 characters");
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return;

            showLoading();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential)
                    .addOnSuccessListener(unused ->
                            user.updatePassword(newPass)
                                    .addOnSuccessListener(unused1 -> {
                                        hideLoading();
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        hideLoading();
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    )
                    .addOnFailureListener(e -> {
                        hideLoading();
                        etOld.setError("Incorrect password");
                    });
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // ---------------- LOADING ----------------

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }

        if (contentContainer != null) {
            contentContainer.setVisibility(View.INVISIBLE); // 🔥 hides everything
        }
    }

    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }

        if (contentContainer != null) {
            contentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showVerifyPinDialog(String existingPin, String newPin, String username) {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_verify_pin, null);

        EditText etPin = dialogView.findViewById(R.id.etVerifyPin);
        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        com.google.android.material.button.MaterialButton btnVerify =
                dialogView.findViewById(R.id.btnVerify);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnVerify.setOnClickListener(v -> {

            String entered = etPin.getText().toString().trim();

            if (entered.length() != 4) {
                etPin.setError("Enter 4-digit PIN");
                return;
            }

            if (!entered.equals(existingPin)) {
                etPin.setError("Incorrect PIN");
                return;
            }

            dialog.dismiss();
            updateSettings(username, newPin);
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void updateSettings(String username, String pin) {

        showLoading();

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("journalLock", isLockEnabled);

        if (isLockEnabled && pin != null) {
            updates.put("journalPin", pin);
        } else {
            updates.put("journalPin", null);
        }

        userRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    hideLoading();
                    Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        AvatarManager.loadInto(getView().findViewById(R.id.imgAvatar));
    }

}