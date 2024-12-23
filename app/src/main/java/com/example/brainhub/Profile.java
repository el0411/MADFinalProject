package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private EditText editUsername, editFName, editLName, editEmail;
    private Button updBtn, logoutBtn;
    private ImageView profilePic;
    private TextView currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        editUsername = findViewById(R.id.editUsername);
        editFName = findViewById(R.id.editFName);
        editLName = findViewById(R.id.editLName);
        editEmail = findViewById(R.id.editEmail);
        updBtn = findViewById(R.id.updBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        profilePic = findViewById(R.id.imageView2);
        currentUser = findViewById(R.id.currentUser);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editUsername.setText(prefs.getString("USERNAME", ""));
        editFName.setText(prefs.getString("FIRST_NAME", ""));
        editLName.setText(prefs.getString("LAST_NAME", ""));
        editEmail.setText(prefs.getString("EMAIL", ""));
        currentUser.setText(prefs.getString("USERNAME", ""));

        String encodedImage = prefs.getString("USER_PHOTO", null);
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profilePic.setImageBitmap(photo);
        }
    }

    private void setupClickListeners() {
        updBtn.setOnClickListener(v -> updateProfile());
        logoutBtn.setOnClickListener(v -> logout());
        profilePic.setOnClickListener(v -> openCamera());
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                profilePic.setImageBitmap(photo);

                String encodedImage = encodeBitmapToBase64(photo);
                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putString("USER_PHOTO", encodedImage);
                editor.apply();
            }
        }
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updateProfile() {
        String username = editUsername.getText().toString().trim();
        String firstName = editFName.getText().toString().trim();
        String lastName = editLName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (validateInputs(username, firstName, lastName, email)) {
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.putString("USERNAME", username);
            editor.putString("FIRST_NAME", firstName);
            editor.putString("LAST_NAME", lastName);
            editor.putString("EMAIL", email);
            editor.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            currentUser.setText(username);
        }
    }

    private boolean validateInputs(String username, String firstName, String lastName, String email) {
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}