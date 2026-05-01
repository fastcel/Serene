package com.example.serene;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GoalsPagerAdapter extends FragmentStateAdapter {

    public GoalsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) return new AllGoalsFragment();
        if (position == 1) return new PendingGoalsFragment();
        if (position == 2) return new CompletedGoalsFragment();
        if (position == 3) return new OverdueGoalsFragment();
        return new AllGoalsFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
