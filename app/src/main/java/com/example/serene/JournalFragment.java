package com.example.serene;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JournalFragment extends Fragment {

    public interface OnUnlockCheckListener {
        void onResult(boolean isUnlocked);
    }

    public JournalFragment() {
        super(R.layout.fragment_journal);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {

            isJournalUnlocked(isUnlocked -> {

                if (!isAdded()) return;

                if (isUnlocked) {
                    loadRoot(new JournalListFragment());
                } else {
                    loadRoot(new JournalLockFragment());
                }
            });
        }
    }

    private void isJournalUnlocked(OnUnlockCheckListener callback) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            callback.onResult(false);
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        ref.get().addOnSuccessListener(snapshot -> {

            Boolean isLocked = snapshot.child("journalLock").getValue(Boolean.class);

            // unlocked if lock is OFF
            boolean unlocked = (isLocked == null || !isLocked);

            callback.onResult(unlocked);

        }).addOnFailureListener(e -> {
            callback.onResult(false);
        });
    }

    public void loadRoot(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.journalContainer, fragment)
                .commit();
    }

    public void openFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.journalContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}