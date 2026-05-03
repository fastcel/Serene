package com.example.serene;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class IntroSlide1 extends Fragment {

    public IntroSlide1() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro_slide1, container, false);

        Button btn = view.findViewById(R.id.btnStartNow);
        btn.setOnClickListener(v -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("onboarding_done", true).apply();
            startActivity(new Intent(getActivity(), Login.class));
        });

        return view;
    }
}