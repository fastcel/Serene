package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class IntroSlide3 extends Fragment {

    public IntroSlide3() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro_slide3, container, false);

        Button btn = view.findViewById(R.id.btnStartNow);
        btn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
        });

        return view;
    }
}