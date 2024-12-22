package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePost extends AppCompatActivity {
    private EditText titleBar, body;
    private Button postBtn;
    private ImageButton deleteBtn;
    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        titleBar = findViewById(R.id.titleBar);
        body = findViewById(R.id.body);
        postBtn = findViewById(R.id.postBtn);
        deleteBtn = findViewById(R.id.imageButton);
        username = findViewById(R.id.username);

        username.setText("Current User");
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
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean validatePost(String title, String content) {
        if (title.isEmpty() || title.equals("Title")) {
            showError("Please enter a title");
            return false;
        }

        if (content.isEmpty() || content.equals("Body")) {
            showError("Please enter content");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}