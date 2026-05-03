package com.example.serene;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class GoalsFragment extends Fragment {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    TextView btnAddGoal;

    private GoalsPagerAdapter adapter;
    private DatabaseReference goalsRef;   // ← moved here

    public GoalsFragment() {
        super(R.layout.fragment_goals);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager   = view.findViewById(R.id.viewPager);
        tabLayout   = view.findViewById(R.id.tabLayout);
        btnAddGoal  = view.findViewById(R.id.btnAddGoal);

        // Firebase lives here now
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        goalsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("goals");

        adapter = new GoalsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String[] labels = {"All", "Pending", "Completed", "Overdue"};
            tab.setText(labels[position]);
        }).attach();

        // No tab-switching needed — dialog works from any tab
        btnAddGoal.setOnClickListener(v -> showAddGoalDialog());
    }

    // ─── ADD GOAL DIALOG (was in AllGoalsFragment) ───────────────────────────

    private String selectedDate     = "";
    private String selectedTime     = "";
    private String selectedPriority = "medium";

    private void showAddGoalDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_goal, null);

        TextView dialogTitle = dialogView.findViewById(R.id.tvTitle);
        dialogTitle.setText("Add Goal");

        EditText etGoal  = dialogView.findViewById(R.id.etGoal);
        TextView tvDate  = dialogView.findViewById(R.id.tvDate);
        TextView tvTime  = dialogView.findViewById(R.id.tvTime);
        TextView low     = dialogView.findViewById(R.id.priorityLow);
        TextView medium  = dialogView.findViewById(R.id.priorityMedium);
        TextView high    = dialogView.findViewById(R.id.priorityHigh);

        selectedDate     = "";
        selectedTime     = "";
        selectedPriority = "medium";
        updatePriorityUI(low, medium, high, selectedPriority);

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

        tvDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (picker, year, month, day) -> {
                        selectedDate = String.format(Locale.getDefault(),
                                "%02d/%02d/%04d", day, month + 1, year);
                        tvDate.setText("📅 " + selectedDate);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        tvTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(requireContext(),
                    (picker, hour, minute) -> {
                        selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d", hour, minute);
                        tvTime.setText("⏰ " + selectedTime);
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
            ).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnSaveGoal).setOnClickListener(v -> {
            String text = etGoal.getText().toString().trim();
            if (text.isEmpty()) { etGoal.setError("Enter a goal"); return; }
            if (selectedTime.isEmpty()) selectedTime = "23:59";

            String id = goalsRef.push().getKey();
            if (id == null) return;

            goalsRef.child(id).setValue(
                    new Goal(id, text, "pending", selectedPriority, selectedDate, selectedTime)
            );
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updatePriorityUI(TextView low, TextView medium, TextView high, String selected) {
        low.setBackgroundResource(R.drawable.chip_unselected);
        medium.setBackgroundResource(R.drawable.chip_unselected);
        high.setBackgroundResource(R.drawable.chip_unselected);
        low.setTextColor(getResources().getColor(android.R.color.black));
        medium.setTextColor(getResources().getColor(android.R.color.black));
        high.setTextColor(getResources().getColor(android.R.color.black));

        TextView target = "low".equals(selected) ? low : "high".equals(selected) ? high : medium;
        target.setBackgroundResource(R.drawable.chip_selected);
        target.setTextColor(getResources().getColor(android.R.color.white));
    }

    // Let the pager adapter (or other fragments) get the ref if needed
    public DatabaseReference getGoalsRef() { return goalsRef; }
}