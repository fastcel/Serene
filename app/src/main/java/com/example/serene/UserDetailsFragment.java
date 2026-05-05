package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private String uid;

    private RecyclerView rvGoals, rvJournals;
    private GoalsAdapter goalsAdapter;
    private JournalsAdapter journalsAdapter;

    private List<Goal> goalsList = new ArrayList<>();
    private List<Journal> journalList = new ArrayList<>();

    private DatabaseReference userRef;
    private TextView tvEmptyJournals, tvEmptyGoals;

    public static UserDetailsFragment newInstance(String uid) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString(ARG_UID);
        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        tvEmptyJournals = view.findViewById(R.id.tvEmptyJournals);
        tvEmptyGoals = view.findViewById(R.id.tvEmptyGoals);
        rvGoals = view.findViewById(R.id.rvGoals);
        rvJournals = view.findViewById(R.id.rvJournals);
        Button btnAddJournal = view.findViewById(R.id.btnAddJournal);
        Button btnAddGoal = view.findViewById(R.id.btnAddGoal);

        btnAddGoal.setOnClickListener(v -> showAddGoalDialog());
        btnAddJournal.setOnClickListener(v -> showAddJournalDialog());

        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJournals.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();

        return view;
    }

    private void showAddJournalDialog() {

        android.view.LayoutInflater inflater =
                android.view.LayoutInflater.from(getContext());

        View dialogView = inflater.inflate(R.layout.dialog_edit_journal, null);

        android.widget.EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        android.widget.EditText inputContent = dialogView.findViewById(R.id.inputContent);
        android.widget.EditText inputThemes = dialogView.findViewById(R.id.inputThemes);
        android.widget.CheckBox inputFav = dialogView.findViewById(R.id.inputFav);

        TextView titlee = dialogView.findViewById(R.id.tvTitle3);
        titlee.setText("Add Journal");

        inputFav.setChecked(false);

        android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(getContext())
                        .setView(dialogView)
                        .setPositiveButton("Create", null)
                        .setNegativeButton("Cancel", null)
                        .create();

        dialog.setOnShowListener(d -> {

            android.widget.Button btn =
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {

                String title = inputTitle.getText().toString().trim();
                String content = inputContent.getText().toString().trim();
                boolean fav = inputFav.isChecked();

                String themeText = inputThemes.getText().toString().trim();

                if (title.isEmpty()) {
                    inputTitle.setError("Required");
                    return;
                }

                java.util.List<String> themesList = new java.util.ArrayList<>();
                if (!themeText.isEmpty()) {
                    for (String t : themeText.split(",")) {
                        themesList.add(t.trim());
                    }
                }

                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid)
                        .child("journals");

                String id = ref.push().getKey();

                java.util.Map<String, Object> journal = new java.util.HashMap<>();
                journal.put("title", title);
                journal.put("content", content);
                journal.put("isFavorite", fav);
                journal.put("themes", themesList);

                java.text.SimpleDateFormat sdf =
                        new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault());

                journal.put("date", sdf.format(new java.util.Date()));

                long timestamp = System.currentTimeMillis();
                journal.put("timestamp", timestamp);

                ref.child(id).setValue(journal);

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void loadData() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                TextView tvUsername = getView().findViewById(R.id.tvUsername);
                TextView tvEmail = getView().findViewById(R.id.tvEmail);

                String username = snapshot.child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                tvUsername.setText(username != null ? username : "N/A");
                tvEmail.setText(email != null ? email : "N/A");

                goalsList.clear();

                DataSnapshot goalsSnap = snapshot.child("goals");

                if (goalsSnap.exists() && goalsSnap.hasChildren()) {

                    tvEmptyGoals.setVisibility(View.GONE);
                    rvGoals.setVisibility(View.VISIBLE);

                    for (DataSnapshot g : goalsSnap.getChildren()) {

                        Goal goal = new Goal();
                        goal.setId(g.getKey());
                        goal.setTitle(g.child("title").getValue(String.class));
                        goal.setStatus(g.child("status").getValue(String.class));
                        goal.setPriority(g.child("priority").getValue(String.class));
                        goal.setDate(g.child("date").getValue(String.class));
                        goal.setTime(g.child("time").getValue(String.class));

                        goalsList.add(goal);
                    }

                    goalsAdapter = new GoalsAdapter(goalsList, uid);
                    rvGoals.setAdapter(goalsAdapter);

                } else {
                    rvGoals.setVisibility(View.GONE);
                    tvEmptyGoals.setVisibility(View.VISIBLE);
                }

                journalList.clear();

                DataSnapshot journalsSnap = snapshot.child("journals");

                if (journalsSnap.exists() && journalsSnap.hasChildren()) {

                    tvEmptyJournals.setVisibility(View.GONE);
                    rvJournals.setVisibility(View.VISIBLE);

                    for (DataSnapshot j : journalsSnap.getChildren()) {

                        Journal journal = new Journal();
                        journal.id = j.getKey();
                        journal.title = j.child("title").getValue(String.class);
                        journal.content = j.child("content").getValue(String.class);
                        journal.date = j.child("date").getValue(String.class);

                        Boolean fav = j.child("isFavorite").getValue(Boolean.class);
                        journal.isFavorite = fav != null && fav;

                        List<String> themes = new ArrayList<>();
                        for (DataSnapshot t : j.child("themes").getChildren()) {
                            String val = t.getValue(String.class);
                            if (val != null) themes.add(val);
                        }
                        journal.themes = themes;

                        journalList.add(journal);
                    }

                    journalsAdapter = new JournalsAdapter(journalList, uid);
                    rvJournals.setAdapter(journalsAdapter);

                } else {
                    rvJournals.setVisibility(View.GONE);
                    tvEmptyJournals.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAddGoalDialog() {

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        android.widget.EditText title = new android.widget.EditText(getContext());
        title.setHint("Title");

        android.widget.EditText status = new android.widget.EditText(getContext());
        status.setHint("Status (pending/completed)");

        android.widget.EditText priority = new android.widget.EditText(getContext());
        priority.setHint("Priority (low/medium/high)");

        android.widget.EditText date = new android.widget.EditText(getContext());
        date.setHint("Date (dd/MM/yyyy)");

        android.widget.EditText time = new android.widget.EditText(getContext());
        time.setHint("Time (HH:mm)");

        java.text.SimpleDateFormat dateFormat =
                new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());

        java.text.SimpleDateFormat timeFormat =
                new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        String currentDate = dateFormat.format(new java.util.Date());
        String currentTime = timeFormat.format(new java.util.Date());

        date.setText(currentDate);
        time.setText(currentTime);

        layout.addView(title);
        layout.addView(status);
        layout.addView(priority);
        layout.addView(date);
        layout.addView(time);

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Add Goal")
                .setView(layout)
                .setPositiveButton("Create", (d, w) -> {

                    DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(uid)
                            .child("goals");

                    String id = ref.push().getKey();

                    java.util.Map<String, Object> goal = new java.util.HashMap<>();
                    goal.put("id", id);
                    goal.put("title", title.getText().toString());
                    goal.put("status", status.getText().toString());
                    goal.put("priority", priority.getText().toString());

                    goal.put("date", date.getText().toString());
                    goal.put("time", time.getText().toString());

                    ref.child(id).setValue(goal);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}