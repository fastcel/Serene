package com.example.serene;

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
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // optional: Toast
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
            btnFavorite.setText("★ Remove from favorites");
            btnFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFFD700)
            );
            btnFavorite.setTextColor(0xFF1A1A4A);
        } else {
            btnFavorite.setText("☆ Add to favorites");
            btnFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFD8C8F0)
            );
            btnFavorite.setTextColor(0xFF6058B0);
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
                    chip.setBackgroundResource(R.drawable.chip_unselected);

                    layoutThemes.addView(chip);
                }

            } else {
                tvNoThemes.setVisibility(View.VISIBLE);
            }

        }).addOnFailureListener(e -> {
            // optional error handling
        });
    }
}