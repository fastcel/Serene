package com.example.serene;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private List<String> userIds;
    public UserAdapter(List<User> userList, List<String> userIds) {
        this.userList = userList;
        this.userIds = userIds;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = userList.get(position);
        String uid = userIds.get(position);
        holder.tvUsername.setText(user.username);
        holder.tvEmail.setText(user.email);
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
        holder.btnDelete.setOnClickListener(v -> {
            userRef.removeValue();
            userList.remove(position);
            userIds.remove(position);
            notifyItemRemoved(position);
        });
        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Edit User");
            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_edit_user, null);
            EditText etUsername = dialogView.findViewById(R.id.etUsername);
            EditText etEmail = dialogView.findViewById(R.id.etEmail);
            etUsername.setText(user.username);
            etEmail.setText(user.email);
            builder.setView(dialogView);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String newUsername = etUsername.getText().toString().trim();
                String newEmail = etEmail.getText().toString().trim();
                userRef.child("username").setValue(newUsername);
                userRef.child("email").setValue(newEmail);
                user.username = newUsername;
                user.email = newEmail;
                notifyItemChanged(position);
                Toast.makeText(v.getContext(), "User updated", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail;
        Button btnEdit, btnDelete;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}