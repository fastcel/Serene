package com.example.serene;

import android.content.Intent;
import android.content.SharedPreferences;
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

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        logo.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean("onboarding_done", false);

            Intent intent;

            if (!onboardingDone || false) { //Change this false to true to hardwire intro screens
                // First ever launch
                intent = new Intent(MainActivity.this, OnboardingScreen.class);
            } else {
                // Always go to login (your login handles auth check)
                intent = new Intent(MainActivity.this, Login.class);
            }

            startActivity(intent);
            finish();

        }, 3000);
    }
}