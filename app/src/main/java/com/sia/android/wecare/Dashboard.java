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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    TextView textEmail;
    Toolbar toolbar;

    // New dashboard elements
    private TextView tvTotalComplaints, tvPendingComplaints, tvResolvedThisMonth, tvInProgressComplaints;
    private RecyclerView rvActiveComplaints, rvResolvedComplaints;
    private RequestQueue requestQueue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
                int statusBarHeight = insets.getSystemWindowInsetTop();
                findViewById(R.id.toolbar).setPadding(0, statusBarHeight, 0, 0);
                return insets;
            });
        }

        // Initialize views (original + new)
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuIcon = findViewById(R.id.menuIcon);
        toolbar = findViewById(R.id.toolbar);

        // New dashboard views
        tvTotalComplaints = findViewById(R.id.tvTotalComplaints);
        tvPendingComplaints = findViewById(R.id.tvPendingComplaints);
        tvResolvedThisMonth = findViewById(R.id.tvResolvedThisMonth);
        tvInProgressComplaints = findViewById(R.id.tvInProgressComplaints);
        rvActiveComplaints = findViewById(R.id.rvActiveComplaints);
        rvResolvedComplaints = findViewById(R.id.rvResolvedComplaints);

        requestQueue = Volley.newRequestQueue(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Get user ID from intent
        userId = getIntent().getIntExtra("id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerViews
        rvActiveComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvResolvedComplaints.setLayoutManager(new LinearLayoutManager(this));

        // Original menu icon click listener
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Original sidebar handling (unchanged)
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dashboard) {
                Toast.makeText(this, "You are on Dashboard", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_complaints) {
                if (userId == -1) {
                    Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(Dashboard.this, ComplaintActivity.class);
                intent.putExtra("id", userId);
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

        // Load dashboard data
        loadDashboardData();
    }

    private void loadDashboardData() {
        String url = "http://192.168.0.35/wecare_connection/dashboard.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Update statistics
                        JSONObject stats = response.getJSONObject("statistics");
                        tvTotalComplaints.setText(stats.getString("total_complaints"));
                        tvPendingComplaints.setText(stats.getString("pending_complaints"));
                        tvResolvedThisMonth.setText(stats.getString("resolved_this_month"));
                        tvInProgressComplaints.setText(stats.getString("in_progress_complaints"));

                        // Update active complaints
                        List<Complaint> activeComplaints = parseComplaints(response.getJSONArray("active_complaints"));
                        rvActiveComplaints.setAdapter(new ComplaintAdapter(activeComplaints));

                        // Update resolved complaints
                        List<Complaint> resolvedComplaints = parseComplaints(response.getJSONArray("resolved_complaints"));
                        rvResolvedComplaints.setAdapter(new ComplaintAdapter(resolvedComplaints));

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading dashboard", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private List<Complaint> parseComplaints(JSONArray jsonArray) throws JSONException {
        List<Complaint> complaints = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            complaints.add(new Complaint(
                    obj.getInt("id"),
                    obj.getString("title"),
                    obj.getString("description"),
                    obj.getString("status"),
                    obj.getString("created_at")
                    // Add resolved_at if needed
            ));
        }
        return complaints;
    }
}