package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Hardcoded credentials for demonstration
                String validUsername = "admin";
                String validPassword = "password";

                if (username.equals(validUsername) && password.equals(validPassword)) {
                    // If login is successful, navigate to ManagerDashboardActivity
                    Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Show login error
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}