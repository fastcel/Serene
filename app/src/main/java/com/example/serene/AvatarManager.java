package com.example.serene;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AvatarManager {

    public interface Callback {
        void onLoaded();
    }

    public static void loadInto(AvatarView avatarView) {
        loadInto(avatarView, null);
    }

    public static void loadInto(AvatarView avatarView, Callback callback) {

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null || avatarView == null) {
            if (callback != null) callback.onLoaded();
            return;
        }

        // 🔥 loading state (safe default)
        avatarView.setAlpha(0.6f);
        avatarView.setAvatarConfig("Normal", "Smile", "None");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("avatar");

        ref.get().addOnSuccessListener(snapshot -> {

            if (snapshot.exists()) {

                String eyes = snapshot.child("eyes").getValue(String.class);
                String mouth = snapshot.child("mouth").getValue(String.class);
                String accessory = snapshot.child("accessory").getValue(String.class);

                avatarView.setAvatarConfig(
                        eyes != null ? eyes : "Normal",
                        mouth != null ? mouth : "Smile",
                        accessory != null ? accessory : "None"
                );
            }

            // ✅ end loading state
            avatarView.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();

            if (callback != null) callback.onLoaded();

        }).addOnFailureListener(e -> {

            // fallback end state
            avatarView.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();

            if (callback != null) callback.onLoaded();
        });
    }
}