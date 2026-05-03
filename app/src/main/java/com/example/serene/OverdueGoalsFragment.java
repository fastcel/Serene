package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class OverdueGoalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;

    private GoalAdapter adapter;
    private final List<Goal> overdueGoals = new ArrayList<>();

    private DatabaseReference goalsRef;
    private String userId;

    public OverdueGoalsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_overdue_goals, container, false);

        recyclerView = view.findViewById(R.id.recyclerOverdueGoals);
        emptyState   = view.findViewById(R.id.layoutEmptyState);

        if (getParentFragment() instanceof GoalsFragment) {
            goalsRef = ((GoalsFragment) getParentFragment()).getGoalsRef();
        }
        if (goalsRef == null) return view;

        setupRecyclerView();
        loadOverdueGoals();
        return view;
    }

    private void setupRecyclerView() {
        adapter = new GoalAdapter(getContext(), new ArrayList<>(), goalsRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadOverdueGoals() {

        goalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                overdueGoals.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Goal goal = ds.getValue(Goal.class);

                    if (goal != null && "overdue".equals(goal.getStatus())) {
                        overdueGoals.add(goal);
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Failed to load overdue goals",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {

        adapter.updateList(overdueGoals);

        if (overdueGoals.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}