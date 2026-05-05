package com.example.serene;

import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StreakManager {

    public interface Callback {
        void onUpdated(int streak);
    }

    public static void updateStreak(String uid, Callback callback) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("streak");

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int streak = 0;
                String lastDate = "";

                if (snapshot.exists()) {
                    Integer s = snapshot.child("streakCount").getValue(Integer.class);
                    String ld = snapshot.child("lastDate").getValue(String.class);

                    if (s != null) streak = s;
                    if (ld != null) lastDate = ld;
                }

                if (lastDate.equals(today)) {
                    callback.onUpdated(streak);
                    return;
                }

                String yesterday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(System.currentTimeMillis() - 86400000));

                if (lastDate.equals(yesterday)) {
                    streak++;
                } else {
                    streak = 1;
                }

                ref.child("streakCount").setValue(streak);
                ref.child("lastDate").setValue(today);

                callback.onUpdated(streak);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}