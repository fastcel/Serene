package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class PendingGoalsFragment extends Fragment {

    private RecyclerView recyclerGoals;
    private LinearLayout layoutEmptyState;

    private GoalAdapter goalAdapter;
    private final List<Goal> pendingGoals = new ArrayList<>();

    private DatabaseReference goalsRef;
    private String userId;

    public PendingGoalsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending_goals, container, false);

        recyclerGoals = view.findViewById(R.id.recyclerPendingGoals);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        goalsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("goals");

        setupRecyclerView();
        loadPendingGoals();

        return view;
    }

    // ---------------- Recycler ----------------
    private void setupRecyclerView() {

        goalAdapter = new GoalAdapter(getContext(), new ArrayList<>(), goalsRef);

        recyclerGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerGoals.setAdapter(goalAdapter);
    }

    // ---------------- LOAD + FILTER ----------------
    private void loadPendingGoals() {

        goalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pendingGoals.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Goal goal = ds.getValue(Goal.class);

                    if (goal != null && "pending".equals(goal.getStatus())) {
                        pendingGoals.add(goal);
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Failed to load pending goals",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {

        goalAdapter.updateList(pendingGoals);

        if (pendingGoals.isEmpty()) {
            recyclerGoals.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerGoals.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}