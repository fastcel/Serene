package com.example.serene;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AllGoalsFragment extends Fragment {

    private RecyclerView recyclerGoals;
    private LinearLayout layoutEmptyState;
    private GoalAdapter goalAdapter;
    private final List<Goal> allGoals = new ArrayList<>();
    private DatabaseReference goalsRef;

    public AllGoalsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_goals, container, false);

        recyclerGoals    = view.findViewById(R.id.recyclerGoals);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Get the ref from the parent instead of creating a second Firebase connection
        if (getParentFragment() instanceof GoalsFragment) {
            goalsRef = ((GoalsFragment) getParentFragment()).getGoalsRef();
        }

        if (goalsRef == null) return view;   // safety guard

        setupRecyclerView();
        loadGoals();
        return view;
    }

    private void setupRecyclerView() {
        goalAdapter = new GoalAdapter(getContext(), new ArrayList<>(), goalsRef);
        recyclerGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerGoals.setAdapter(goalAdapter);
    }

    private void loadGoals() {
        goalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allGoals.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Goal goal = ds.getValue(Goal.class);
                    if (goal == null) continue;
                    checkAndMarkOverdue(goal);
                    allGoals.add(goal);
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load goals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndMarkOverdue(Goal goal) {
        if (goal.getDate() == null || goal.getDate().isEmpty()) return;
        if ("completed".equals(goal.getStatus())) return;
        try {
            String time = (goal.getTime() == null || goal.getTime().isEmpty()) ? "23:59" : goal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            sdf.setLenient(false);
            Date due = sdf.parse(goal.getDate() + " " + time);
            if (due != null && due.before(new Date())) {
                goal.setStatus("overdue");
                goalsRef.child(goal.getId()).child("status").setValue("overdue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        goalAdapter.updateList(allGoals);
        boolean empty = allGoals.isEmpty();
        recyclerGoals.setVisibility(empty ? View.GONE : View.VISIBLE);
        layoutEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}