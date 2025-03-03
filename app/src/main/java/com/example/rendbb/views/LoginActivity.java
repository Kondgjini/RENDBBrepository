package com.example.rendbb.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.utilities.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword;
    private Button buttonLogin;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, ManagerDashboardActivity.class));
            finish();
            return;
        }

        buttonLogin.setOnClickListener(v -> {
            String username = txtUsername.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(username)) {
                txtUsername.setError("Username is required");
                txtUsername.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                txtPassword.setError("Password is required");
                txtPassword.requestFocus();
                return;
            }

            if (dbHelper.authenticateUser(username, password)) {
                // Get user details
                Cursor cursor = dbHelper.getUserDetails(username);
                if (cursor.moveToFirst()) {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));

                    // Create login session
                    session.createLoginSession(id, username, email);

                    // Update last login time
                    dbHelper.updateLastLogin(username);
                }
                cursor.close();

                // Navigate to dashboard
                Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}