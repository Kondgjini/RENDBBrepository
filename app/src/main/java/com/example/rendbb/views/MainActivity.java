// MainActivity.java
package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rendbb.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    // UI elements
    TextView txtUsername, txtEmail;
    RadioGroup rgTheme, rgLanguage;
    Button btnSavePreferences, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Check if user is logged in
        if (!session.isLoggedIn()) {
            // User is not logged in, redirect to login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize UI elements
        txtUsername = findViewById(R.id.username_display);
        txtEmail = findViewById(R.id.email_display);
        rgTheme = findViewById(R.id.theme_radio_group);
        rgLanguage = findViewById(R.id.language_radio_group);
        btnSavePreferences = findViewById(R.id.save_preferences);
        btnLogout = findViewById(R.id.logout_button);

        // Get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_USERNAME);
        String email = user.get(SessionManager.KEY_EMAIL);

        // Display user data
        txtUsername.setText("Welcome, " + username + "!");
        txtEmail.setText(email);

        // Set preferences based on saved values
        String theme = session.getUserPreference("user_theme");
        if (theme != null) {
            if (theme.equals("light")) {
                rgTheme.check(R.id.theme_light);
            } else if (theme.equals("dark")) {
                rgTheme.check(R.id.theme_dark);
            }
        }

        String language = session.getUserPreference("user_language");
        if (language != null) {
            if (language.equals("en")) {
                rgLanguage.check(R.id.lang_english);
            } else if (language.equals("es")) {
                rgLanguage.check(R.id.lang_spanish);
            } else if (language.equals("fr")) {
                rgLanguage.check(R.id.lang_french);
            }
        }

        // Save preferences button click event
        btnSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTheme = "light";
                if (rgTheme.getCheckedRadioButtonId() == R.id.theme_dark) {
                    selectedTheme = "dark";
                }

                String selectedLanguage = "en";
                if (rgLanguage.getCheckedRadioButtonId() == R.id.lang_spanish) {
                    selectedLanguage = "es";
                } else if (rgLanguage.getCheckedRadioButtonId() == R.id.lang_french) {
                    selectedLanguage = "fr";
                }

                // Save preferences
                session.storeUserPreference("user_theme", selectedTheme);
                session.storeUserPreference("user_language", selectedLanguage);

                Toast.makeText(getApplicationContext(),
                        "Preferences saved",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data
                session.logoutUser();

                // Redirect to Login Activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}