package com.sia.android.wecare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Dashboard extends AppCompatActivity {

    MaterialButton btnProfile, btnComplaints, btnLogout;
    TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textEmail = findViewById(R.id.textEmail);
        btnProfile = findViewById(R.id.btnProfile);
        btnComplaints = findViewById(R.id.btnComplaints);
        btnLogout = findViewById(R.id.btnLogout);

        // Retrieve passed email from login (via intent)
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            textEmail.setText(email);
        }

        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnComplaints.setOnClickListener(v -> {
            Toast.makeText(this, "Complaint form not yet implemented", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Login.class); // Start Login activity
            startActivity(intent);
            finish(); // Finish the current activity (Dashboard)
        });
    }
}