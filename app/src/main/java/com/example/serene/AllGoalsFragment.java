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
    private String userId;

    private String selectedDate = "";
    private String selectedTime = "";
    private String selectedPriority = "medium";

    public AllGoalsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_goals, container, false);

        recyclerGoals = view.findViewById(R.id.recyclerGoals);
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
        loadGoals();

        return view;
    }

    private void setupRecyclerView() {
        goalAdapter = new GoalAdapter(getContext(), new ArrayList<>(), goalsRef);
        recyclerGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerGoals.setAdapter(goalAdapter);
    }

    // ---------------- ADD GOAL ----------------
    public void showAddGoalDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_goal, null);

        TextView dialogTitle = dialogView.findViewById(R.id.tvTitle);
        dialogTitle.setText("Add Goal");
        EditText etGoal = dialogView.findViewById(R.id.etGoal);
        TextView tvDate = dialogView.findViewById(R.id.tvDate);
        TextView tvTime = dialogView.findViewById(R.id.tvTime);

        TextView low = dialogView.findViewById(R.id.priorityLow);
        TextView medium = dialogView.findViewById(R.id.priorityMedium);
        TextView high = dialogView.findViewById(R.id.priorityHigh);

        selectedDate = "";
        selectedTime = "";
        selectedPriority = "medium";

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        // ---------------- PRIORITY FIX (REAL FIX) ----------------
        low.setOnClickListener(v -> {
            selectedPriority = "low";
            updatePriorityUI(low, medium, high, selectedPriority);
        });

        medium.setOnClickListener(v -> {
            selectedPriority = "medium";
            updatePriorityUI(low, medium, high, selectedPriority);
        });

        high.setOnClickListener(v -> {
            selectedPriority = "high";
            updatePriorityUI(low, medium, high, selectedPriority);
        });

        // ---------------- DATE PICKER ----------------
        tvDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {

                        selectedDate = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                (month + 1),
                                year
                        );

                        tvDate.setText("📅 " + selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            picker.show();
        });

        // ---------------- TIME PICKER ----------------
        tvTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePicker = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {

                        selectedTime = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                hourOfDay,
                                minute
                        );

                        tvTime.setText("⏰ " + selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );

            timePicker.show();
        });

        // ---------------- SAVE ----------------
        dialogView.findViewById(R.id.btnSaveGoal).setOnClickListener(v -> {

            String text = etGoal.getText().toString().trim();

            if (text.isEmpty()) {
                etGoal.setError("Enter a goal");
                return;
            }

            if (selectedTime.isEmpty()) {
                selectedTime = "23:59";
            }

            String id = goalsRef.push().getKey();
            if (id == null) return;

            Goal goal = new Goal(
                    id,
                    text,
                    "pending",
                    selectedPriority,
                    selectedDate,
                    selectedTime
            );

            goalsRef.child(id).setValue(goal);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updatePriorityUI(TextView low, TextView medium, TextView high, String selected) {

        // reset all
        low.setBackgroundResource(R.drawable.chip_unselected);
        medium.setBackgroundResource(R.drawable.chip_unselected);
        high.setBackgroundResource(R.drawable.chip_unselected);

        low.setTextColor(getResources().getColor(android.R.color.black));
        medium.setTextColor(getResources().getColor(android.R.color.black));
        high.setTextColor(getResources().getColor(android.R.color.black));

        // highlight selected
        switch (selected) {
            case "low":
                low.setBackgroundResource(R.drawable.chip_selected);
                low.setTextColor(getResources().getColor(android.R.color.white));
                break;

            case "medium":
                medium.setBackgroundResource(R.drawable.chip_selected);
                medium.setTextColor(getResources().getColor(android.R.color.white));
                break;

            case "high":
                high.setBackgroundResource(R.drawable.chip_selected);
                high.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }
    // ---------------- LOAD ----------------
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
                Toast.makeText(getContext(),
                        "Failed to load goals",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------- OVERDUE CHECK ----------------
    private void checkAndMarkOverdue(Goal goal) {

        if (goal.getDate() == null || goal.getDate().isEmpty()) return;
        if ("completed".equals(goal.getStatus())) return;

        try {
            String time = goal.getTime();

            // fallback if time missing
            if (time == null || time.isEmpty()) {
                time = "23:59";
            }

            String dateTime = goal.getDate() + " " + time;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            sdf.setLenient(false); // 🔥 important

            Date due = sdf.parse(dateTime);

            if (due == null) return;

            if (due.before(new Date())) {
                goal.setStatus("overdue");

                goalsRef.child(goal.getId())
                        .child("status")
                        .setValue("overdue");
            }

        } catch (Exception e) {
            e.printStackTrace(); // helps debugging
        }
    }
    private void updateUI() {

        goalAdapter.updateList(allGoals);

        if (allGoals.isEmpty()) {
            recyclerGoals.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerGoals.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}