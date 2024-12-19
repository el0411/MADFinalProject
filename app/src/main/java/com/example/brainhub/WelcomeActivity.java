package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnNext;
    ImageView ivPhoto2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        btnNext = findViewById(R.id.btnNext);
        ivPhoto2 = findViewById(R.id.ivPhoto2);

        String username = getIntent().getStringExtra("USERNAME");

        tvWelcome.setText("Welcome, " + username + "!");


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String encodedImage = sharedPreferences.getString("USER_PHOTO", null);

        if (encodedImage != null) {

            Bitmap photo = decodeBase64ToBitmap(encodedImage);
            ivPhoto2.setImageBitmap(photo);
        }

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, Welcome2Activity.class);
            startActivity(intent);
        });
    }

    // Decode Base64 string to Bitmap
    private Bitmap decodeBase64ToBitmap(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
