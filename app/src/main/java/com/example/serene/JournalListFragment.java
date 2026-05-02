package com.example.serene;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JournalListFragment extends Fragment {

    RecyclerView recyclerView;
    JournalAdapter adapter;
    List<Journal> journalList = new ArrayList<>();
    TextView btnViewFavorites;
    View fabAddJournal;
    View emptyState;

    DatabaseReference journalRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_journal_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerJournals);
        emptyState = view.findViewById(R.id.emptyState);
        fabAddJournal = view.findViewById(R.id.fabAddJournal);
        btnViewFavorites = view.findViewById(R.id.btnViewFavorites);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddJournal.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AddJournalFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadJournalsFromFirebase();
        btnViewFavorites.setOnClickListener(v -> {

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new JournalFavouritesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadJournalsFromFirebase() {

        System.out.println("LOAD JOURNALS CALLED");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            System.out.println("USER IS NULL (not logged in)");

            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);

            return;
        }

        String userId = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        journalRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("journals");

        journalRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println("FIREBASE DATA RECEIVED: " + snapshot.getChildrenCount());

                journalList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Journal journal = ds.getValue(Journal.class);
                    if (journal != null) {
                        journal.id = ds.getKey();
                        journalList.add(journal);
                    }
                }

                if (getContext() == null) return;

                if (journalList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (adapter == null) {
                        adapter = new JournalAdapter(journalList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(journalList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FIREBASE ERROR: " + error.getMessage());
            }
        });
    }
}