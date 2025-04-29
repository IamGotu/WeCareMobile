package com.sia.android.wecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText txtfirst_name, txtmiddle_name, txtlast_name, txtphone_number,
            txtAddress, txtEmail, txtPassword;
    MaterialButton btnRegister;

    String url_register = "http://192.168.0.35/wecare_connection/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView registerTextView = findViewById(R.id.loginTextView);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        txtfirst_name = findViewById(R.id.firstNameEditText);
        txtmiddle_name = findViewById(R.id.middleNameEditText);
        txtlast_name = findViewById(R.id.lastNameEditText);
        txtphone_number = findViewById(R.id.phoneNumberEditText);
        txtAddress = findViewById(R.id.addressEditText);
        txtEmail = findViewById(R.id.emailEditText);
        txtPassword = findViewById(R.id.passwordEditText);
        btnRegister = findViewById(R.id.registerButton);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRegister();
            }
        });
    }

    private void GoRegister() {

        ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("{Loading...");
        String first_name = txtfirst_name.getText().toString().trim();
        String middle_name = txtmiddle_name.getText().toString().trim();
        String last_name = txtlast_name.getText().toString().trim();
        String phone_number = txtphone_number.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (first_name.isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
        } else if (last_name.isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
        } else if (phone_number.isEmpty()) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        } else if (address.isEmpty()) {
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            //tv_success.setText("Register Success");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject sonObject = new JSONObject(response);
                        String success = sonObject.getString("success");
                        String message = sonObject.getString("message");

                        if (success.equals("1")) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Register.this, error.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("first_name", first_name);
                    params.put("middle_name", middle_name);
                    params.put("last_name", last_name);
                    params.put("phone_number", phone_number);
                    params.put("address", address);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
}