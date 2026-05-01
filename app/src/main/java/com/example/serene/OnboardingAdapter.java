package com.example.serene;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new IntroSlide1();
            case 1: return new IntroSlide2();
            default: return new IntroSlide3();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}