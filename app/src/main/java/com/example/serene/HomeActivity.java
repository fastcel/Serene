package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        setupListeners();

        findViewById(R.id.drawerHandle).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );
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

            if (bottomNav.getSelectedItemId() != id) {
                bottomNav.setSelectedItemId(id);
                animateNavItem(id);
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

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_journal) {

        } else if (id == R.id.nav_goals) {

        } else if (id == R.id.nav_insights) {

        } else if (id == R.id.nav_focus) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HomeActivity.this, Login.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
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
}