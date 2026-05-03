package com.example.serene;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView tvDate, tvGreeting;
    private TextView tvHappyCount, tvSadCount, tvAnxiousCount, tvCalmCount, tvStressedCount;
    private ProgressBar progressHappy, progressSad, progressAnxious, progressCalm, progressStressed;
    private TextView tvGoalsCount;
    private ProgressBar progressGoals;
    private LinearLayout moodHappy, moodSad, moodAnxious, moodCalm, moodStressed;
    private DatabaseReference db;
    private String userId;
    Button btnStartPomodoro;
    private View loadingOverlay;
    private int loadingTasks = 0;
    private final String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    public HomeFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvDate = view.findViewById(R.id.tvDate);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvHappyCount = view.findViewById(R.id.tvHappyCount);
        tvSadCount = view.findViewById(R.id.tvSadCount);
        tvAnxiousCount = view.findViewById(R.id.tvAnxiousCount);
        tvCalmCount = view.findViewById(R.id.tvCalmCount);
        tvStressedCount = view.findViewById(R.id.tvStressedCount);
        progressHappy = view.findViewById(R.id.progressHappy);
        progressSad = view.findViewById(R.id.progressSad);
        progressAnxious = view.findViewById(R.id.progressAnxious);
        progressCalm = view.findViewById(R.id.progressCalm);
        progressStressed = view.findViewById(R.id.progressStressed);
        tvGoalsCount = view.findViewById(R.id.tvGoalsCount);
        progressGoals = view.findViewById(R.id.progressGoals);
        moodHappy = view.findViewById(R.id.moodHappy);
        moodSad = view.findViewById(R.id.moodSad);
        moodAnxious = view.findViewById(R.id.moodAnxious);
        moodCalm = view.findViewById(R.id.moodCalm);
        moodStressed = view.findViewById(R.id.moodStressed);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference("users").child(userId);
        btnStartPomodoro = view.findViewById(R.id.btnStartPomodoro);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        AvatarManager.loadInto(view.findViewById(R.id.imgAvatar));
        setupDate();
        loadUsername();
        checkTodayMoodAndSetupClicks();
        loadWeeklyMoods();
        loadGoals();
        btnStartPomodoro.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).navigateTo(R.id.nav_focus);
            }
        });
        return view;
    }
    private void setupDate() {
        String date = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(new Date());
        tvDate.setText(date);
    }
    private void loadUsername() {
        startLoading();
        db.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (!TextUtils.isEmpty(username)) {
                    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    String greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";
                    tvGreeting.setText(greeting + ", " + username);
                }
                stopLoading();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                stopLoading();
            }
        });
    }
    private void checkTodayMoodAndSetupClicks() {
        db.child("moods").child(todayKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    disableMoodButtons();
                } else {
                    enableMoodButtons();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
    private void enableMoodButtons() {
        View.OnClickListener listener = v -> {
            String mood = "";
            int id = v.getId();
            if (id == R.id.moodHappy) mood = "happy";
            else if (id == R.id.moodSad) mood = "sad";
            else if (id == R.id.moodAnxious) mood = "anxious";
            else if (id == R.id.moodCalm) mood = "calm";
            else if (id == R.id.moodStressed) mood = "stressed";
            saveMood(mood);
        };
        moodHappy.setOnClickListener(listener);
        moodSad.setOnClickListener(listener);
        moodAnxious.setOnClickListener(listener);
        moodCalm.setOnClickListener(listener);
        moodStressed.setOnClickListener(listener);
        setMoodAlpha(1.0f);
    }
    private void disableMoodButtons() {
        View.OnClickListener blocked = v ->
                Toast.makeText(getContext(), "Already logged mood today", Toast.LENGTH_SHORT).show();
        moodHappy.setOnClickListener(blocked);
        moodSad.setOnClickListener(blocked);
        moodAnxious.setOnClickListener(blocked);
        moodCalm.setOnClickListener(blocked);
        moodStressed.setOnClickListener(blocked);
        setMoodAlpha(0.4f);
    }
    private void setMoodAlpha(float alpha) {
        moodHappy.setAlpha(alpha);
        moodSad.setAlpha(alpha);
        moodAnxious.setAlpha(alpha);
        moodCalm.setAlpha(alpha);
        moodStressed.setAlpha(alpha);
    }

    private void saveMood(String mood) {
        db.child("moods").child(todayKey).setValue(mood)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Today's mood logged!", Toast.LENGTH_SHORT).show();
                    disableMoodButtons();
                    loadWeeklyMoods();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save mood", Toast.LENGTH_SHORT).show());
    }

    private void loadWeeklyMoods() {
        startLoading();
        db.child("moods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int happy = 0, sad = 0, anxious = 0, calm = 0, stressed = 0;
                for (DataSnapshot daySnap : snapshot.getChildren()) {
                    String mood = daySnap.getValue(String.class);
                    if (mood == null) continue;
                    switch (mood) {
                        case "happy": happy++; break;
                        case "sad": sad++; break;
                        case "anxious": anxious++; break;
                        case "calm": calm++; break;
                        case "stressed": stressed++; break;
                    }
                }
                int max = Math.max(1, Math.max(happy,
                        Math.max(sad, Math.max(anxious, Math.max(calm, stressed)))));
                updateMoodRow(tvHappyCount, progressHappy, happy, max);
                updateMoodRow(tvSadCount, progressSad, sad, max);
                updateMoodRow(tvAnxiousCount, progressAnxious, anxious, max);
                updateMoodRow(tvCalmCount, progressCalm, calm, max);
                updateMoodRow(tvStressedCount, progressStressed, stressed, max);
                stopLoading();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopLoading();
            }
        });
    }
    private void updateMoodRow(TextView countTv, ProgressBar bar, int count, int max) {
        countTv.setText(count + "x");
        bar.setMax(100);
        bar.setProgress(Math.round((count / (float) max) * 100));
    }
    private void loadGoals() {
        startLoading();
        db.child("goals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int completed = 0;
                for (DataSnapshot goalSnap : snapshot.getChildren()) {
                    if (!goalSnap.hasChild("status")) continue;
                    total++;
                    String status = goalSnap.child("status").getValue(String.class);
                    if ("completed".equals(status)) {
                        completed++;
                    }
                }
                if (total == 0) {
                    tvGoalsCount.setText("No goals yet");
                    progressGoals.setProgress(0);
                } else {
                    tvGoalsCount.setText(completed + "/" + total + " done");
                    progressGoals.setMax(100);
                    progressGoals.setProgress((int) ((completed * 100.0f) / total));
                }
                stopLoading();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopLoading();
            }
        });
    }
    private void startLoading() {
        loadingTasks++;
        loadingOverlay.setVisibility(View.VISIBLE);
    }
    private void stopLoading() {
        loadingTasks--;
        if (loadingTasks <= 0) {
            loadingTasks = 0;
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}