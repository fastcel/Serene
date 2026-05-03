package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AvatarSelectionActivity extends AppCompatActivity {
    private AvatarView avatarView;
    private boolean isEditMode = false;
    private int eyesIndex = 1;
    private int mouthIndex = 0;
    private int accessoryIndex = 0;
    private final String[] eyesList = {
            "Happy", "Normal", "Sparkle", "Surprised", "Wink"
    };
    private final String[] mouthList = {
            "Cute", "Neutral", "Sad", "Surprised", "Smile"
    };
    private final String[] accessories = {
            "None", "Glasses", "Sunglasses", "Cap", "Headband", "Earrings", "Beanie", "Bow"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);
        isEditMode = getIntent().getBooleanExtra("isEdit", false);
        avatarView = findViewById(R.id.avatarView);
        Button btnLetsGo = findViewById(R.id.btnLetsGo);
        Button btnSkip = findViewById(R.id.btnSkip);
        View.OnClickListener goToHome = v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("avatar");
            ref.child("eyes").setValue(eyesList[eyesIndex]);
            ref.child("mouth").setValue(mouthList[mouthIndex]);
            ref.child("accessory").setValue(accessories[accessoryIndex]);
            if (isEditMode) {
                finish();
            } else {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        };
        btnLetsGo.setOnClickListener(goToHome);
        btnSkip.setOnClickListener(goToHome);
        setupRow(R.id.rowEyes,
                () -> eyesIndex = (eyesIndex - 1 + eyesList.length) % eyesList.length,
                () -> eyesIndex = (eyesIndex + 1) % eyesList.length
        );
        setupRow(R.id.rowMouth,
                () -> mouthIndex = (mouthIndex - 1 + mouthList.length) % mouthList.length,
                () -> mouthIndex = (mouthIndex + 1) % mouthList.length
        );
        setupRow(R.id.rowAccessory,
                () -> accessoryIndex = (accessoryIndex - 1 + accessories.length) % accessories.length,
                () -> accessoryIndex = (accessoryIndex + 1) % accessories.length
        );
        if (isEditMode) {
            loadExistingAvatar();
            btnLetsGo.setText("Save");
            btnSkip.setVisibility(View.GONE);
        }

        updateAvatar();
    }
    private void setupRow(int rowId, Runnable onPrev, Runnable onNext) {

        View row = findViewById(rowId);
        TextView tvValue = row.findViewById(R.id.tvValue);
        TextView tvLabel = row.findViewById(R.id.tvLabel);
        if (rowId == R.id.rowEyes) tvLabel.setText("Eyes");
        if (rowId == R.id.rowMouth) tvLabel.setText("Mouth");
        if (rowId == R.id.rowAccessory) tvLabel.setText("Accessory");
        updateRowValue(rowId, tvValue);
        row.findViewById(R.id.btnPrev).setOnClickListener(v -> {
            onPrev.run();
            updateAvatar();
            updateRowValue(rowId, tvValue);
        });
        row.findViewById(R.id.btnNext).setOnClickListener(v -> {
            onNext.run();
            updateAvatar();
            updateRowValue(rowId, tvValue);
        });
    }
    private void updateRowValue(int rowId, TextView tvValue) {
        if (rowId == R.id.rowEyes) {
            tvValue.setText(eyesList[eyesIndex]);
        }
        if (rowId == R.id.rowMouth) {
            tvValue.setText(mouthList[mouthIndex]);
        }
        if (rowId == R.id.rowAccessory) {
            tvValue.setText(accessories[accessoryIndex]);
        }
    }
    private void updateAvatar() {
        avatarView.setAvatarConfig(
                eyesList[eyesIndex],
                mouthList[mouthIndex],
                accessories[accessoryIndex]
        );
    }
    private void loadExistingAvatar() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("avatar");
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String eyes = snapshot.child("eyes").getValue(String.class);
                String mouth = snapshot.child("mouth").getValue(String.class);
                String accessory = snapshot.child("accessory").getValue(String.class);
                eyesIndex = findIndex(eyesList, eyes, 1);
                mouthIndex = findIndex(mouthList, mouth, 0);
                accessoryIndex = findIndex(accessories, accessory, 0);
                updateAvatar();
                updateRowValue(R.id.rowEyes, findViewById(R.id.rowEyes).findViewById(R.id.tvValue));
                updateRowValue(R.id.rowMouth, findViewById(R.id.rowMouth).findViewById(R.id.tvValue));
                updateRowValue(R.id.rowAccessory, findViewById(R.id.rowAccessory).findViewById(R.id.tvValue));
            }
        });
    }
    private int findIndex(String[] array, String value, int defaultIndex) {
        if (value == null) return defaultIndex;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return defaultIndex;
    }
}