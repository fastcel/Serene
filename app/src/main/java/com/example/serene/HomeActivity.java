package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        initViews();

        findViewById(R.id.drawerHandle).bringToFront();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        setupListeners();
        handleNavigation(R.id.nav_home);
        setSelected(R.id.nav_home);

        findViewById(R.id.drawerHandle).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Fragment current = getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);

                if (current instanceof JournalFragment) {

                    FragmentManager child = current.getChildFragmentManager();

                    if (child.getBackStackEntryCount() > 0) {
                        child.popBackStack();
                        return;
                    }
                }

                // default behavior
                if (isEnabled()) {
                    setEnabled(false); // prevent infinite loop
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNav = findViewById(R.id.bottomNav);

        navigationView.setItemIconTintList(null);
        bottomNav.setItemIconTintList(null);

        setSelected(R.id.nav_home);
    }

    private void setupListeners() {

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            // avoid loops
            if (navigationView.getCheckedItem() == null ||
                    navigationView.getCheckedItem().getItemId() != id) {
                item.setChecked(true);
            }

            if (id != R.id.nav_settings) {
                if (bottomNav.getSelectedItemId() != id) {
                    bottomNav.setSelectedItemId(id);
                    animateNavItem(id);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            } else {
                clearBottomSelection();
            }

            handleNavigation(id);

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (navigationView.getCheckedItem() == null ||
                    navigationView.getCheckedItem().getItemId() != id) {
                navigationView.setCheckedItem(id);
            }

            handleNavigation(id);
            animateNavItem(id);

            return true;
        });

        bottomNav.setOnItemReselectedListener(item -> {
            // do nothing (prevents re-trigger bugs)
        });
    }

    private void handleNavigation(int id) {

        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();

        } else if (id == R.id.nav_journal) {
            fragment = new JournalFragment();

        } else if (id == R.id.nav_goals) {
            fragment = new GoalsFragment();

        } else if (id == R.id.nav_insights) {
            fragment = new InsightsFragment();

        } else if (id == R.id.nav_focus) {
            fragment = new FocusFragment();

        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            clearBottomSelection();

        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private void setSelected(int id) {

        if (navigationView.getCheckedItem() == null ||
                navigationView.getCheckedItem().getItemId() != id) {
            navigationView.setCheckedItem(id);
        }

        if (bottomNav.getSelectedItemId() != id) {
            bottomNav.setSelectedItemId(id);
        }
    }

    private void animateNavItem(int itemId) {

        View itemView = bottomNav.findViewById(itemId);
        if (itemView == null) return;

        ImageView icon = itemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);

        if (icon == null) return;

        icon.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(250)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> icon.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start())
                .start();
    }

    private void clearBottomSelection() {
        // clear visual
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            bottomNav.getMenu().getItem(i).setChecked(false);
        }

        // 🔥 reset internal state
        bottomNav.setSelectedItemId(View.NO_ID);
    }

    public void navigateTo(int id) {

        // sync drawer
        if (navigationView.getCheckedItem() == null ||
                navigationView.getCheckedItem().getItemId() != id) {
            navigationView.setCheckedItem(id);
        }

        // ⚠️ Only trigger bottom nav — DO NOT call handleNavigation manually
        if (id != R.id.nav_settings) {
            if (bottomNav.getSelectedItemId() != id) {
                bottomNav.setSelectedItemId(id); // this will trigger navigation
                return; // 🔥 STOP here
            }
        } else {
            clearBottomSelection();
            handleNavigation(id); // settings isn't in bottom nav
        }
    }
}