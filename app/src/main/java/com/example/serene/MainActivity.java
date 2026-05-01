package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);

        // Load animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        logo.startAnimation(fadeIn);

        // Navigate to Onboarding after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, OnboardingScreen.class);
            startActivity(intent);
            finish(); // prevents going back to splash
        }, 2000); // 2 seconds splash duration
    }
}