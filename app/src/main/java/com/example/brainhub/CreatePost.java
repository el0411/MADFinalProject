package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePost extends AppCompatActivity {
    private EditText titleBar, body;
    private Button postBtn;
    private ImageView deleteBtn;
    private TextView username;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initializeViews();
        setupClickListeners();
    }

    private Bitmap decodeBase64ToBitmap(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void initializeViews() {
        titleBar = findViewById(R.id.titleBar);
        body = findViewById(R.id.body);
        postBtn = findViewById(R.id.postBtn);
        deleteBtn = findViewById(R.id.imageButton);
        username = findViewById(R.id.username);
        imgView = findViewById(R.id.imageView5);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", "");
        username.setText(currentUser);

        String encodedImage = sharedPreferences.getString("USER_PHOTO", null);
        if (encodedImage != null) {
            Bitmap photo = decodeBase64ToBitmap(encodedImage);
            imgView.setImageBitmap(photo);
        }
    }

    private void setupClickListeners() {
        postBtn.setOnClickListener(v -> createPost());
        deleteBtn.setOnClickListener(v -> finish());
    }

    private void createPost() {
        String title = titleBar.getText().toString().trim();
        String content = body.getText().toString().trim();

        if (validatePost(title, content)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("content", content);
            resultIntent.putExtra("username", username.getText().toString());
            resultIntent.putExtra("timestamp", System.currentTimeMillis());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean validatePost(String title, String content) {
        if (title.isEmpty()) {
            showError("Please enter a title");
            return false;
        }
        if (content.isEmpty()) {
            showError("Please enter content");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}