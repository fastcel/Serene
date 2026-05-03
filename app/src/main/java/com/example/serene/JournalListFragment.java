package com.example.serene;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class JournalListFragment extends Fragment {
    private static final String TAG = "JournalListFragment";
    RecyclerView recyclerView;
    JournalAdapter adapter;
    List<Journal> journalList = new ArrayList<>();
    TextView btnViewFavorites;
    View fabAddJournal;
    View emptyState;
    View loading;
    DatabaseReference journalRef;
    ValueEventListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_journal_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerJournals);
        emptyState = view.findViewById(R.id.emptyState);
        fabAddJournal = view.findViewById(R.id.fabAddJournal);
        btnViewFavorites = view.findViewById(R.id.btnViewFavorites);
        loading = view.findViewById(R.id.loadingJournals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAddJournal.setOnClickListener(v ->
                ((JournalFragment) getParentFragment())
                        .openFragment(new AddJournalFragment())
        );
        btnViewFavorites.setOnClickListener(v ->
                ((JournalFragment) getParentFragment())
                        .openFragment(new JournalFavouritesFragment())
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        loadJournals();
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        detachListener();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        adapter = null;
    }
    private void detachListener() {
        if (journalRef != null && listener != null) {
            Log.d(TAG, "Detaching listener");
            journalRef.removeEventListener(listener);
        }
    }
    private void loadJournals() {
        Log.d(TAG, "loadJournals called");
        showLoading();
        detachListener();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d(TAG, "No user logged in");
            showEmpty();
            return;
        }
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();
        Log.d(TAG, "Loading journals for user: " + userId);
        journalRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("journals");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange called - exists: " + snapshot.exists() + ", count: " + snapshot.getChildrenCount());
                journalList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Journal journal = ds.getValue(Journal.class);
                    if (journal != null) {
                        journal.id = ds.getKey();
                        journalList.add(journal);
                    }
                }
                Log.d(TAG, "Journal list size: " + journalList.size());
                if (!isAdded()) {
                    Log.d(TAG, "Fragment not added, skipping UI update");
                    return;
                }
                if (adapter == null) {
                    Log.d(TAG, "Creating new adapter");
                    adapter = new JournalAdapter(journalList, journal -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("journalId", journal.id);
                        JournalDetailFragment fragment = new JournalDetailFragment();
                        fragment.setArguments(bundle);
                        ((JournalFragment) getParentFragment())
                                .openFragment(fragment);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Updating existing adapter");
                    adapter.updateList(journalList);
                }
                if (journalList.isEmpty()) {
                    showEmpty();
                } else {
                    showList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                if (isAdded()) {
                    showEmpty();
                }
            }
        };
        journalRef.addValueEventListener(listener);
        Log.d(TAG, "Listener attached");
    }

    private void showLoading() {
        Log.d(TAG, "showLoading");
        if (loading == null) return;
        loading.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }
    private void showEmpty() {
        Log.d(TAG, "showEmpty");
        if (emptyState == null) return;
        loading.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
    private void showList() {
        Log.d(TAG, "showList");
        if (recyclerView == null) return;
        loading.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}