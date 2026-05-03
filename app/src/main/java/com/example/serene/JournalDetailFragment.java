package com.example.serene;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class JournalDetailFragment extends Fragment {
    TextView tvTitle, tvDate, tvContent, tvNoThemes;
    ImageView btnDelete;
    LinearLayout layoutThemes;
    TextView btnFavorite;
    boolean isFavorite = false;
    String journalId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_detail, container, false);
        tvTitle = view.findViewById(R.id.tvJournalTitle);
        tvDate = view.findViewById(R.id.tvJournalDate);
        tvContent = view.findViewById(R.id.tvJournalContent);
        layoutThemes = view.findViewById(R.id.layoutThemeChips);
        tvNoThemes = view.findViewById(R.id.tvNoThemes);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnFavorite = view.findViewById(R.id.btnFavorite);
        if (getArguments() != null) {
            journalId = getArguments().getString("journalId");
        }
        loadJournal();
        btnDelete.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("journals")
                    .child(journalId);
            ref.removeValue()
                    .addOnSuccessListener(unused -> {
                        if (getActivity() != null) {
                            getActivity()
                                    .getOnBackPressedDispatcher()
                                    .onBackPressed();
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        });

        btnFavorite.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("journals")
                    .child(journalId);
            isFavorite = !isFavorite;
            ref.child("isFavorite").setValue(isFavorite);
            updateFavoriteUI();
        });
        return view;
    }

    private void updateFavoriteUI() {

        if (isFavorite) {
            btnFavorite.setText("Remove from favorites");
            btnFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.favorite_filled, 0, 0, 0
            );
        } else {
            btnFavorite.setText("Add to favorites");
            btnFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.favorite, 0, 0, 0
            );
        }
    }
    private void loadJournal() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("journals")
                .child(journalId);
        ref.get().addOnSuccessListener(snapshot -> {
            Journal j = snapshot.getValue(Journal.class);
            isFavorite = j.isFavorite;
            updateFavoriteUI();
            if (j == null) return;
            tvTitle.setText(j.title);
            tvDate.setText(j.date);
            tvContent.setText(j.content);
            layoutThemes.removeAllViews();
            if (j.themes != null && !j.themes.isEmpty()) {
                tvNoThemes.setVisibility(View.GONE);
                for (String theme : j.themes) {
                    TextView chip = new TextView(getContext());
                    chip.setText(theme);
                    chip.setTextSize(11f);
                    chip.setPadding(24, 12, 24, 12);
                    Integer color = getThemeColor(theme);
                    chip.setBackgroundResource(R.drawable.chip_unselected);
                    chip.setBackgroundTintList(ColorStateList.valueOf(color));
                    layoutThemes.addView(chip);
                }
            } else {
                tvNoThemes.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {

        });
    }
    private int getThemeColor(String theme) {
        switch (theme) {
            case "Stress": return Color.parseColor("#803040");
            case "Work": return Color.parseColor("#2A3A7A");
            case "Family": return Color.parseColor("#4A2A8A");
            case "Health": return Color.parseColor("#7A5020");
            case "Relationships": return Color.parseColor("#1A4A6A");
            case "Self": return Color.parseColor("#3A3A7A");
            default: return Color.parseColor("#5A5A9A");
        }
    }
}