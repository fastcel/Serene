package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

    private List<Journal> list;
    public interface OnJournalClickListener {
        void onJournalClick(Journal journal);
    }
    private OnJournalClickListener listener;
    public JournalAdapter(List<Journal> list, OnJournalClickListener listener) {
        this.list = list;
        this.listener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, date;
        ImageView btnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            date = itemView.findViewById(R.id.tvDate);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Journal j = list.get(position);

        holder.title.setText(j.title != null ? j.title : "No Title");
        holder.date.setText(j.date != null ? j.date : "");

        if (j.isFavorite) {
            holder.btnFavorite.setImageResource(R.drawable.favorite_filled);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.favorite);
        }

        // open detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJournalClick(j);
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {

            j.isFavorite = !j.isFavorite;

            // instant UI update
            if (j.isFavorite) {
                holder.btnFavorite.setImageResource(R.drawable.favorite_filled);
            } else {
                holder.btnFavorite.setImageResource(R.drawable.favorite);
            }

            String userId = FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("journals")
                    .child(j.id);

            ref.child("isFavorite").setValue(j.isFavorite);
        });
    }

    public void updateList(List<Journal> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}