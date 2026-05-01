package com.example.serene;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GoalsFragment extends Fragment {

    ViewPager2 viewPager;
    TabLayout tabLayout;

    public GoalsFragment() {
        super(R.layout.fragment_goals);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        GoalsPagerAdapter adapter = new GoalsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("All");
                    else if (position == 1) tab.setText("Pending");
                    else if (position == 2) tab.setText("Completed");
                    else tab.setText("Overdue");
                }).attach();
    }
}