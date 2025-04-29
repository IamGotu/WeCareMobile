package com.sia.android.wecare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    private int residentId; // Get this from your session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewComplaints = findViewById(R.id.btnViewComplaints);
        rvComplaints = findViewById(R.id.rvComplaints);

        // Set up RecyclerView
        complaintAdapter = new ComplaintAdapter(complaintList);
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvComplaints.setAdapter(complaintAdapter);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get resident ID from session
        residentId = getResidentIdFromSession();

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
            jsonBody.put("resident_id", residentId); // Get this from your session
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

    private int getResidentIdFromSession() {
        // Implement your session management here
        // This should return the logged-in resident's ID
        return 1; // Replace with actual session logic
    }
}