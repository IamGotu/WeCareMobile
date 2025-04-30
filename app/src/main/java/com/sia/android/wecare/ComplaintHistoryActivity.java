package com.sia.android.wecare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class ComplaintHistoryActivity extends AppCompatActivity {

    private RecyclerView rvComplaintHistory;
    private RequestQueue requestQueue;
    private int userId;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);

        // For white status bar with dark icons
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuIcon = findViewById(R.id.menuIcon);
        toolbar = findViewById(R.id.toolbar);

        rvComplaintHistory = findViewById(R.id.rvComplaintHistory);
        rvComplaintHistory.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        // Get user ID from intent
        userId = getIntent().getIntExtra("id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Menu icon click listener
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(ComplaintHistoryActivity.this, Dashboard.class);
                intent.putExtra("id", userId);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_complaints) {
                Intent intent = new Intent(ComplaintHistoryActivity.this, ComplaintActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_history) {
                // Already on history page
                Toast.makeText(this, "You are on History", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(ComplaintHistoryActivity.this, Login.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadComplaintHistory();
    }

    private void loadComplaintHistory() {
        String url = "http://192.168.0.35/wecare_connection/history.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<Complaint> complaints = parseComplaints(response.getJSONArray("complaints"));
                        rvComplaintHistory.setAdapter(new ComplaintAdapter(complaints));
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading complaint history", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
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
                    obj.getString("created_at"),
                    obj.optString("priority", ""),
                    obj.optString("assigned_personnel", ""),
                    obj.optString("resolution_notes", ""),
                    obj.optString("resolved_at", "")
            ));
        }
        return complaints;
    }
}