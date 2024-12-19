package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {


    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnNext = findViewById(R.id.btnNext);


        String username = getIntent().getStringExtra("USERNAME");


        tvWelcome.setText("Welcome, " + username + "!");

        btnNext.setOnClickListener(v->{
            Intent intent = new Intent(WelcomeActivity.this, Welcome2Activity.class);
            startActivity(intent);
        });
    }
}
