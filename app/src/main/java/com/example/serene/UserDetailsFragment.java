package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;

public class UserDetailsFragment extends Fragment {
    private static final String ARG_UID = "uid";
    private String uid;
    private TextView tvUsername, tvEmail, tvJournals, tvGoals;
    private DatabaseReference userRef;
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvJournals = view.findViewById(R.id.tvJournals);
        tvGoals = view.findViewById(R.id.tvGoals);
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        loadUser();
        return view;
    }
    private void loadUser() {
        if (uid == null) return;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                tvUsername.setText(username != null ? username : "N/A");
                tvEmail.setText(email != null ? email : "N/A");
                DataSnapshot journals = snapshot.child("journals");
                if (journals.exists()) {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (DataSnapshot j : journals.getChildren()) {
                        String title = j.child("title").getValue(String.class);
                        String date = j.child("data").getValue(String.class);
                        StringBuilder themes = new StringBuilder();
                        for (DataSnapshot t : j.child("themes").getChildren()) {
                            String val = t.getValue(String.class);
                            if (val != null) {
                                if (themes.length() > 0) themes.append(", ");
                                themes.append(val);
                            }
                        }
                        sb.append(count++).append(". ").append(title != null ? title : "Untitled").append("\n");
                        if (themes.length() > 0)
                            sb.append("Themes: ").append(themes).append("\n");
                        sb.append("\n");
                    }
                    tvJournals.setText(sb.toString().trim());
                } else {
                    tvJournals.setText("No journals found");
                }
                DataSnapshot goals = snapshot.child("goals");
                if (goals.exists()) {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (DataSnapshot g : goals.getChildren()) {
                        String title = g.child("title").getValue(String.class);
                        String status = g.child("status").getValue(String.class);
                        String date = g.child("date").getValue(String.class);
                        String time = g.child("time").getValue(String.class);
                        String priority = g.child("priority").getValue(String.class);
                        sb.append(count++).append(". ").append(title != null ? title : "Untitled").append("\n");
                        sb.append("Status: ").append(status != null ? status : "pending").append("\n");
                        sb.append("Priority: ").append(priority != null ? priority : "medium").append("\n");
                        if (date != null && !date.isEmpty())
                            sb.append("Date: ").append(date)
                                    .append(time != null && !time.isEmpty() ? " at " + time : "")
                                    .append("\n");
                        sb.append("\n");
                    }
                    tvGoals.setText(sb.toString().trim());
                } else {
                    tvGoals.setText("No goals found");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvUsername.setText("Error: " + error.getMessage());
            }
        });
    }

}