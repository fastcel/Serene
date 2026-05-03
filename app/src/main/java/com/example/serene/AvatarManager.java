package com.example.serene;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AvatarManager {

    public static void loadInto(AvatarView avatarView) {

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null || avatarView == null) return;

        // optional: show default immediately
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
        });
    }
}