package com.example.serene;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GoalsFragment extends Fragment {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    TextView btnAddGoal;

    private GoalsPagerAdapter adapter;

    public GoalsFragment() {
        super(R.layout.fragment_goals);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        btnAddGoal = view.findViewById(R.id.btnAddGoal);

        adapter = new GoalsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("All");
                    else if (position == 1) tab.setText("Pending");
                    else if (position == 2) tab.setText("Completed");
                    else tab.setText("Overdue");
                }).attach();

        btnAddGoal.setOnClickListener(v -> {

            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0);
                Toast.makeText(getContext(), "Switching to All tab", Toast.LENGTH_SHORT).show();
                return;
            }

            Fragment f = adapter.getAllGoalsFragment();

            if (f instanceof AllGoalsFragment) {
                ((AllGoalsFragment) f).showAddGoalDialog();
            }
        });
    }
}