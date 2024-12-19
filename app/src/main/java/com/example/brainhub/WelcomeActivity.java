package com.example.brainhub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView tvWelcome = findViewById(R.id.tvWelcome);

        // Retrieve the username passed from MainActivity
        String username = getIntent().getStringExtra("USERNAME");

        // Set the welcome message with the username
        tvWelcome.setText("Welcome, " + username + "!");
    }
}
