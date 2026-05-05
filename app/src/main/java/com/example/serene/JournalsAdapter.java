package com.example.serene;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JournalsAdapter extends RecyclerView.Adapter<JournalsAdapter.VH> {

    private List<Journal> list;
    private String uid;
    private DatabaseReference ref;

    public JournalsAdapter(List<Journal> list, String uid) {
        this.list = list;
        this.uid = uid;
        ref = FirebaseDatabase.getInstance()
                .getReference("users").child(uid).child("journals");
    }

    class VH extends RecyclerView.ViewHolder {

        TextView title, content, date, themes;
        ImageView favIcon;
        Button edit, delete;

        VH(View v) {
            super(v);

            title = v.findViewById(R.id.title2);
            content = v.findViewById(R.id.contentText);
            date = v.findViewById(R.id.dateText);
            themes = v.findViewById(R.id.themesText);

            edit = v.findViewById(R.id.edit2);
            delete = v.findViewById(R.id.delete2);
            favIcon = v.findViewById(R.id.favIcon);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journals, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        Journal j = list.get(pos);

        h.title.setText(j.title);

        h.content.setText(
                j.content != null ? j.content : "No content"
        );

        h.date.setText(j.date != null ? j.date : "");

        if (j.themes != null) {
            h.themes.setText("Themes: " + android.text.TextUtils.join(", ", j.themes));
        } else {
            h.themes.setText("Themes: none");
        }

        h.favIcon.setImageResource(
                j.isFavorite ?
                        R.drawable.favorite_filled :
                        R.drawable.favorite
        );

        h.delete.setOnClickListener(v -> {
            ref.child(j.id).removeValue();
            int position = h.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                list.remove(position);
                notifyItemRemoved(position);
            }
        });

        h.edit.setOnClickListener(v -> {

            android.view.LayoutInflater inflater =
                    android.view.LayoutInflater.from(v.getContext());

            View dialogView = inflater.inflate(R.layout.dialog_edit_journal, null);

            android.widget.EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
            android.widget.EditText inputContent = dialogView.findViewById(R.id.inputContent);
            android.widget.EditText inputThemes = dialogView.findViewById(R.id.inputThemes);
            android.widget.CheckBox inputFav = dialogView.findViewById(R.id.inputFav);

            inputTitle.setText(j.title);
            inputContent.setText(j.content);
            inputFav.setChecked(j.isFavorite);

            if (j.themes != null) {
                inputThemes.setText(android.text.TextUtils.join(", ", j.themes));
            }

            android.app.AlertDialog dialog =
                    new android.app.AlertDialog.Builder(v.getContext())
                            .setView(dialogView)
                            .setPositiveButton("Update", null)
                            .setNegativeButton("Cancel", null)
                            .create();

            dialog.setOnShowListener(d -> {

                android.widget.Button btn =
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);

                btn.setOnClickListener(view -> {

                    String newTitle = inputTitle.getText().toString();
                    String newContent = inputContent.getText().toString();
                    boolean fav = inputFav.isChecked();

                    String themeText = inputThemes.getText().toString();

                    java.util.List<String> themesList = new java.util.ArrayList<>();
                    if (!themeText.trim().isEmpty()) {
                        for (String t : themeText.split(",")) {
                            themesList.add(t.trim());
                        }
                    }

                    ref.child(j.id).child("title").setValue(newTitle);
                    ref.child(j.id).child("content").setValue(newContent);
                    ref.child(j.id).child("isFavorite").setValue(fav);

                    ref.child(j.id).child("themes").setValue(themesList);

                    dialog.dismiss();
                });
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
