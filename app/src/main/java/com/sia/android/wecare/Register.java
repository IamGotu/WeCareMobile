package com.sia.android.wecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

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

        // Initialize EditText fields
        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText middleNameEditText = findViewById(R.id.middleNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        EditText addressEditText = findViewById(R.id.addressEditText);

        // Register button
        Button registerButton = findViewById(R.id.registerButton);

        // Handle button click to register
        registerButton.setOnClickListener(v -> {
            // Collect data from EditText fields
            String firstName = firstNameEditText.getText().toString().trim();
            String middleName = middleNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

            // Log the values (You can remove this later)
            Log.d("Register", "User Data: " + firstName + " " + middleName + " " + lastName + " " + email + " " + phoneNumber + " " + address);

            // Send this data to the server (we will do that part next)
        });
    }
}