package com.sia.android.wecare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class Dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    MaterialButton btnProfile, btnComplaints, btnLogout;
    TextView textEmail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Your original status bar code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            // Add this for status bar overlap fix
            window.getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
                int statusBarHeight = insets.getSystemWindowInsetTop();
                findViewById(R.id.toolbar).setPadding(0, statusBarHeight, 0, 0);
                return insets;
            });
        }

        // Initialize ALL your original components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuIcon = findViewById(R.id.menuIcon);
        btnProfile = findViewById(R.id.btnProfile);
        btnComplaints = findViewById(R.id.btnComplaints);
        btnLogout = findViewById(R.id.btnLogout);
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar (NEW)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Your original menu icon click listener
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Your original sidebar handling
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dashboard) {
                Toast.makeText(this, "You are on Dashboard", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_complaints) {
                Intent intent = new Intent(Dashboard.this, ComplaintActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_history) {
                Toast.makeText(this, "Complaint History coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(Dashboard.this, Login.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Your original button listeners
        btnProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show());

        btnComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ComplaintActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}