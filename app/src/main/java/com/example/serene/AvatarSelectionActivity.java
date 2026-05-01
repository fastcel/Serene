package com.example.serene;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.serene.databinding.ActivityAvatarSelectionBinding;

public class AvatarSelectionActivity extends AppCompatActivity {

    private AvatarView avatarView;
    private int colorIndex = 0;
    private int faceIndex = 0;
    private int accessoryIndex = 0;

    private final String[] faces = {"Happy", "Neutral", "Wink", "Cool", "Surprised", "Sleepy"};
    private final String[] accessories = {"None", "Glasses", "Sunglasses", "Cap", "Headband", "Earrings"};
    private final int[] colors = {
            0xFFD4956A,
            0xFF5B4FCF,
            0xFFE95F8B,
            0xFF6FCF97,
            0xFFF2C94C
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);

        Button btnLetsGo = findViewById(R.id.btnLetsGo);
        Button btnSkip = findViewById(R.id.btnSkip);

        View.OnClickListener goToHome = v -> {
            Intent intent = new Intent(AvatarSelectionActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // optional: removes this screen from back stack
        };

        btnLetsGo.setOnClickListener(goToHome);
        btnSkip.setOnClickListener(goToHome);

        avatarView = findViewById(R.id.avatarView);
        setupRow(R.id.rowFace, faces, () -> {
            faceIndex = (faceIndex - 1 + faces.length) % faces.length;
        }, () -> {
            faceIndex = (faceIndex + 1) % faces.length;
        });
        setupRow(R.id.rowAccessory, accessories, () -> {
            accessoryIndex = (accessoryIndex - 1 + accessories.length) % accessories.length;
        }, () -> {
            accessoryIndex = (accessoryIndex + 1) % accessories.length;
        });
        setupRow(R.id.rowColor, null, () -> {
            colorIndex = (colorIndex - 1 + colors.length) % colors.length;
        }, () -> {
            colorIndex = (colorIndex + 1) % colors.length;
        });

        updateAvatar();
    }

    private void setupRow(int rowId, String[] values, Runnable onPrev, Runnable onNext) {
        View row = findViewById(rowId);
        TextView tvLabel = row.findViewById(R.id.tvLabel);
        TextView tvValue = row.findViewById(R.id.tvValue);
        View colorSwatch = row.findViewById(R.id.viewColorSwatch);


        if (rowId == R.id.rowFace) tvLabel.setText("Face");
        if (rowId == R.id.rowAccessory) tvLabel.setText("Style");
        if (rowId == R.id.rowColor) {
            tvLabel.setText("Color");
            tvValue.setVisibility(View.GONE);
            colorSwatch.setVisibility(View.VISIBLE);
        }

        // INITIAL VALUE
        updateRowValue(rowId, tvValue, colorSwatch);

        row.findViewById(R.id.btnPrev).setOnClickListener(v -> {
            onPrev.run();
            updateAvatar();
            updateRowValue(rowId, tvValue, colorSwatch);
        });

        row.findViewById(R.id.btnNext).setOnClickListener(v -> {
            onNext.run();
            updateAvatar();
            updateRowValue(rowId, tvValue, colorSwatch);
        });
    }

    private void updateRowValue(int rowId, TextView tvValue, View colorSwatch) {

        if (rowId == R.id.rowFace) {
            tvValue.setText(faces[faceIndex]);
        }

        if (rowId == R.id.rowAccessory) {
            tvValue.setText(accessories[accessoryIndex]);
        }

        if (rowId == R.id.rowColor) {
            colorSwatch.setBackgroundColor(colors[colorIndex]);
        }
    }
    private void updateAvatar() {
        avatarView.setAvatarConfig(
                accessories[accessoryIndex],
                faces[faceIndex],
                colors[colorIndex]
        );
    }
}