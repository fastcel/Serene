package com.example.serene;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class JournalLockFragment extends Fragment {
    EditText etPin;
    Button btnUnlock;
    String storedPin = null;
    public JournalLockFragment() {
        super(R.layout.fragment_journal_lock);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPin = view.findViewById(R.id.etPin);
        btnUnlock = view.findViewById(R.id.btnUnlock);
        loadPinFromDB();
        btnUnlock.setOnClickListener(v -> {
            String enteredPin = etPin.getText().toString().trim();
            if (TextUtils.isEmpty(enteredPin) || enteredPin.length() != 4) {
                etPin.setError("Enter 4-digit PIN");
                return;
            }
            if (storedPin == null) {
                unlock();
                return;
            }
            if (enteredPin.equals(storedPin)) {
                unlock();
            } else {
                etPin.setError("Incorrect PIN");
            }
        });
    }
    private void loadPinFromDB() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                storedPin = snapshot.child("journalPin").getValue(String.class);
                Boolean isLocked = snapshot.child("journalLock").getValue(Boolean.class);
                if (isLocked == null || !isLocked) {
                    unlock();
                }
            }
        });
    }
    private void unlock() {
        JournalFragment parent = (JournalFragment) getParentFragment();
        if (parent != null) {
            parent.loadRoot(new JournalListFragment());
        }
    }
}