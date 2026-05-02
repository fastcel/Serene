package com.example.serene;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class JournalLockFragment extends Fragment {

    public JournalLockFragment() {
        super(R.layout.fragment_journal_lock);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etPin = view.findViewById(R.id.etPin);
        Button btnUnlock = view.findViewById(R.id.btnUnlock);

        btnUnlock.setOnClickListener(v -> {

            String enteredPin = etPin.getText().toString();

            if (enteredPin.length() != 4) {
                etPin.setError("Enter 4-digit PIN");
                return;
            }

            if (isCorrectPin(enteredPin)) {

                JournalFragment parent = (JournalFragment) getParentFragment();

//                parent.setUnlocked(true);

                parent.loadRoot(new JournalListFragment());

            } else {
                etPin.setError("Incorrect PIN");
            }
        });
    }

    private boolean isCorrectPin(String pin) {
        // TEMP: hardcoded (you can store this later)
        return pin.equals("1234");
    }
}