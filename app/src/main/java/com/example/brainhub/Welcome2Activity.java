package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome2Activity extends AppCompatActivity {

    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);

        Button btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v->{
            Intent intent = new Intent(Welcome2Activity.this, WordOfTheDayActivity.class);
            startActivity(intent);
        });
    }
}
