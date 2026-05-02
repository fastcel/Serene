package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddJournalFragment extends Fragment {

    // Views
    private EditText etTitle, etContent;
    private TextView btnSave, tvDate, tvCalendarToggle;
    private CalendarView calendarView;
    private LinearLayout layoutDateRow;

    // Theme chips
    private TextView themeStress, themeWork, themeFamily,
            themeHealth, themeRelationships, themeSelf, themeOther;

    // State
    private final List<String> selectedThemes = new ArrayList<>();
    private String selectedDate = "";
    private boolean calendarVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_journal, container, false);

        initViews(view);
        setTodayDate();
        setupCalendar();
        setupThemeChips();

        btnSave.setOnClickListener(v -> saveJournal());

        return view;
    }

    private void initViews(View view) {
        etTitle          = view.findViewById(R.id.etTitle);
        etContent        = view.findViewById(R.id.etContent);
        btnSave          = view.findViewById(R.id.btnSave);
        tvDate           = view.findViewById(R.id.tvDate);
        tvCalendarToggle = view.findViewById(R.id.tvCalendarToggle);
        calendarView     = view.findViewById(R.id.calendarView);
        layoutDateRow    = view.findViewById(R.id.layoutDateRow);

        themeStress        = view.findViewById(R.id.themeStress);
        themeWork          = view.findViewById(R.id.themeWork);
        themeFamily        = view.findViewById(R.id.themeFamily);
        themeHealth        = view.findViewById(R.id.themeHealth);
        themeRelationships = view.findViewById(R.id.themeRelationships);
        themeSelf          = view.findViewById(R.id.themeSelf);
        themeOther         = view.findViewById(R.id.themeOther);
    }

    // ── DATE ──────────────────────────────────────────────────────────────────

    private void setTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        tvDate.setText(selectedDate);
    }


    private void setupCalendar() {

        layoutDateRow.setOnClickListener(v -> {

            com.google.android.material.datepicker.MaterialDatePicker<Long> picker =
                    com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select Date")
                            .build();

            picker.show(getParentFragmentManager(), "DATE_PICKER");

            picker.addOnPositiveButtonClickListener(selection -> {

                Date date = new Date(selection);

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
                selectedDate = sdf.format(date);

                tvDate.setText(selectedDate);
            });
        });
    }

    private void setupThemeChips() {
        setupChip(themeStress,        "Stress");
        setupChip(themeWork,          "Work");
        setupChip(themeFamily,        "Family");
        setupChip(themeHealth,        "Health");
        setupChip(themeRelationships, "Relationships");
        setupChip(themeSelf,          "Self");
        setupChip(themeOther,         "Other");
    }

    private void setupChip(TextView chip, String theme) {
        chip.setOnClickListener(v -> {

            if (selectedThemes.contains(theme)) {
                selectedThemes.remove(theme);

                chip.setBackgroundResource(R.drawable.chip_unselected);
                chip.setTextColor(0xFF3A3A7A); // reset color
            } else {
                selectedThemes.add(theme);

                chip.setBackgroundResource(R.drawable.chip_selected);
                chip.setTextColor(0xFFFFFFFF);
            }
        });
    }

    // ── SAVE ──────────────────────────────────────────────────────────────────

    private void saveJournal() {

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Please add a title");
            return;
        }

        if (content.isEmpty()) {
            etContent.setError("Please write something");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("journals");

        String journalId = ref.push().getKey();

        Journal journal = new Journal(
                title,
                content,
                selectedDate,
                System.currentTimeMillis(),
                new ArrayList<>(selectedThemes),
                false
        );

        ref.child(journalId).setValue(journal)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Journal saved!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}