// LoginActivity.java
package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rendbb.R;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    // UI elements
    EditText txtUsername, txtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in
        if (session.isLoggedIn()) {
            // User is already logged in, redirect to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize UI elements
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login_button);

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check for empty fields
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    // In a real app, validate credentials against a database or API
                    // This is a simplified example

                    // Generate unique user ID (in production use database ID or API response)
                    String userId = UUID.randomUUID().toString();

                    // Create login session with dummy email
                    session.createLoginSession(userId, username, username + "@example.com");

                    // Store default preferences
                    session.storeUserPreference("user_theme", "light");
                    session.storeUserPreference("user_language", "en");

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Display error message
                    Toast.makeText(getApplicationContext(),
                            "Please enter username and password",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}