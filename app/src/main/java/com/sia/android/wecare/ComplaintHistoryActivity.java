package com.sia.android.wecare;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComplaintHistoryActivity extends AppCompatActivity {

    private RecyclerView rvComplaintHistory;
    private RequestQueue requestQueue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);

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
                    }
                },
                error -> Toast.makeText(this, "Error loading complaint history", Toast.LENGTH_SHORT).show()
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