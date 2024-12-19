package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    EditText username, password, firstName, lastName, email;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.Email);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();
                String enteredFirstName = firstName.getText().toString().trim();
                String enteredLastName = lastName.getText().toString().trim();
                String enteredEmail = email.getText().toString().trim();

                if (enteredUsername.isEmpty() || enteredPassword.isEmpty() || enteredFirstName.isEmpty() ||
                        enteredLastName.isEmpty() || enteredEmail.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save user data to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERNAME", enteredUsername);
                    editor.putString("PASSWORD", enteredPassword);
                    editor.putString("FIRST_NAME", enteredFirstName);
                    editor.putString("LAST_NAME", enteredLastName);
                    editor.putString("EMAIL", enteredEmail);
                    editor.apply();

                    // After registration, redirect to MainActivity for login
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close RegistrationActivity
                }
            }
        });
    }
}
