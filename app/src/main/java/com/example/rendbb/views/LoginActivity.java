package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
            Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
            startActivity(intent);
            finish();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.authenticateUser(username, password)) {
                    session.createLoginSession(username);
                    Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}