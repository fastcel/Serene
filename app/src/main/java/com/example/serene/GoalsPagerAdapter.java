package com.example.serene;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GoalsPagerAdapter extends FragmentStateAdapter {

    private AllGoalsFragment allGoalsFragment = new AllGoalsFragment();

    public GoalsPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    public GoalsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0: return allGoalsFragment;
            case 1: return new PendingGoalsFragment();
            case 2: return new CompletedGoalsFragment();
            case 3: return new OverdueGoalsFragment();
            default: return allGoalsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public AllGoalsFragment getAllGoalsFragment() {
        return allGoalsFragment;
    }
}