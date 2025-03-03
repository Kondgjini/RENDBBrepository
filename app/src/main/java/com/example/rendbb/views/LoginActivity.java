package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.utilities.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText txtUsername, txtPassword;
    private Button buttonLogin;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            // Initialize views
            txtUsername = findViewById(R.id.username);
            txtPassword = findViewById(R.id.password);
            buttonLogin = findViewById(R.id.login_button);

            // Initialize database helper and session manager
            dbHelper = new DatabaseHelper(this);
            session = new SessionManager(getApplicationContext());

            // Check if user is already logged in
            if (session.isLoggedIn()) {
                navigateToDashboard();
                return;
            }

            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        Log.e(TAG, "Error during login attempt", e);
                        Toast.makeText(LoginActivity.this,
                                "An error occurred during login",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this,
                    "Error initializing application",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void attemptLogin() {
        // Reset errors
        txtUsername.setError(null);
        txtPassword.setError(null);

        // Get values
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Validate
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            focusView = txtPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            txtUsername.setError("Username is required");
            focusView = txtUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            performLogin(username, password);
        }
    }

    private void performLogin(String username, String password) {
        try {
            Log.d(TAG, "Attempting login for user: " + username);

            if (dbHelper.authenticateUser(username, password)) {
                Log.d(TAG, "Login successful for user: " + username);

                // Create login session
                session.createLoginSession(username);

                // Navigate to dashboard
                navigateToDashboard();
            } else {
                Log.d(TAG, "Login failed for user: " + username);
                Toast.makeText(this,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            Toast.makeText(this,
                    "An error occurred during login",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}