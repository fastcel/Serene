package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);

        // Animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        logo.startAnimation(fadeIn);

        // Delay
        new Handler().postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Intent intent;

            if (user != null) {
                intent = new Intent(MainActivity.this, Login.class);
            } else {
                intent = new Intent(MainActivity.this, OnboardingScreen.class);
            }

            startActivity(intent);
            finish();

        }, 3000);
    }
}