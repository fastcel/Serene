package com.example.serene;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.VH> {

    private List<Goal> list;
    private String uid;
    private DatabaseReference ref;

    public GoalsAdapter(List<Goal> list, String uid) {
        this.list = list;
        this.uid = uid;
        ref = FirebaseDatabase.getInstance()
                .getReference("users").child(uid).child("goals");
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title, status, priority, dateTime;
        Button edit, delete;

        VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            status = v.findViewById(R.id.status);
            priority = v.findViewById(R.id.priority);
            edit = v.findViewById(R.id.edit);
            delete = v.findViewById(R.id.delete);
            dateTime = v.findViewById(R.id.dateTime);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goals, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Goal g = list.get(pos);

        h.title.setText(g.getTitle());
        h.status.setText("Status: " + g.getStatus());
        h.priority.setText("Priority: " + g.getPriority());

        String dt = "";
        if (g.getDate() != null) dt += g.getDate();
        if (g.getTime() != null) dt += " at " + g.getTime();

        h.dateTime.setText(dt.isEmpty() ? "No date/time" : dt);

        h.delete.setOnClickListener(v -> {
            int position = h.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            ref.child(g.getId()).removeValue();
            list.remove(position);
            notifyItemRemoved(position);
        });

        h.edit.setOnClickListener(v -> {

            android.view.LayoutInflater inflater =
                    android.view.LayoutInflater.from(v.getContext());

            View dialogView = inflater.inflate(android.R.layout.simple_list_item_1, null);

            android.widget.LinearLayout layout = new android.widget.LinearLayout(v.getContext());
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(40, 20, 40, 10);

            android.widget.EditText title = new android.widget.EditText(v.getContext());
            title.setHint("Title");
            title.setText(g.getTitle());

            android.widget.EditText status = new android.widget.EditText(v.getContext());
            status.setHint("Status");
            status.setText(g.getStatus());

            android.widget.EditText priority = new android.widget.EditText(v.getContext());
            priority.setHint("Priority");
            priority.setText(g.getPriority());

            android.widget.EditText date = new android.widget.EditText(v.getContext());
            date.setHint("Date (dd/mm/yyyy)");
            date.setText(g.getDate());

            android.widget.EditText time = new android.widget.EditText(v.getContext());
            time.setHint("Time (hh:mm)");
            time.setText(g.getTime());

            layout.addView(title);
            layout.addView(status);
            layout.addView(priority);
            layout.addView(date);
            layout.addView(time);

            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Edit Goal")
                    .setView(layout)
                    .setPositiveButton("Update", (d, w) -> {

                        ref.child(g.getId()).child("title")
                                .setValue(title.getText().toString());

                        ref.child(g.getId()).child("status")
                                .setValue(status.getText().toString());

                        ref.child(g.getId()).child("priority")
                                .setValue(priority.getText().toString());

                        ref.child(g.getId()).child("date")
                                .setValue(date.getText().toString());

                        ref.child(g.getId()).child("time")
                                .setValue(time.getText().toString());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
