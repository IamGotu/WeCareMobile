package com.sia.android.wecare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class Login extends AppCompatActivity {

    TextInputEditText txtEmail, txtPassword;
    MaterialTextView tv_error;
    MaterialButton btnLogin;

    String url_login = "http://192.168.0.35/wecare_connection/login.php";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        
        txtEmail = findViewById(R.id.emailEditText);
        txtPassword = findViewById(R.id.passwordEditText);
        tv_error = findViewById(R.id.registrationCard);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoLogin();

            }
        });

    }

    private void GoLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(email.isEmpty()){
            tv_error.setText("Enter Email");
        } else if(password.isEmpty()){
            tv_error.setText("Enter Password");
        } else {
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
        }

    }
}