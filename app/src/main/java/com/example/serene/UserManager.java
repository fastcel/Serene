package com.example.serene;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserManager {

    public static void loadUsername(TextView textView) {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || textView == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("username");

        ref.get().addOnSuccessListener(snapshot -> {

            String username = snapshot.getValue(String.class);

            if (username != null && !username.isEmpty()) {
                textView.setText(username);
            } else {
                textView.setText("User");
            }
        });
    }
}