package com.example.serene;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JournalFragment extends Fragment {

    public JournalFragment() {
        super(R.layout.fragment_journal);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {

            boolean isUnlocked = isJournalUnlocked();

            if (isUnlocked) {
                loadRoot(new JournalListFragment());
            } else {
                loadRoot(new JournalLockFragment());
            }
        }
    }

    private boolean isJournalUnlocked(){
        return false;
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