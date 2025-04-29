package com.sia.android.wecare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Button btnSubmit, btnViewComplaints;
    private RecyclerView rvComplaints;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> complaintList = new ArrayList<>();
    private RequestQueue requestQueue;
    private int residentId;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

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

        // Get resident ID from Intent
        residentId = getIntent().getIntExtra("id", -1);
        if (residentId == -1) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewComplaints = findViewById(R.id.btnViewComplaints);
        rvComplaints = findViewById(R.id.rvComplaints);
        // Initialize navigation components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        menuIcon = findViewById(R.id.menuIcon); // Make sure this exists in your toolbar

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dashboard) {
                int userId = getIntent().getIntExtra("id", -1);
                if (userId == -1) {
                    Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(ComplaintActivity.this, Dashboard.class);
                intent.putExtra("id", userId); // Pass the ID forward
                startActivity(intent);
            } else if (id == R.id.nav_complaints) {
                Toast.makeText(this, "You are on Complaint Page", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_history) {
                Toast.makeText(this, "Complaint History coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(ComplaintActivity.this, Login.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up RecyclerView
        complaintAdapter = new ComplaintAdapter(complaintList);
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvComplaints.setAdapter(complaintAdapter);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Button listeners
        btnSubmit.setOnClickListener(v -> submitComplaint());
        btnViewComplaints.setOnClickListener(v -> loadComplaints());

        // Load complaints initially
        loadComplaints();
    }

    private void submitComplaint() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.0.35/wecare_connection/complaints_api.php";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("resident_id", residentId);
            jsonBody.put("title", title);
            jsonBody.put("description", description);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                etTitle.setText("");
                                etDescription.setText("");
                                loadComplaints();
                            } else {
                                Toast.makeText(this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            try {
                                String errorMsg = new String(error.networkResponse.data, "UTF-8");
                                Toast.makeText(this, "Server error: " + errorMsg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadComplaints() {
        String url = "http://192.168.0.35/wecare_connection/complaints_api.php?resident_id=" + residentId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    complaintList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject complaintJson = response.getJSONObject(i);
                            Complaint complaint = new Complaint(
                                    complaintJson.getInt("id"),
                                    complaintJson.getString("title"),
                                    complaintJson.getString("description"),
                                    complaintJson.getString("status"),
                                    complaintJson.getString("created_at")
                            );
                            complaintList.add(complaint);
                        }
                        complaintAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load complaints", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}