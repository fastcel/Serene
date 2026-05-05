package com.example.serene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import androidx.appcompat.widget.SwitchCompat;

public class HomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNav;
    ImageView speaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        initViews();
        loadAvatar();
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
        if (!MusicManager.isPlaying()) {
            MusicManager.play(this, R.raw.rain);
        }
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
                if (isEnabled()) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        speaker.setOnClickListener(v -> showMusicDialog());
    }
    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        speaker = findViewById(R.id.speakerIcon);
        bottomNav = findViewById(R.id.bottomNav);
        navigationView.setItemIconTintList(null);
        bottomNav.setItemIconTintList(null);
        setSelected(R.id.nav_home);
    }

    private void showMusicDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_music, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.TransparentDialog)
                .setView(dialogView)
                .create();
        TextView txtCurrent = dialogView.findViewById(R.id.txtCurrentTrack);
        SwitchCompat swMusic = dialogView.findViewById(R.id.switchMusic);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnChangeMusic = dialogView.findViewById(R.id.btnChangeMusic);
        boolean isPlaying = MusicManager.isPlaying();
        txtCurrent.setText("Currently playing: " + getTrackName(MusicManager.getCurrentTrack()));
        swMusic.setChecked(isPlaying);
        btnChangeMusic.setAlpha(isPlaying ? 1f : 0.4f);
        btnChangeMusic.setEnabled(isPlaying);
        swMusic.setOnCheckedChangeListener((btn, checked) -> {
            if (!checked) {
                MusicManager.stop();
                txtCurrent.setText("Currently playing: None");
                btnChangeMusic.setAlpha(0.4f);
                btnChangeMusic.setEnabled(false);
            } else {
                MusicManager.play(this, R.raw.rain);
                txtCurrent.setText("Currently playing: " + getTrackName(MusicManager.getCurrentTrack()));
                btnChangeMusic.setAlpha(1f);
                btnChangeMusic.setEnabled(true);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnChangeMusic.setOnClickListener(v -> {
            dialog.dismiss();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new MusicFragment())
                    .addToBackStack(null)
                    .commit();
        });

        dialog.show();
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }
    private String getTrackName(int track) {
        if (track == R.raw.rain)    return "Rain 🌧️";
        if (track == R.raw.ambient) return "Ambient 🌿";
        if (track == R.raw.piano)   return "Piano 🎹";
        if (track == R.raw.sad)     return "Sad 🌙";
        return "None";
    }

    private void setupListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
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
        } else if (id == R.id.nav_focus) {
            fragment = new FocusFragment();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            clearBottomSelection();
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
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            bottomNav.getMenu().getItem(i).setChecked(false);
        }
        bottomNav.setSelectedItemId(View.NO_ID);
    }

    public void navigateTo(int id) {
        if (navigationView.getCheckedItem() == null ||
                navigationView.getCheckedItem().getItemId() != id) {
            navigationView.setCheckedItem(id);
        }
        if (id != R.id.nav_settings) {
            if (bottomNav.getSelectedItemId() != id) {
                bottomNav.setSelectedItemId(id);
            }
        } else {
            clearBottomSelection();
            handleNavigation(id);
        }
    }
    private void loadAvatar() {
        View header = navigationView.getHeaderView(0);
        AvatarView avatarView = header.findViewById(R.id.imgAvatar);
        TextView txtName = header.findViewById(R.id.txtName);
        AvatarManager.loadInto(avatarView);
        UserManager.loadUsername(txtName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAvatar();
    }
}