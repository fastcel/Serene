package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;



public class OnboardingScreen extends AppCompatActivity {

    ViewPager2 viewPager;
    LinearLayout dotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        viewPager.setAdapter(new OnboardingAdapter(this));

        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);

            }
        });


    }

    private void setupDots(int position) {
        dotsLayout.removeAllViews();

        for (int i = 0; i < 3; i++) {
            TextView dot = new TextView(this);
            dot.setText("•");
            dot.setTextSize(30);
            dot.setTextColor(i == position ? 0xFF6C63FF : 0x55FFFFFF);
            dotsLayout.addView(dot);
        }
    }
}